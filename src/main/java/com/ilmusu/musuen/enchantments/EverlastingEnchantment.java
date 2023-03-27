package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ItemEntityStackCallback;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class EverlastingEnchantment extends Enchantment
{
    public EverlastingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{});
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 1);
    }

    static
    {
        ItemEntityStackCallback.EVENT.register((entity, stack) ->
        {
            int level = EnchantmentHelper.getLevel(ModEnchantments.EVERLASTING, stack);
            if(level == 0)
                return;

            // Sets the item entity to never despawn
            entity.setNeverDespawn();
        });
    }
}
