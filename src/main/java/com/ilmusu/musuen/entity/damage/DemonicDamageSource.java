package com.ilmusu.musuen.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class DemonicDamageSource extends DamageSource
{
    private boolean bypassesDemonction = false;

    public DemonicDamageSource(RegistryEntry<DamageType> type, @Nullable Entity source, @Nullable Entity attacker)
    {
        super(type, source, attacker);
    }
    
    public DemonicDamageSource setBypassesDemonction()
    {
        this.bypassesDemonction = true;
        return this;
    }

    public boolean bypassesDemonction()
    {
        return this.bypassesDemonction;
    }
}
