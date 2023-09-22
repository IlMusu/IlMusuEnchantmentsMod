package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.HudRenderCallback;
import com.ilmusu.musuen.callbacks.PlayerAttackCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.networking.messages.BerserkOverlayMessage;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BerserkerEnchantment extends DamageEnchantment implements _IDemonicEnchantment, _IEnchantmentExtensions
{
    private static final String BERSERK_TAG = Resources.MOD_ID+".berserk";
    private static final String BERSERK_DAMAGE_TAG = Resources.MOD_ID+".berserk_damage";
    private static final String BERSERK_TIME_TAG = Resources.MOD_ID+".berserk_time";

    private static ModUtils.Linear berserkOverlay;
    private static float berserkOverlayTime;

    public BerserkerEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, 0, EquipmentSlot.MAINHAND);
        ((_IEnchantmentLevels)this).setConfigurationLevels(minLevel, maxLevel);
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public int getMinLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMinLevel();
    }

    @Override
    public int getMaxLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMaxLevel();
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer()
    {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection()
    {
        return !ModConfigurations.isDemonicEnchantingEnabled();
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group)
    {
        // The base attack damage for this enchantment is 0.0F
        return 0.0F;
    }

    @Override
    public float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        if(!(stack.hasNbt()) || !stack.getNbt().contains(BERSERK_TAG))
            return 0.0F;
        return ((NbtCompound)stack.getNbt().get(BERSERK_TAG)).getFloat(BERSERK_DAMAGE_TAG);
    }

    public float getDamageForHealthConsumed(float health, float level)
    {
        return health + level*0.2F;
    }

    static
    {
        PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.register((entity, stack, attacked, hand) ->
        {
            if (!(entity instanceof LivingEntity living) || entity.world.isClient)
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.BERSERKER, stack);
            if(level == 0)
                return;

            NbtCompound nbt = stack.getOrCreateSubNbt(BERSERK_TAG);
            long time = nbt.getLong(BERSERK_TIME_TAG);
            long dTime = living.world.getTime() - time;
            long maxDTime = (long) (20*(2+level*0.5));

            // The base additional damage is the previous one if not much time has passed
            float additionalDamage = (dTime > 0 && dTime < maxDTime) ? nbt.getFloat(BERSERK_DAMAGE_TAG) : 0.0F;
            float maxAdditionalDamage = 5.0F*level;

            // The percentage of health to consume at the current level of the enchantment
            // Not using the min and max levels so that the enchantment is able to scale
            float percentage = new ModUtils.Linear(1, 0.05F, 5, 0.10F).of(level);
            // Getting the amount of actually consumed health and the related damage
            float consumed = _IDemonicEnchantment.consumeHealthValue(living, percentage, false);
            additionalDamage += ((BerserkerEnchantment)ModEnchantments.BERSERKER).getDamageForHealthConsumed(consumed, level);
            additionalDamage = Math.min(additionalDamage, maxAdditionalDamage);

            // Sending the message for the overlay
            if(living instanceof PlayerEntity player)
                new BerserkOverlayMessage((int)maxDTime).sendToClient((ServerPlayerEntity)player);

            // Setting the additional damage before attacking the entity
            nbt.putLong(BERSERK_TIME_TAG, living.world.getTime());
            nbt.putFloat(BERSERK_DAMAGE_TAG, additionalDamage);
            stack.getOrCreateNbt().put(BERSERK_TAG, nbt);
        });
    }

    @Environment(EnvType.CLIENT)
    public static class BerserkerOverlayRendering
    {
        public static void register()
        {
            HudRenderCallback.AFTER_OVERLAYS.register(((matrixStack, tickDelta) ->
            {
                if(berserkOverlayTime <= 0)
                    return;

                // Getting the new opacity and decreasing time
                float opacity = berserkOverlay.of(berserkOverlayTime);
                berserkOverlayTime -= tickDelta;

                // Rendering outline with the opacity
                renderBerserkOverlay(Resources.BERSERKER_OUTLINE_TEXTURE, opacity);

                if(berserkOverlayTime <= 0)
                {
                    // Playing a sound when the berserker effect expires
                    PlayerEntity player = MinecraftClient.getInstance().player;
                    Vec3d pos = player.getPos();
                    float pitch = ModUtils.range(player.getRandom(), 0.6F, 0.8F);
                    player.world.playSound(pos.x, pos.y, pos.z, SoundEvents.ENTITY_IRON_GOLEM_DEATH, SoundCategory.NEUTRAL, 0.6F, pitch, false);
                }
            }));
        }

        public static void setBerserkOverlay(int duration)
        {
            berserkOverlay = new ModUtils.Linear(0, 0.1F, duration, 0.7F);
            berserkOverlayTime = duration+10.0F;
        }

        private static void renderBerserkOverlay(Identifier texture, float opacity)
        {
            float scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
            float scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
            RenderSystem.setShaderTexture(0, texture);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(0.0, scaledHeight, -90.0).texture(0.0f, 1.0f).next();
            bufferBuilder.vertex(scaledWidth, scaledHeight, -90.0).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
            tessellator.draw();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
