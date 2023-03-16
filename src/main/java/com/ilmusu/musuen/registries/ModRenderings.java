package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.enchantments.BerserkerEnchantment;
import com.ilmusu.musuen.enchantments.SkyhookEnchantment;

public class ModRenderings
{
    public static void register()
    {
        SkyhookEnchantment.SkyhookEnchantmentRendering.register();
        BerserkerEnchantment.BerserkerOverlayRendering.register();
    }
}
