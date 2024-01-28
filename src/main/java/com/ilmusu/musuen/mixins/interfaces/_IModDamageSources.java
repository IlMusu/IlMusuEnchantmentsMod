package com.ilmusu.musuen.mixins.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

public interface _IModDamageSources
{
    DamageSource demonicEnchanting();

    DamageSource demonicDamage();

    DamageSource colossus(@Nullable Entity attacker);
}
