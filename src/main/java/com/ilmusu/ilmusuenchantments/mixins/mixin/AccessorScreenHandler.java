package com.ilmusu.ilmusuenchantments.mixins.mixin;

import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScreenHandler.class)
public interface AccessorScreenHandler
{
    @Invoker("addProperty")
    Property addPropertyAccess(Property property);

    @Invoker("addSlot")
    Slot addSlotAccess(Slot slot);
}
