package com.ilmusu.musuen.entities;

import com.google.common.collect.Multimap;
import com.ilmusu.musuen.client.particles.colored.ColoredParticleEffect;
import com.ilmusu.musuen.client.particles.eblock.BlockParticleEffect;
import com.ilmusu.musuen.entities.utils._IAdditionalSpawnData;
import com.ilmusu.musuen.registries.ModDamageSources;
import com.ilmusu.musuen.registries.ModEntities;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColossusEntity extends Entity implements _IAdditionalSpawnData
{
    public static final int DAMAGE_AFTER_TICKS = 25;
    public static final int DEATH_AFTER_TICKS = 100;

    @Nullable private UUID ownerUUID;
    @Nullable private Entity owner;

    protected ItemStack stack;
    protected Direction facing;
    protected int level;

    protected boolean hasDoneDamage = false;

    protected List<UUID> partsUUID = new ArrayList<>();

    public ColossusEntity(EntityType<?> type, World world)
    {
        super(type, world);
        this.ignoreCameraFrustum = true;
        this.noClip = true;
    }

    @Override
    protected void initDataTracker()
    {

    }

    public void setColossusStack(ItemStack stack)
    {
        this.stack = stack.copy();
    }

    public ItemStack getColossusStack()
    {
        return this.stack;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getLevel()
    {
        return this.level;
    }

    public void setFacing(Direction facing)
    {
        this.facing = facing;
    }

    public Direction getFacing()
    {
        return this.facing;
    }

    public void setOwner(Entity owner)
    {
        this.owner = owner;
        this.ownerUUID = this.owner.getUuid();
    }

    public Entity getOwner()
    {
        if(this.owner != null && !this.owner.isRemoved())
            return this.owner;
        if (this.ownerUUID != null && this.getWorld() instanceof ServerWorld serverWorld)
            this.owner = serverWorld.getEntity(this.ownerUUID);
        return this.owner;
    }

    public static Pair<Float, Float> getDimensionsScale(int level, Direction facing, boolean collision)
    {
        float scale =  1.0F + (level - 1) * 0.5F;
        if(!collision || facing == Direction.UP || facing == Direction.DOWN)
            return new Pair<>(scale, scale);
        return new Pair<>(scale, scale*0.3F);
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.age < DAMAGE_AFTER_TICKS)
        {
            if(this.getWorld().isClient)
                spawnRumblingParticles();
        }

        if(!this.hasDoneDamage && this.age > DAMAGE_AFTER_TICKS)
        {
            if(this.getWorld().isClient)
                spawnExplosionParticles();
            else
            {
                // Spawning the parts that provide collisions here
                this.spawnParts();

                // Damaging all the entities
                List<Entity> entities = this.getWorld().getOtherEntities(this,
                        getBoxForDamage(), (entity) -> entity instanceof Entity);
                if(!entities.isEmpty())
                {
                    DamageSource source = createDamageSource();
                    float damage = this.computeStackDamage() * (this.getLevel())*1.5F;
                    for(Entity entity : entities)
                        entity.damage(source, damage);
                }
            }

            this.hasDoneDamage = true;
        }

        if(this.age > DEATH_AFTER_TICKS)
            this.remove(RemovalReason.DISCARDED);
    }

    protected Box getBoxForDamage()
    {
        Direction facing = getFacing();
        Direction side1 = Direction.values()[(facing.ordinal()+2)%Direction.values().length];
        Direction side2 = Direction.values()[(side1.ordinal()+2)%Direction.values().length];
        Pair<Float, Float> scale = getDimensionsScale(getLevel(), getFacing(), false);
        float width = ModEntities.COLOSSUS_PART.getHeight()*scale.getLeft();
        float height = ModEntities.COLOSSUS_PART.getHeight()*scale.getRight()*3;

        Vec3d v0 = new Vec3d(facing.getUnitVector());
        Vec3d v1 = new Vec3d(side1.getUnitVector());
        Vec3d v2 = new Vec3d(side2.getUnitVector());

        Vec3d heightVec = v0.multiply(height);
        Vec3d sideVec = v1.add(v2).multiply(width).multiply(0.5);
        Box box = new Box(0, 0, 0, 0, 0, 0);
        box = box.stretch(heightVec.x, heightVec.y, heightVec.z);
        box = box.expand(sideVec.x, sideVec.y, sideVec.z);
        return box.offset(this.getPos());
    }

    protected void spawnParts()
    {
        Direction facing = getFacing();
        Vec3d dir = new Vec3d(facing.getUnitVector());
        Pair<Float, Float> scale = getDimensionsScale(getLevel(), getFacing(), true);
        float width = ModEntities.COLOSSUS_PART.getWidth()*scale.getLeft();
        float height = ModEntities.COLOSSUS_PART.getHeight()*scale.getRight();

        Vec3d start = this.getPos();
        if(facing == Direction.DOWN)
            start = start.add(0, -width, 0);
        else if(facing != Direction.UP)
            start = start.add(0, -height/2, 0).add(dir.multiply(width/2));

        for(int i=0; i<3; ++i)
        {
            ColossusPartEntity part = ColossusPartEntity.fromColossus(this);
            part.setPosition(start.add(dir.multiply(width*i)));
            this.getWorld().spawnEntity(part);

            this.partsUUID.add(part.getUuid());
        }
    }

    protected void removeParts()
    {
        ServerWorld server = (ServerWorld)this.getWorld();
        for(UUID uuid : this.partsUUID)
            server.getEntity(uuid).remove(RemovalReason.DISCARDED);
    }

    @Override
    public void remove(RemovalReason reason)
    {
        super.remove(reason);
        if(!isRemoved() || reason != RemovalReason.DISCARDED)
            return;

        if(!this.getWorld().isClient)
        {
            // Returning the owner or dropping
            if(getOwner() instanceof PlayerEntity living && !living.isRemoved())
                ModUtils.mergeStackOrDrop(this.getColossusStack(), living);
            else
                this.dropStack(this.getColossusStack(), 0.1F);
            // Removing the parts manually
            this.removeParts();
        }
    }

    @Override
    public void onRemoved()
    {
        super.onRemoved();
        spawnDespawnParticles();
    }

    protected BlockState getOnState()
    {
        return this.getWorld().getBlockState(this.getBlockPos().offset(getFacing().getOpposite()));
    }

    protected void spawnRumblingParticles()
    {
        BlockState state = getOnState();
        if(state != null)
        {
            int life = ModUtils.range(this.random, 30, 40);
            float size = ModUtils.range(this.random, 0.05F, 0.10F);
            BlockParticleEffect particle = new BlockParticleEffect(state).life(life).size(size).gravity(0.7F);

            for(int i=0; i<5; ++i)
            {
                Vec3d pos = this.getPos().add(0,0.2,0).add(ModUtils.randomInCircle(this.random, 1.0F));
                Vec3d vel = ModUtils.randomInCircle(this.random, 0.2F);
                this.getWorld().addParticle(particle, pos.x, pos.y, pos.z, vel.x, Math.abs(vel.y)+0.1F, vel.z);
            }
        }

        if(this.random.nextBoolean())
        {
            float pitch = ModUtils.range(this.random, 0.8F, 1.2F);
            SoundEvent sound = state.getSoundGroup().getBreakSound();
            this.getWorld().playSound(getX(), getY(), getZ(), sound, SoundCategory.BLOCKS, 1.0F, pitch, false);
        }
    }

    protected void spawnExplosionParticles()
    {
        BlockState state = getOnState();
        if(state != null)
        {
            for (int i = 0; i < 20; ++i)
            {
                int life = ModUtils.range(this.random, 30, 40);
                float size = ModUtils.range(this.random, 0.07F, 0.13F);
                Vec3d pos = this.getPos().add(0, this.random.nextFloat() * 2, 0).add(ModUtils.randomInCircle(this.random, 0.4F));
                Vec3d vel = ModUtils.randomInCircle(this.random, 0.3F);
                BlockParticleEffect particle = new BlockParticleEffect(state).life(life).size(size).gravity(0.7F);
                this.getWorld().addParticle(particle, pos.x, pos.y, pos.z, vel.x, Math.abs(vel.y) + 0.2F, vel.z);
            }
        }

        float pitch = ModUtils.range(this.random, 0.8F, 1.2F);
        SoundEvent sound = state.getSoundGroup().getBreakSound();
        this.getWorld().playSound(getX(), getY(), getZ(), sound, SoundCategory.BLOCKS, 1.4F, pitch, false);
    }

    protected void spawnDespawnParticles()
    {
        for(int i=0; i<20; ++i)
        {
            int life = ModUtils.range(this.random, 30, 40);
            float size = ModUtils.range(this.random, 0.15F, 0.30F);
            Vec3d pos = this.getPos().add(0,this.random.nextFloat()*2,0).add(ModUtils.randomInCircle(this.random, 0.4F));
            Vec3d vel = ModUtils.randomInCircle(this.random, 0.1F);
            ColoredParticleEffect particle = new ColoredParticleEffect(Color.DARK_GRAY).life(life).size(size).gravity(0.7F);
            this.getWorld().addParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y+0.1, vel.z);
        }
    }

    protected DamageSource createDamageSource()
    {
        return ModDamageSources.colossus(this.getOwner());
    }

    protected float computeStackDamage()
    {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = this.getColossusStack().getAttributeModifiers(EquipmentSlot.MAINHAND);
        EntityAttributeInstance instance = new EntityAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE, (instance1) -> {});
        instance.setBaseValue(0.0F);
        for(EntityAttributeModifier modifier : modifiers.get(EntityAttributes.GENERIC_ATTACK_DAMAGE))
            instance.addTemporaryModifier(modifier);
        return (float)instance.getValue();
    }

    @Override
    public boolean shouldRender(double distance)
    {
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
        nbt.putInt("age", this.age);
        nbt.put("colossus_stack", this.getColossusStack().writeNbt(new NbtCompound()));
        nbt.putInt("facing", this.getFacing().ordinal());
        nbt.putInt("level", this.getLevel());
        nbt.putBoolean("has_done_damage", this.hasDoneDamage);
        nbt.putFloat("yaw", this.getYaw());
        return nbt;
    }

    @Override
    public void readSpawnData(NbtCompound nbt)
    {
        this.age = nbt.getInt("age");
        this.setColossusStack(ItemStack.fromNbt(nbt.getCompound("colossus_stack")));
        this.setFacing(Direction.values()[nbt.getInt("facing")]);
        this.setLevel(nbt.getInt("level"));
        this.setYaw(nbt.getFloat("yaw"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putInt("age", this.age);
        nbt.putUuid("owner_uuid", this.ownerUUID);
        nbt.put("colossus_stack", this.getColossusStack().writeNbt(new NbtCompound()));
        nbt.putInt("facing", this.getFacing().ordinal());
        nbt.putInt("level", this.getLevel());
        nbt.putBoolean("has_done_damage", this.hasDoneDamage);
        NbtCompound parts_uuids = new NbtCompound();
        for(int i=0; i<this.partsUUID.size(); ++i)
            parts_uuids.putUuid("uuid_"+i, this.partsUUID.get(i));
        nbt.put("parts", parts_uuids);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        this.age = nbt.getInt("age");
        this.ownerUUID = nbt.getUuid("owner_uuid");
        this.setColossusStack(ItemStack.fromNbt(nbt.getCompound("colossus_stack")));
        this.setFacing(Direction.values()[nbt.getInt("facing")]);
        this.setLevel(nbt.getInt("level"));
        this.hasDoneDamage = nbt.getBoolean("has_done_damage");
        this.partsUUID.clear();
        NbtCompound parts = nbt.getCompound("parts");
        for(int i=0; i<parts.getSize(); ++i)
            this.partsUUID.add(parts.getUuid("uuid_"+i));
    }
}
