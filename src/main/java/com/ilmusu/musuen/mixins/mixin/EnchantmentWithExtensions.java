package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.enchantments._IEnchantmentExtensions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class EnchantmentWithExtensions
{
    @Mixin(EnchantmentHelper.class)
    public static abstract class EnchantmentHelperModifications
    {
        @Unique private static ItemStack stack;

        @Inject(method = "getAttackDamage", at = @At("HEAD"))
        private static void addStackDependantDamageHook(ItemStack stack, EntityGroup group, CallbackInfoReturnable<Float> cir)
        {
            EnchantmentHelperModifications.stack = stack;
        }

        @Inject(method = "method_8208", at = @At(
                value = "INVOKE",
                target = "Lorg/apache/commons/lang3/mutable/MutableFloat;add(F)V",
                shift = At.Shift.AFTER
        ))
        private static void addStackDependantDamage(MutableFloat mutableFloat, EntityGroup entityGroup,
                                                    Enchantment enchantment, int level, CallbackInfo ci)
        {
            ItemStack stack = EnchantmentHelperModifications.stack;
            EnchantmentHelperModifications.stack = null;

            // The stack should not be null to add stack dependant damage
            if(stack == null || !(enchantment instanceof _IEnchantmentExtensions enchantmentExt))
                return;

            // Adding stack dependant damage to the accumulator
            mutableFloat.add(enchantmentExt.getAdditionalAttackDamage(stack, level, entityGroup));
        }
    }
}