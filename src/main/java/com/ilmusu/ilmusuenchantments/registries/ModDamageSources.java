package com.ilmusu.ilmusuenchantments.registries;

import com.ilmusu.ilmusuenchantments.entity.damage.DemonicDamageSource;
import net.minecraft.entity.damage.DamageSource;

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
}
