package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.mixins.interfaces._IPlayerTickers;
import com.ilmusu.musuen.networking.messages.PhasingSwitchMessage;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import com.ilmusu.musuen.utils.raycasting.ModRaycast;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

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
    public boolean isAvailableForEnchantedBookOffer()
    {
        return false;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    public static boolean onPhasingKeyBindingPress(PlayerEntity player, int modifiers)
    {
        long lastPhaseTick = ((_IEntityPersistentNbt)player).get().getLong(PHASING_TAG);
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
        if(player.world.getBlockState(new BlockPos(target)).getMaterial().blocksMovement())
            target = new Vec3d(target.getX(), ((int)target.getY())+1, target.getZ());

        // Prevents the teleport if the position is out of world
        float preDistance = (float)player.getPos().distanceTo(target);
        if(player.world.isOutOfHeightLimit(new BlockPos(target)) || preDistance < 2.0F)
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
                ((_IEntityPersistentNbt)player).get().putLong(PHASING_TAG, player.world.getTime()+fovEffectTime);
                // Sending the message for updating the player fov
                new PhasingSwitchMessage(true).sendToClient((ServerPlayerEntity) player);
            })
            .onExiting((ticker) ->
            {
                // Teleporting player
                player.requestTeleport(finalTarget.x, finalTarget.getY(), finalTarget.getZ());
                float pitch = ModUtils.range(player.world.getRandom(), 0.8F, 1.2F);
                player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, pitch);

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
            return new BlockHitResult(pos, result.getSide(), new BlockPos(pos), true);
        }
        if(result.getSide() == Direction.DOWN)
        {
            Vec3d pos = new Vec3d(result.getPos().x, result.getBlockPos().getY()-2.5F, result.getPos().z);
            return new BlockHitResult(pos, result.getSide(), new BlockPos(pos), true);
        }

        Vec3f offset = result.getSide().getUnitVector();
        Vec3d pos = result.getPos().add(0, -1, 0).add(new Vec3d(offset));
        return new BlockHitResult(pos, result.getSide(), new BlockPos(pos), true);
    }

    public static void onClientPlayerPhasing(boolean enter)
    {
        PhasingEnchantment.clientTargetFov = enter ? 1.8F : 1.0F;
    }
}
