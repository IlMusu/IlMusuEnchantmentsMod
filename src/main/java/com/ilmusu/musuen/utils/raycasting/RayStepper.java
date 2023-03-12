package com.ilmusu.musuen.utils.raycasting;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Function;

public class RayStepper
{
    protected Vec3d start;
    protected Vec3d direction;
    protected float step;
    protected float range;

    protected  Vec3d position;
    protected float current;

    public RayStepper(Vec3d start, Vec3d direction, float step, float range)
    {
        this.start = start;
        this.direction = direction.normalize();
        this.step = step;
        this.range = range;

        this.current = 0;
        this.updatePosition();
    }

    public boolean stepForward()
    {
        // Computing the new position of the ray
        this.current += this.step;
        this.updatePosition();
        // Return true if this is complete
        return this.current < this.range;
    }

    public void stepBackward()
    {
        // Stepping the ray backwards
        this.current -= this.step;
        this.updatePosition();
    }

    protected void updatePosition()
    {
        this.position = this.start.add(this.direction.multiply(this.current));
    }

    public HitResult intersectsEmpty(World world, Box box)
    {
        // The box must be centered at its center position!
        box = box.offset(this.position);
        // Trying to find and empty position by moving the box up
        if(!world.getBlockCollisions(null, box).iterator().hasNext())
        {
            Vec3d center = box.getCenter();
            return BlockHitResult.createMissed(center, Direction.UP, BlockPos.ofFloored(center));
        }

        return null;
    }

    public BlockHitResult intersectsBlock(World world, Function<Object, Boolean> filter)
    {
        BlockPos pos = BlockPos.ofFloored(this.position);
        BlockState state = world.getBlockState(pos);

        if(state.isAir() || !filter.apply(state))
            return null;

        // The box needs to be at the origin
        float f = this.step/2.0F;
        Box box = new Box(-f, -f, -f, +f, +f, +f);
        // Check if the block shape actually intersects the box
        List<Box> boxes = state.getOutlineShape(world, pos).getBoundingBoxes();
        if(boxes.stream().noneMatch((box::intersects)))
            return null;

        this.stepBackward();
        Vec3d delta = this.position.subtract(new Vec3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5));
        return new BlockHitResult(this.position, Direction.getFacing(delta.x, delta.y, delta.z), pos, true);
    }

    public BlockHitResult intersectsFullBlock(World world, Function<Object, Boolean> filter)
    {
        BlockPos pos = BlockPos.ofFloored(this.position);
        BlockState state = world.getBlockState(pos);

        if(state.isAir() || !filter.apply(state))
            return null;

        this.stepBackward();
        Vec3d delta = this.position.subtract(new Vec3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5));
        return new BlockHitResult(this.position, Direction.getFacing(delta.x, delta.y, delta.z), pos, true);
    }

    public EntityHitResult intersectsEntity(World world, Function<Object, Boolean> filter)
    {
        float f = this.step/2.0F;
        Vec3d vec0 = this.position.add(-f, -f, -f);
        Vec3d vec1 = this.position.add(+f, +f, +f);
        Box aabb = new Box(vec0, vec1);

        List<Entity> list = world.getNonSpectatingEntities(Entity.class, aabb);
        list.removeIf((entity) -> !filter.apply(entity));

        if(list.size() == 0)
            return null;

        return new EntityHitResult(list.get(0), this.position);
    }

    public BlockHitResult createMissed()
    {
        Vec3d missVec = this.start.add(this.direction.multiply(this.range));
        return BlockHitResult.createMissed(missVec, Direction.UP, BlockPos.ofFloored(missVec));
    }
}
