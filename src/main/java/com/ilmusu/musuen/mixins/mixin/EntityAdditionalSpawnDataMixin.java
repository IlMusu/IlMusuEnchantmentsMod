package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.entities.utils._IAdditionalSpawnData;
import com.ilmusu.musuen.mixins.interfaces._IPacketAdditionalData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class EntityAdditionalSpawnDataMixin
{
    @Mixin(EntitySpawnS2CPacket.class)
    public static class EntitySpawnS2CPacketWithAdditionalData implements _IPacketAdditionalData
    {
        @Unique private NbtCompound data = new NbtCompound();

        @Override
        public NbtCompound getAdditionalData()
        {
            return this.data;
        }

        @Inject(method = "<init>(Lnet/minecraft/entity/Entity;I)V", at = @At("TAIL"))
        private void createAdditionalDataOnPacketCreation(Entity entity, int entityData, CallbackInfo ci)
        {
            if (entity instanceof _IAdditionalSpawnData spawnData)
                this.data = spawnData.writeSpawnData(new NbtCompound());
        }

        @Inject(method = "write", at = @At("TAIL"))
        public void writeAdditionalDataIntoBuffer(PacketByteBuf buf, CallbackInfo ci)
        {
            buf.writeNbt(this.data);
        }

        @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("TAIL"))
        public void readAdditionalDataFromBuffer(PacketByteBuf buf, CallbackInfo ci)
        {
            this.data = buf.readNbt();
        }
    }

    @Mixin(Entity.class)
    public static class EntityReadAdditionalDataFromPacket
    {
        @Inject(method = "onSpawnPacket", at = @At("TAIL"))
        public void setAdditionalData(EntitySpawnS2CPacket packet, CallbackInfo ci)
        {
            if (this instanceof _IAdditionalSpawnData spawnData)
                spawnData.readSpawnData(((_IPacketAdditionalData) packet).getAdditionalData());
        }
    }

    @Mixin(LivingEntity.class)
    public static class LivingReadAdditionalDataFromPacket
    {
        @Inject(method = "readFromPacket", at = @At("TAIL"))
        public void setAdditionalData(MobSpawnS2CPacket packet, CallbackInfo ci)
        {
            if (this instanceof _IAdditionalSpawnData spawnData)
                spawnData.readSpawnData(((_IPacketAdditionalData) packet).getAdditionalData());
        }
    }
}
