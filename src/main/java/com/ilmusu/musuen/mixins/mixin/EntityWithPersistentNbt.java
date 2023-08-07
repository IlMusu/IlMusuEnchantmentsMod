package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityWithPersistentNbt implements _IEntityPersistentNbt
{
    @Unique protected NbtCompound pnbt = new NbtCompound();

    @Override
    public NbtCompound getPNBT()
    {
        return pnbt;
    }

    @Override
    public void clone(_IEntityPersistentNbt other)
    {
        this.pnbt = other.getPNBT();
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void writePocketsDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir)
    {
        nbt.put(Resources.MOD_ID+".persistent_data", this.pnbt);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci)
    {
        this.pnbt = nbt.getCompound(Resources.MOD_ID+".persistent_data");
    }

    static
    {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, isEndTeleport) ->
        {
            if(!isEndTeleport)
                return;
            ((_IEntityPersistentNbt)newPlayer).clone((_IEntityPersistentNbt)oldPlayer);
        });
    }
}