package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ProjectileReflectionCallback;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class ReflectionEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public ReflectionEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.SHIELD, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 3);
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
