package com.ilmusu.ilmusuenchantments.mixins.interfaces;

import net.minecraft.entity.damage.DamageSource;

public interface _IEntityTrackableDrops
{
    void setDeathDamageSource(DamageSource source);

    DamageSource getDeathDamageSource();
}
