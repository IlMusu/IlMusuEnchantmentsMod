package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;

public class AlightingEnchantment extends Enchantment
{
    public AlightingEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, ModEnchantmentTargets.ELYTRA, new EquipmentSlot[]{EquipmentSlot.CHEST});
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
