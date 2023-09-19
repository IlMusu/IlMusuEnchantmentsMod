package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.enchantments._IEnchantmentExtensions;
import com.ilmusu.musuen.enchantments.utils.ModEnchantmentHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

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

            // The stack should not be null to add stack dependant damage
            if(stack == null || !(enchantment instanceof _IEnchantmentExtensions enchantmentExt))
                return;

            // Adding stack dependant damage to the accumulator
            mutableFloat.add(enchantmentExt.getAdditionalAttackDamage(stack, level, entityGroup));
        }
    }

    @Mixin(PlayerEntity.class)
    public static abstract class PlayerModifications
    {
        @ModifyVariable(method = "getBlockBreakingSpeed", at = @At(
                value = "TAIL",
                shift = At.Shift.BEFORE
        ))
        private float afterComputingBlockBreakingSpeed(float speed)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            return speed + ModEnchantmentHelper.getEnchantmentBreakSpeed(player.getMainHandStack());
        }
    }

    @Mixin(ItemStack.class)
    public static abstract class ItemStackExtensions
    {
        @Shadow
        protected abstract int getHideFlags();

        @Shadow
        private static boolean isSectionVisible(int flags, ItemStack.TooltipSection tooltipSection)
        {
            return false;
        }

        @SuppressWarnings("InvalidInjectorMethodSignature")
        @Inject(method = "getTooltip", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/item/ItemStack;hasNbt()Z",
                ordinal = 1))
        private void addAdditionalSpeedToTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list)
        {
            int hideFlags = this.getHideFlags();
            if(!isSectionVisible(hideFlags, ItemStack.TooltipSection.MODIFIERS))
                return;

            ItemStack stack = (ItemStack)(Object)this;
            float additionalDigSpeed = ModEnchantmentHelper.getEnchantmentBreakSpeed(stack);
            if(additionalDigSpeed == 0.0F)
                return;

            // Adding the tooltip
            String operation = "attribute.modifier.plus."+ EntityAttributeModifier.Operation.ADDITION.getId();
            Text attributeName = Text.translatable("attribute.name.generic.musuen.break_speed");
            String value = ItemStack.MODIFIER_FORMAT.format(additionalDigSpeed);
            list.add(Text.literal(" ").append(Text.translatable(operation, value, attributeName)).formatted(Formatting.DARK_GREEN));
        }
    }
}