package com.ilmusu.ilmusuenchantments.registries;

import net.minecraft.entity.damage.DamageSource;

public class ModDamageSources
{
    public static final DamageSource DEMONIC_ENCHANTING = new DamageSource("demonicEnchanting").setBypassesArmor().setBypassesProtection().setUnblockable();
    public static final DamageSource DEMONIC_DAMAGE_ENCHANTMENT = new DamageSource("demonicDamageEnchantment").setBypassesArmor().setBypassesProtection().setUnblockable();
}
