package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

public class ModDamageTypes
{
    public static final RegistryKey<DamageType> DEMONIC_ENCHANTING = ModDamageTypes.of("demonic_enchanting");
    public static final RegistryKey<DamageType> DEMONIC_DAMAGE = ModDamageTypes.of("demonic_damage");

    public static RegistryKey<DamageType> of(String identifier)
    {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Resources.identifier(identifier));
    }
}
