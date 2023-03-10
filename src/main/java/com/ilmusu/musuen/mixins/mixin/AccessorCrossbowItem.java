package com.ilmusu.musuen.mixins.mixin;

import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CrossbowItem.class)
public interface AccessorCrossbowItem
{
    @Invoker("getPullProgress")
    static float getPullProgressAccess(int useTicks, ItemStack stack)
    {
        return 0;
    }
}
