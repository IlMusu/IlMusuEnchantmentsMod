package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.mixins.interfaces._IModDamageSources;
import com.ilmusu.musuen.registries.ModDamageTypes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSources.class)
public abstract class ModDamageSources implements _IModDamageSources
{
    @Shadow public abstract DamageSource create(RegistryKey<DamageType> key);

    @Unique private DamageSource demonicEnchanting;
    @Unique private DamageSource demonicDamage;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInitialization(DynamicRegistryManager registryManager, CallbackInfo ci)
    {
        this.demonicEnchanting = this.create(ModDamageTypes.DEMONIC_ENCHANTING);
        this.demonicDamage = this.create(ModDamageTypes.DEMONIC_DAMAGE);
    }

    @Override
    public DamageSource demonicEnchanting()
    {
        return this.demonicEnchanting;
    }

    @Override
    public DamageSource demonicDamage()
    {
        return this.demonicDamage;
    }
}
