package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.callbacks.PlayerFovMultiplierCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IPlayerTickers;
import com.ilmusu.ilmusuenchantments.networking.messages.PhasingSwitchMessage;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import com.ilmusu.ilmusuenchantments.utils.ModUtils;
import com.ilmusu.ilmusuenchantments.utils.raycasting.ModRaycast;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class PhasingEnchantment extends Enchantment implements _IDemonicEnchantment
{
    public static float clientTargetFov = 1.0F;

    public PhasingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    public static boolean onPhasingKeyBindingPress(PlayerEntity player, int modifiers)
    {
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

        float travelled = ModRaycast.computeTravelDistance(player, hitResult);
        HitResult phaseResult = ModRaycast.raycastBox(player, travelled+0.5F, distance);

        // If it is not possible to phase, teleport to looked block
        if(phaseResult == null)
            return performPlayerPhasing(player, ModEnchantments.PHASING, offsetFromDir(hitResult), level);

        // This is the actual phasing
        return performPlayerPhasing(player, ModEnchantments.PHASING, phaseResult, level);
    }

    private static boolean performPlayerPhasing(PlayerEntity player, Enchantment ench, HitResult result, int level)
    {
        Vec3d target = result.getPos();
        if(player.world.getBlockState(new BlockPos(target)).getMaterial().blocksMovement())
            target = new Vec3d(target.getX(), ((int)target.getY())+1, target.getZ());

        Vec3d finalTarget = target;
        ((_IPlayerTickers)player).addTicker(new _IPlayerTickers.Ticker(10)
            .onEntering(() ->
                // Sending the message for updating the player fov
                new PhasingSwitchMessage(true).sendToClient((ServerPlayerEntity) player)
            )
            .onExiting(() ->
            {
                // Teleporting player
                player.requestTeleport(finalTarget.x, finalTarget.getY(), finalTarget.getZ());
                float pitch = ModUtils.range(player.world.getRandom(), 0.6F, 0.8F);
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

        Vector3f offset = result.getSide().getUnitVector();
        Vec3d pos = result.getPos().add(0, -1, 0).add(new Vec3d(offset));
        return new BlockHitResult(pos, result.getSide(), new BlockPos(pos), true);
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
    }
}
