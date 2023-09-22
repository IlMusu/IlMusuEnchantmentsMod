package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.advancements.criteria.DemonicEnchantCriterion;
import net.minecraft.advancement.criterion.Criteria;

public class ModCriteria
{
    public static final DemonicEnchantCriterion DEMONIC_ENCHANTMENT = new DemonicEnchantCriterion();

    public static void register()
    {
        Criteria.register(Resources.identifier("demonic_enchantment").toString(), DEMONIC_ENCHANTMENT);
    }
}
