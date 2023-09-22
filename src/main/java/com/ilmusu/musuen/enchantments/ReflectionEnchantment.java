package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ProjectileReflectionCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class ReflectionEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public ReflectionEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, ModEnchantmentTargets.SHIELD, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
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
        ProjectileReflectionCallback.BEFORE.register((hit, projectile) ->
        {
            if(!(hit.getEntity() instanceof LivingEntity living))
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.REFLECTION, living);
            if(level == 0)
                return;

            float norm = level*3F;
            projectile.setVelocity(projectile.getVelocity().multiply(norm));
        });
    }
}
