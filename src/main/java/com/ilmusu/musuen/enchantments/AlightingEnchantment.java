package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;

public class AlightingEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public AlightingEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.ELYTRA, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 0);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 5);
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
