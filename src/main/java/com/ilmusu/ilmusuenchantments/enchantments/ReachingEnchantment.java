package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.callbacks.PlayerReachDistanceCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEnchantmentExtensions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class ReachingEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public ReachingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public boolean shouldUseStackInsteadOfTargetCheck()
    {
        return true;
    }

    @Override
    public int getMaxLevel()
    {
        return 4;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack)
    {
        return  EnchantmentTarget.DIGGER.isAcceptableItem(stack.getItem()) ||
                EnchantmentTarget.WEAPON.isAcceptableItem(stack.getItem()) ||
                EnchantmentTarget.TRIDENT.isAcceptableItem(stack.getItem());
    }

    static
    {
        PlayerReachDistanceCallback.BEFORE.register((player, vanilla) ->
        {
            int additionalReach = 0;

            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(player.getMainHandStack());
            for(Enchantment enchantment : enchantments.keySet())
                if(enchantment instanceof ReachingEnchantment)
                    additionalReach += enchantments.get(enchantment);

            return vanilla + additionalReach;
        });
    }
}
