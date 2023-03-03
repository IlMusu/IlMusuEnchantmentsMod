package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.callbacks.ShieldCoverageAngleCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEnchantmentExtensions;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class CoverageEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public CoverageEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean shouldUseStackInsteadOfTargetCheck()
    {
        return true;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack)
    {
        return stack.getItem() instanceof ShieldItem;
    }

    static
    {
        ShieldCoverageAngleCallback.BEFORE.register(((user, shield, source) ->
        {
            int level = EnchantmentHelper.getLevel(ModEnchantments.COVERAGE, shield);
            if(level == 0)
                return 0.0;

            return (double)level/ModEnchantments.COVERAGE.getMaxLevel();
        }));
    }
}
