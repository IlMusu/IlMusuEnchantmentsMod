package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityWithPersistentNbt implements _IEntityPersistentNbt
{
    protected NbtCompound musuPersistentNbt = new NbtCompound();

    @Override
    public NbtCompound getPNBT()
    {
        return musuPersistentNbt;
    }

    @Override
    public void clone(_IEntityPersistentNbt other)
    {
        this.musuPersistentNbt = other.getPNBT();
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void writePocketsDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir)
    {
        nbt.put(Resources.MOD_ID+".persistent_data", this.musuPersistentNbt);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci)
    {
        this.musuPersistentNbt = nbt.getCompound(Resources.MOD_ID+".persistent_data");
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
