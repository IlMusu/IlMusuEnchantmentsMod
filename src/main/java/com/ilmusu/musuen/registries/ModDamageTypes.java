package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.callbacks.RegisterDamageTypesCallback;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

public class ModDamageTypes
{
    public static final RegistryKey<DamageType> DEMONIC_ENCHANTING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("demonic_enchanting"));
    public static final RegistryKey<DamageType> DEMONIC_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("demonic_damage"));

    static
    {
        RegisterDamageTypesCallback.AFTER.register(registerable ->
        {
            registerable.register(DEMONIC_ENCHANTING, new DamageType("demonicEnchanting", 0.1f));
            registerable.register(DEMONIC_DAMAGE, new DamageType("demonicDamage", 0.1f));
        });
    }
}
