package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.enchantments._IEnchantmentExtensions;
import com.ilmusu.musuen.mixins.MixinSharedData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

public abstract class EnchantmentWithExtensions
{
    @Mixin(EnchantmentHelper.class)
    public static abstract class EnchantmentHelperModifications
    {
        private static ItemStack musuen$stack;
        private static Enchantment musuen$enchantment;

        @Inject(method = "getAttackDamage", at = @At("HEAD"))
        private static void addStackDependantDamageHook(ItemStack stack, EntityGroup group, CallbackInfoReturnable<Float> cir)
        {
            EnchantmentHelperModifications.musuen$stack = stack;
        }

        @Inject(method = "method_8208", at = @At(
            value = "INVOKE",
            target = "Lorg/apache/commons/lang3/mutable/MutableFloat;add(F)V",
            shift = At.Shift.AFTER
        ))
        private static void addStackDependantDamage(MutableFloat mutableFloat, EntityGroup entityGroup,
            Enchantment enchantment, int level, CallbackInfo ci)
        {
            ItemStack stack = EnchantmentHelperModifications.musuen$stack;
            EnchantmentHelperModifications.musuen$stack = null;

            if(!(enchantment instanceof _IEnchantmentExtensions enchantmentExt))
                return;

            // Adding stack dependant damage to the accumulator
            mutableFloat.add(enchantmentExt.getAdditionalAttackDamage(stack, level, entityGroup));
        }

        @Inject(method = "getPossibleEntries", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"
        ))
        private static void fixEnchantmentExtractionHook(int power, ItemStack stack, boolean treasureAllowed,
            CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir, List<?> list, Item item, boolean bl,
                Iterator<?> var6, Enchantment enchantment)
        {
            EnchantmentHelperModifications.musuen$enchantment = enchantment;
        }

        @Inject(method = "getPossibleEntries", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z")
        )
        private static void fixEnchantmentExtraction(int power, ItemStack stack, boolean treasureAllowed,
             CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir)
        {
            Enchantment enchantment = EnchantmentHelperModifications.musuen$enchantment;
            EnchantmentHelperModifications.musuen$enchantment = null;

            // Vanilla behavior, using target for checking the stack
            if(!(enchantment instanceof _IEnchantmentExtensions ext) || !ext.shouldUseStackInsteadOfTargetCheck())
                return;

            MixinSharedData.shouldOverrideEnchantmentTargetAcceptableCheck = true;
            MixinSharedData.overrideEnchantmentTargetAcceptable = enchantment.isAcceptableItem(stack);
        }
    }

    @Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$2")
    public static class EnchantmentTargetBreakableStackCheck
    {
        @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
        private void performStackCheckIfRequired(Item item, CallbackInfoReturnable<Boolean> cir)
        {
            if(MixinSharedData.shouldOverrideEnchantmentTargetAcceptableCheck)
                cir.setReturnValue(MixinSharedData.overrideEnchantmentTargetAcceptable);
            MixinSharedData.shouldOverrideEnchantmentTargetAcceptableCheck = false;
        }
    }
}