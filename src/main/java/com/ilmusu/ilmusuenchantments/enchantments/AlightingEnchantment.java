package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.callbacks.LivingEntityDamageCallback;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AlightingEnchantment extends Enchantment
{
    public AlightingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.WEARABLE, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    static
    {
        LivingEntityDamageCallback.FALL.register(((entity, source, damage) ->
        {
            // The entity must be wearing elytra and fall flying in order for this to work
            if(!entity.isFallFlying())
                return damage;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.ALIGHTING, entity);
            if(level == 0)
                return damage;

            // Cancelling the damage if the level is max
            if(level == ModEnchantments.ALIGHTING.getMaxLevel())
                return 0;
            // Computing the damage reduction
            float reduction = level*1.5F;
            return damage - reduction;
        }));
    }
}
