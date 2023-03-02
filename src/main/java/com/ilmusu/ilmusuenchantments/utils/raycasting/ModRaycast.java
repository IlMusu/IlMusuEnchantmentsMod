package com.ilmusu.ilmusuenchantments.utils.raycasting;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Function;

public class ModRaycast
{
    public static HitResult raycast(World world, Vec3d start, Vec3d direction, float range, Function<Object, Boolean> filter)
    {
        RayStepper stepper = new RayStepper(start, direction, 0.1F, range);
        while(!stepper.step())
        {
            HitResult resultEntity = stepper.intersectsEntity(world, filter);
            if(resultEntity != null)
                return resultEntity;

            HitResult resultBlock = stepper.intersectsBlock(world, filter);
            if(resultBlock != null)
                return resultBlock;
        }

        return stepper.createMissed();
    }

    public static BlockHitResult raycastBlock(World world, Vec3d start, Vec3d direction, float range, Function<Object, Boolean> filter)
    {
        RayStepper stepper = new RayStepper(start, direction, 0.1F, range);
        while(stepper.step())
        {
            BlockHitResult result = stepper.intersectsBlock(world, filter);
            if(result != null)
                return result;
        }

        return stepper.createMissed();
    }

    public static BlockHitResult raycastFullBlock(World world, Vec3d start, Vec3d direction, float range, Function<Object, Boolean> filter)
    {
        RayStepper stepper = new RayStepper(start, direction, 0.1F, range);
        while(stepper.step())
        {
            BlockHitResult result = stepper.intersectsFullBlock(world, filter);
            if(result != null)
                return result;
        }

        return stepper.createMissed();
    }

    public static HitResult raycastBox(World word, Vec3d start, Vec3d direction, float range, Box box)
    {
        RayStepper stepper = new RayStepper(start, direction, 0.1F, range);
        while(stepper.step())
        {
            HitResult result = stepper.intersectsEmpty(word, box);
            if(result != null)
                return result;
        }

        return null;
    }

    public static HitResult raycast(LivingEntity entity, float range, Function<Object, Boolean> filter)
    {
        return raycast(entity.world, computeStart(entity), entity.getRotationVector(), range, filter);
    }

    public static BlockHitResult raycastBlock(LivingEntity entity, float from, float range)
    {
        // Adjusting the start pos with the value of "from"
        Vec3d direction = entity.getRotationVector();
        Vec3d start = computeStart(entity).add(direction.multiply(from));
        // The selector for the block
        Function<Object, Boolean> filter = (state) -> ((BlockState)state).getMaterial().blocksMovement();
        return raycastBlock(entity.world, start, direction, range, filter);
    }

    public static BlockHitResult raycastFullBlock(LivingEntity entity, float from, float range)
    {
        // Adjusting the start pos with the value of "from"
        Vec3d direction = entity.getRotationVector();
        Vec3d start = computeStart(entity).add(direction.multiply(from));
        // The selector for the block
        Function<Object, Boolean> filter = (state) -> ((BlockState)state).getMaterial().blocksMovement();
        return raycastFullBlock(entity.world, start, direction, range, filter);
    }

    public static HitResult raycastBox(LivingEntity entity, Box box, float from, float range)
    {
        // Adjusting the start pos with the value of "from"
        Vec3d direction = entity.getRotationVector();
        Vec3d start = computeStart(entity).add(direction.multiply(from));
        // The selector for the block
        box = box.offset(0, -entity.getEyeHeight(entity.getPose()), 0);
        return raycastBox(entity.world, start, direction, range, box);
    }

    public static Vec3d computeStart(LivingEntity entity)
    {
        return entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
    }

    public static float computeTravelDistance(LivingEntity entity, HitResult raycast)
    {
        return (float) computeStart(entity).distanceTo(raycast.getPos());
    }
}
