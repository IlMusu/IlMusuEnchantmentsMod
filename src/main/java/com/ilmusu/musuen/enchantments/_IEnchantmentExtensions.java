package com.ilmusu.musuen.enchantments;

import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemStack;

public interface _IEnchantmentExtensions
{
    default float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        return 0.0F;
    }

    default float getAdditionalBreakSpeed(ItemStack stack, int level)
    {
        return 0.0F;
    }
}
