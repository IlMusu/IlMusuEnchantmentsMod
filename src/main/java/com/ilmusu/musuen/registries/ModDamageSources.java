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
                    .setBypassesArmor();

    public static final DamageSource DEMONIC_DAMAGE =
            new DemonicDamageSource("demonicDamage")
                    .setBypassesArmor();

    public static DamageSource colossus(Entity attacker)
    {
        return new EntityDamageSource("colossus", attacker);
    }
}
