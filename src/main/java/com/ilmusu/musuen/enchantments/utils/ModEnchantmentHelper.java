package com.ilmusu.musuen.enchantments.utils;

import com.ilmusu.musuen.enchantments._IEnchantmentExtensions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;

public class ModEnchantmentHelper extends EnchantmentHelper
{
    public static float getEnchantmentBreakSpeed(ItemStack stack)
    {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.forEachEnchantment((Enchantment enchantment, int level) ->
        {
            if(enchantment instanceof _IEnchantmentExtensions extensions)
                mutableFloat.add(extensions.getAdditionalBreakSpeed(stack, level));
        }, stack);
        return mutableFloat.floatValue();
    }
}
