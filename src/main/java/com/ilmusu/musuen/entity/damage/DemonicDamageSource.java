package com.ilmusu.musuen.entity.damage;

import net.minecraft.entity.damage.DamageSource;

public class DemonicDamageSource extends DamageSource
{
    private boolean bypassesDemonction = false;

    public static final DamageSource DEMONIC_ENCHANTING =
            new DemonicDamageSource("demonicEnchanting")
                    .setBypassesDemonction()
                    .setBypassesArmor();

    public static final DamageSource DEMONIC_DAMAGE =
            new DemonicDamageSource("demonicDamage")
                    .setBypassesArmor();

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
