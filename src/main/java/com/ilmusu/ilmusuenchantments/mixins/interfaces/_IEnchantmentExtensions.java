package com.ilmusu.ilmusuenchantments.mixins.interfaces;

import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemStack;

public interface _IEnchantmentExtensions
{
    default float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        return 0;
    }

    default boolean shouldUseStackInsteadOfTargetCheck()
    {
        return false;
    }
}
