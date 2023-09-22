package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ShieldCoverageCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;

public class CoverageEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public CoverageEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, ModEnchantmentTargets.SHIELD, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        ShieldCoverageCallback.BEFORE.register(((user, shield, source) ->
        {
            int level = EnchantmentHelper.getLevel(ModEnchantments.COVERAGE, shield);
            if(level == 0)
                return 0.0;

            return (double)level/ModEnchantments.COVERAGE.getMaxLevel();
        }));
    }
}
