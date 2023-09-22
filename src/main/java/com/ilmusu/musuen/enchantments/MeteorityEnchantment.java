package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.FireworkElytraSpeedCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;

public class MeteorityEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public MeteorityEnchantment(Rarity weight, int minLevel, int maxLevel)
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
        FireworkElytraSpeedCallback.EVENT.register(((shooter, firework, velocity) ->
        {
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.METEORITY, shooter);
            if(level == 0)
                return velocity;

            float speedBonus = 1.0F + level * 0.7F;
            return velocity.multiply(speedBonus);
        }));
    }
}
