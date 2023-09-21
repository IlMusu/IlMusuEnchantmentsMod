package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ProjectileShotCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;

public class ZeroGravityEnchantment extends Enchantment
{
    public ZeroGravityEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, ModEnchantmentTargets.ARROW_SHOOTER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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

    @Override
    protected boolean canAccept(Enchantment other)
    {
        return !(other instanceof SkyhookEnchantment) &&
               super.canAccept(other);
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
