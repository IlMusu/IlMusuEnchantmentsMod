package com.ilmusu.ilmusuenchantments.entity.damage;

import net.minecraft.entity.damage.DamageSource;

public class DemonicDamageSource extends DamageSource
{
    private boolean bypassesDemonction = false;

    public DemonicDamageSource(String name)
    {
        super(name);
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
