package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ItemEntityStackCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class EverlastingEnchantment extends Enchantment
{
    public EverlastingEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{});
        ((_IEnchantmentLevels)this).setConfigurationLevels(minLevel, maxLevel);
    }

    @Override
    public int getMinLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMinLevel();
    }

    @Override
    public int getMaxLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMaxLevel();
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
