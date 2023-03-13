package com.ilmusu.musuen.mixins.interfaces;

import com.ilmusu.musuen.callbacks.RegisterDamageTypesCallback;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registerable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTypes.class)
@SuppressWarnings("UnusedMixin")
public abstract class DamageTypesCallbacks implements DamageTypes
{
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void registerDamageTypesCallback(Registerable<DamageType> registerable, CallbackInfo ci)
    {
        RegisterDamageTypesCallback.AFTER.invoker().handler(registerable);
    }
}
