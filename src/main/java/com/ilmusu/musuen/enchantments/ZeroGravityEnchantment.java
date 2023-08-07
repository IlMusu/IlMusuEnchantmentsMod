package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ProjectileShotCallback;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;

public class ZeroGravityEnchantment extends Enchantment
{
    public ZeroGravityEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.ARROW_SHOOTER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel()
    {
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 1);
    }

    static
    {
        ProjectileShotCallback.AFTER.register((shooter, container, projectile) ->
        {
            if(shooter.getWorld().isClient)
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.ZERO_GRAVITY, shooter);
            if(level == 0)
                return;

            // Setting the projectile to have no gravity
            projectile.setNoGravity(true);
        });
    }
}
