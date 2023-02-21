package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.callbacks.LivingEntityDamageCallback;
import com.ilmusu.ilmusuenchantments.entity.damage.DemonicDamageSource;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;

public class DemonctionEnchantment extends Enchantment
{
    public DemonctionEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    public static float getDemonicProtectionAmount(int level, DamageSource source)
    {
        return level * 0.5F;
    }

    static
    {
        LivingEntityDamageCallback.BEFORE_PROTECTION.register(((entity, source, damage) ->
        {
            if(!(source instanceof DemonicDamageSource demonicSource) || demonicSource.bypassesDemonction())
                return damage;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.DEMONCTION, entity);
            if(level == 0)
                return damage;

            damage -= DemonctionEnchantment.getDemonicProtectionAmount(level, source);
            return Math.max(0, damage);
        }));
    }
}
