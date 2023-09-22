package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.LivingEntityJumpCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.MathHelper;

public class LongJumpEnchantment extends Enchantment
{
    public LongJumpEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
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
        return !(other instanceof LongJumpEnchantment) &&
               !(other instanceof SkyJumpEnchantment) &&
               !(other == Enchantments.FEATHER_FALLING) &&
                super.canAccept(other);
    }

    static
    {
        LivingEntityJumpCallback.EVENT.register(((entity, velocity) ->
        {
            // The entity must be sprinting
            if(!entity.isSprinting())
                return velocity;

            // The entity must have this enchantment on its armor
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.LONG_JUMP, entity);
            if(level == 0)
                return velocity;

            float angle = entity.getYaw() * ((float)Math.PI / 180);
            float strength = level*0.2F;
            float up = strength*0.1F;
            return velocity.add(-MathHelper.sin(angle) * strength, up, MathHelper.cos(angle) * strength);
        }));
    }
}
