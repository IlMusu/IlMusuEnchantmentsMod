package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerPhantomSpawnCallback;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class DreamlikeEnchantment extends Enchantment
{
    public DreamlikeEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMinLevel()
    {
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 3);
    }

    static
    {
        PlayerPhantomSpawnCallback.BEFORE.register((player, insomniaAmount) ->
        {
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.DREAMLIKE, player);
            if(level == 0)
                return insomniaAmount;

            // Reducing the insomnia amount with a percentage at a given level
            int maxLevel = ModEnchantments.ALIGHTING.getMaxLevel();
            float insomniaReduction = new ModUtils.Linear(0.0F, 1.0F, maxLevel, 0.0F).of(level);
            return (int) (insomniaAmount * insomniaReduction);
        });
    }
}
