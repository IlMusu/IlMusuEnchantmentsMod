package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerItemUseCallback;
import com.ilmusu.musuen.entities.ColossusEntity;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.registries.ModEntities;
import com.ilmusu.musuen.utils.raycasting.ModRaycast;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ColossusEnchantment extends Enchantment
{
    public ColossusEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        ((_IEnchantmentLevels)this).setConfigurationLevels(minLevel, maxLevel);
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

    protected static boolean filterTargets(PlayerEntity caster, Object target)
    {
        if(target instanceof BlockState)
            return ((BlockState) target).isSolid();
        return target instanceof LivingEntity && target != caster;
    }

    static
    {
        PlayerItemUseCallback.EVENT.register((player, hand, stack) -> {
            if(player.getWorld().isClient || stack == null)
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.COLOSSUS, stack);
            if(level == 0)
                return;

            Pair<Vec3d, Direction> result = getColossusSpawningPos(player);
            if(result == null)
                return;

            Vec3d spawnPos = result.getLeft();
            Direction facing = result.getRight();

            ColossusEntity colossus = ModEntities.COLOSSUS.create(player.getWorld());
            colossus.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            colossus.setFacing(facing);
            colossus.setYaw(player.getRandom().nextFloat()*6.28F);
            colossus.setColossusStack(stack);
            colossus.setOwner(player);
            colossus.setLevel(level);

            player.getWorld().spawnEntity(colossus);

            if(!player.isCreative())
                player.setStackInHand(hand, ItemStack.EMPTY);
        });
    }

    protected static Pair<Vec3d, Direction> getColossusSpawningPos(PlayerEntity user)
    {
        HitResult result = ModRaycast.raycast(user, 15, (object) -> ColossusEnchantment.filterTargets(user, object));
        if(result.getType().equals(HitResult.Type.MISS))
            return null;

        if(result.getType().equals(HitResult.Type.BLOCK))
        {
            BlockHitResult blockResult = (BlockHitResult) result;
            BlockPos blockPos = blockResult.getBlockPos();
            Direction facing = blockResult.getSide();
            Vec3d spawnPos = blockPos.toCenterPos().add(new Vec3d(facing.getUnitVector()).multiply(0.6));
            return new Pair<>(spawnPos, facing);
        }

        if(result.getType().equals(HitResult.Type.ENTITY))
        {
            BlockPos pos = ((EntityHitResult) result).getEntity().getBlockPos();
            BlockPos.Mutable mutable = new BlockPos.Mutable(pos.getX(), pos.getY()+1, pos.getZ());
            while(!user.getWorld().getBlockState(mutable.move(Direction.DOWN)).isSolid() && mutable.getY() >= 0);

            if(mutable.getY() < 0)
                return null;

            Vec3d spawnPos = mutable.toCenterPos().add(0, 0.6, 0);
            return new Pair<>(spawnPos, Direction.UP);
        }

        return null;
    }
}
