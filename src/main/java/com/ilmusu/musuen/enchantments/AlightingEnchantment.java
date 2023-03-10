package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class AlightingEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public AlightingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public boolean shouldUseStackInsteadOfTargetCheck()
    {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack)
    {
        return stack.getItem() instanceof ElytraItem;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    static
    {
        LivingEntityDamageCallback.BEFORE_FALL.register(((entity, source, damage) ->
        {
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.ALIGHTING, entity);
            if(level == 0)
                return damage;

            // Computing the damage reduction
            float reduction = level < ModEnchantments.ALIGHTING.getMaxLevel() ? level*1.5F : damage;
            return Math.max(0, damage - reduction);
        }));
    }
}
