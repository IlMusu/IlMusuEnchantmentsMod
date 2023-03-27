package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.PlayerFovMultiplierCallback;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.mixins.interfaces._IPlayerTickers;
import com.ilmusu.musuen.networking.messages.PhasingSwitchMessage;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import com.ilmusu.musuen.utils.raycasting.ModRaycast;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class PhasingEnchantment extends Enchantment implements _IDemonicEnchantment
{
    private static final String PHASING_TAG = Resources.MOD_ID+".is_phasing";
    private static final int TICK_DELAY_BETWEEN_PHASING = 10;

    public static float clientTargetFov = 1.0F;

    public PhasingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 5);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer()
    {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection()
    {
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean onPhasingKeyBindingPress(PlayerEntity player, int modifiers)
    {
        long lastPhaseTick = ((_IEntityPersistentNbt)player).getPNBT().getLong(PHASING_TAG);
        if(player.world.getTime() - lastPhaseTick <= TICK_DELAY_BETWEEN_PHASING)
            return false;

        // The phasing enchantment must be present on the legs
        int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.PHASING, player);
        if(level == 0)
            return false;

        // The maximum distance the player can phase to
        float distance = level * 5.0F;

        // If there was no block on the path of the ray, simply teleport the player there
        BlockHitResult hitResult = ModRaycast.raycastFullBlock(player, 0.0F, distance);
        if(hitResult.getType() == HitResult.Type.MISS)
            return performPlayerPhasing(player, ModEnchantments.PHASING, hitResult, level);

        Box box = player.getBoundingBox(player.getPose()).expand(0.3F, 0.0F, 0.3F);
        box = box.withMinY(box.minY+0.6F);
        float travelled = ModRaycast.computeTravelDistance(player, hitResult);
        HitResult phaseResult = ModRaycast.raycastBox(player, box, travelled+0.5F, distance);

        // If it is not possible to phase, teleport to the looked block
        if(phaseResult == null)
            return performPlayerPhasing(player, ModEnchantments.PHASING, offsetFromDir(hitResult), level);

        // This is the actual phasing
        return performPlayerPhasing(player, ModEnchantments.PHASING, phaseResult, level);
    }

    private static boolean performPlayerPhasing(PlayerEntity player, Enchantment ench, HitResult result, int level)
    {
        // Modifies the target position to make it more suitable
        Vec3d target = result.getPos();
        if(player.world.getBlockState(BlockPos.ofFloored(target)).getMaterial().blocksMovement())
            target = new Vec3d(target.getX(), ((int)target.getY())+1, target.getZ());

        // Prevents the teleport if the position is out of world
        float preDistance = (float)player.getPos().distanceTo(target);
        if(player.world.isOutOfHeightLimit(BlockPos.ofFloored(target)) || preDistance < 2.0F)
        {
            float pitch = ModUtils.range(player.world.getRandom(), 0.5F, 0.7F);
            player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_BLAZE_DEATH, SoundCategory.PLAYERS, 1.0F, pitch);
            return false;
        }
        
        Vec3d finalTarget = target;
        int fovEffectTime = 13;
        ((_IPlayerTickers)player).addTicker(new _IPlayerTickers.Ticker(fovEffectTime)
            .onEntering((ticker) ->
            {
                // The player starts to phase, registering in the nbt
                ((_IEntityPersistentNbt)player).getPNBT().putLong(PHASING_TAG, player.world.getTime()+fovEffectTime);
                // Sending the message for updating the player fov
                new PhasingSwitchMessage(true).sendToClient((ServerPlayerEntity) player);

                Vec3d start = player.getEyePos();
                Vec3d direction = finalTarget.subtract(start).normalize();
                float distance = Math.min((float)finalTarget.subtract(start).length(), 3.0F);
                for(float i=0; i<distance; i+=ModUtils.range(player.getRandom(), 0.7F, 1.1F))
                {
                    Vec3d pos = start.add(direction.multiply(i));
                    ((ServerWorld)player.world).spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            })
            .onExiting((ticker) ->
            {
                // Teleporting player
                player.requestTeleport(finalTarget.x, finalTarget.getY(), finalTarget.getZ());
                float pitch = ModUtils.range(player.world.getRandom(), 0.8F, 1.2F);
                player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.0F, pitch);

                // Sending the message for updating the player fov
                new PhasingSwitchMessage(false).sendToClient((ServerPlayerEntity) player);

                // Consuming health
                float percentage = new ModUtils.Linear(ench.getMinLevel(), 0.2F, ench.getMaxLevel(), 0.3F).of(level);
                _IDemonicEnchantment.consumeHealthValue(player, percentage, false);
            })
        );

        return true;
    }

    private static BlockHitResult offsetFromDir(BlockHitResult result)
    {
        if(result.getSide() == Direction.UP)
        {
            Vec3d pos = new Vec3d(result.getPos().x, result.getBlockPos().getY()+1, result.getPos().z);
            return new BlockHitResult(pos, result.getSide(), BlockPos.ofFloored(pos), true);
        }
        if(result.getSide() == Direction.DOWN)
        {
            Vec3d pos = new Vec3d(result.getPos().x, result.getBlockPos().getY()-2.5F, result.getPos().z);
            return new BlockHitResult(pos, result.getSide(), BlockPos.ofFloored(pos), true);
        }

        Vector3f offset = result.getSide().getUnitVector();
        Vec3d pos = result.getPos().add(0, -1, 0).add(new Vec3d(offset));
        return new BlockHitResult(pos, result.getSide(), BlockPos.ofFloored(pos), true);
    }

    public static void onClientPlayerPhasing(boolean enter)
    {
        PhasingEnchantment.clientTargetFov = enter ? 1.8F : 1.0F;
    }

    static
    {
        PlayerFovMultiplierCallback.AFTER.register(((player) ->
        {
            if(PhasingEnchantment.clientTargetFov == 1.0F)
                return PlayerFovMultiplierCallback.FovParams.UNCHANGED;

            return new PlayerFovMultiplierCallback.FovParams(PhasingEnchantment.clientTargetFov)
                    .unclamped().velocity(0.35F);
        }));

        LootTableEvents.MODIFY.register(((resourceManager, lootManager, id, builder, source) ->
        {
            if(!id.equals(LootTables.ANCIENT_CITY_CHEST))
                return;

            // Adding enchanted book to ancient city loot table3
            builder.pool(LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(Items.BOOK)
                    .weight(5)
                    .apply(EnchantRandomlyLootFunction.create().add(ModEnchantments.PHASING)))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
                .with(EmptyEntry.builder()
                    .weight(10))
                .build());
        }));
    }
}
