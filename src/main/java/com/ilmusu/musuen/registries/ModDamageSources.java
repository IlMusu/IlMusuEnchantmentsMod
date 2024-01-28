package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.entity.damage.DemonicDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;

public class ModDamageSources
{
    public static final DamageSource DEMONIC_ENCHANTING =
            new DemonicDamageSource("demonicEnchanting")
                    .setBypassesDemonction()
                    .setBypassesProtection()
                    .setBypassesArmor();

    public static final DamageSource DEMONIC_DAMAGE =
            new DemonicDamageSource("demonicDamage")
                    .setBypassesProtection()
                    .setBypassesArmor();

    public static DamageSource colossus(Entity attacker)
    {
        return new EntityDamageSource("colossus", attacker);
    }
}
