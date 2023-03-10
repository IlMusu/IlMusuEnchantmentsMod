package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ProjectileReflectionCallback;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class ReflectionEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public ReflectionEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public boolean shouldUseStackInsteadOfTargetCheck()
    {
        return true;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack)
    {
        return stack.getItem() instanceof ShieldItem;
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
