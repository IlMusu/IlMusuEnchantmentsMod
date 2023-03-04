package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.FireworkElytraSpeedCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentExtensions;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class MeteorityEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public MeteorityEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.WEARABLE, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public boolean shouldUseStackInsteadOfTargetCheck()
    {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack)
    {
        return stack.getItem() instanceof ElytraItem;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    static
    {
        FireworkElytraSpeedCallback.EVENT.register(((shooter, firework, rotation) ->
        {
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.METEORITY, shooter);
            if(level == 0)
                return rotation;

            float speedBonus = 1.0F + level * 0.7F;
            return rotation.multiply(speedBonus);
        }));
    }
}
