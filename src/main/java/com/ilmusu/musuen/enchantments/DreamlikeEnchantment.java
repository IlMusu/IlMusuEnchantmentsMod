package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PhantomSpawnCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class DreamlikeEnchantment extends Enchantment
{
    public DreamlikeEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
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
        PhantomSpawnCallback.BEFORE.register((player, insomniaAmount) ->
        {
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.DREAMLIKE, player);
            if(level == 0)
                return 1.0F;

            // Reducing the insomnia amount with a percentage at a given level
            int maxLevel = ModEnchantments.DREAMLIKE.getMaxLevel();
            return new ModUtils.Linear(0.0F, 1.0F, maxLevel, 0.0F).of(level);
        });
    }
}
