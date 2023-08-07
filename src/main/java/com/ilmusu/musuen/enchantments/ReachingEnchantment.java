package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerTickCallback;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.UUID;

public class ReachingEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    private static final UUID REACHING_ENCHANTMENT_ID = UUID.fromString("fdd73ea0-7eaa-4ea0-8918-744136d1a0ba");

    public ReachingEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.REACHER, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMinLevel()
    {
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 4);
    }

    @SuppressWarnings("unused")
    public static boolean shouldIncreaseReach(ItemStack stack)
    {
        return true;
    }

    public static boolean shouldIncreaseAttackRange(ItemStack stack)
    {
        return EnchantmentTarget.WEAPON.isAcceptableItem(stack.getItem()) ||
               EnchantmentTarget.TRIDENT.isAcceptableItem(stack.getItem());
    }

    public static EntityAttributeModifier modifierForAdditionalReach(float reach)
    {
        return new EntityAttributeModifier(REACHING_ENCHANTMENT_ID, "Reaching Enchantment", reach, Operation.ADDITION);
    }

    static
    {
        PlayerTickCallback.BEFORE.register((player ->
        {
            float additionalReach = 0;

            ItemStack stack = player.getMainHandStack();

            // Removing the attributes so that can be reapplied
            EntityAttributeInstance reachAttribute = player.getAttributes().getCustomInstance(ReachEntityAttributes.REACH);
            EntityAttributeInstance rangeAttribute = player.getAttributes().getCustomInstance(ReachEntityAttributes.ATTACK_RANGE);
            reachAttribute.removeModifier(REACHING_ENCHANTMENT_ID);
            rangeAttribute.removeModifier(REACHING_ENCHANTMENT_ID);

            if(stack.isEmpty())
                return;

            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
            for(Enchantment enchantment : enchantments.keySet())
                if(enchantment instanceof ReachingEnchantment)
                    additionalReach += enchantments.get(enchantment);

            if(shouldIncreaseReach(stack))
                reachAttribute.addTemporaryModifier(ReachingEnchantment.modifierForAdditionalReach(additionalReach));
            if(shouldIncreaseAttackRange(stack))
                rangeAttribute.addTemporaryModifier(ReachingEnchantment.modifierForAdditionalReach(additionalReach));

        }));
    }
}
