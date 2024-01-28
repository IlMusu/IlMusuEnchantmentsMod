package com.ilmusu.musuen.entities;

import com.ilmusu.musuen.entities.utils._IAdditionalSpawnData;
import com.ilmusu.musuen.registries.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ColossusPartEntity extends Entity implements _IAdditionalSpawnData
{
    protected int level;
    protected Direction facing;

    public ColossusPartEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }


    public static ColossusPartEntity fromColossus(ColossusEntity owner)
    {
        ColossusPartEntity part = ModEntities.COLOSSUS_PART.create(owner.getWorld());
        part.level = owner.level;
        part.facing = owner.facing;
        part.calculateDimensions();
        return part;
    }

    @Override
    protected void initDataTracker()
    {

    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose)
    {
        Pair<Float, Float> scale = ColossusEntity.getDimensionsScale(this.level, this.facing, true);
        return this.getType().getDimensions().scaled(scale.getLeft(), scale.getRight());
    }

    @Override
    public boolean isCollidable()
    {
        // Entities can walk on this entity
        return true;
    }

    @Override
    public boolean isAttackable()
    {
        return false;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this, 0);
    }

    @Override
    public NbtCompound writeSpawnData(NbtCompound nbt)
    {
        nbt.putInt("facing", this.facing.ordinal());
        nbt.putInt("level", this.level);
        return nbt;
    }

    @Override
    public void readSpawnData(NbtCompound nbt)
    {
        this.facing = Direction.values()[nbt.getInt("facing")];
        this.level = nbt.getInt("level");
        this.calculateDimensions();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putInt("facing", this.facing.ordinal());
        nbt.putInt("level", this.level);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        this.facing = Direction.values()[nbt.getInt("facing")];
        this.level = nbt.getInt("level");
        this.calculateDimensions();
    }
}
