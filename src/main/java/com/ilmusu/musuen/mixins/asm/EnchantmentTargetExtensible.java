package com.ilmusu.musuen.mixins.asm;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("UnusedMixin")
@Mixin(EnchantmentTarget.class)
abstract class EnchantmentTargetExtensible
{
    @Shadow public abstract boolean isAcceptableItem(Item item);
}