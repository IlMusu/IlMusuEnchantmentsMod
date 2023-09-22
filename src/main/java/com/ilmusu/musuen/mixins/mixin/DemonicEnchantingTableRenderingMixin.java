package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.mixins.interfaces._IDemonicEnchantmentScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class DemonicEnchantingTableRenderingMixin
{
    @Mixin(EnchantmentScreen.class)
    public abstract static class HeartRequirementsRendering
    {
        @Unique private int slot = 0;
        @Unique private boolean isDemonicEnchantment = false;

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/screen/EnchantmentScreenHandler;getLapisCount()I"
        ))
        private void drawBackgroundEnchantmentPreHook(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            // Resetting the slot count before entering the for-loop cycle
            this.slot = -1;
        }

        @ModifyVariable(method = "drawBackground", ordinal = 8, at = @At(value = "STORE"))
        private int drawBackgroundEnchantmentHook(int constant)
        {
            // Increasing the slot count, this is called at every iteration
            this.slot += 1;
            // Storing if the enchantment for slot is demonic so that this is done only once
            this.isDemonicEnchantment = getDemonicScreenHandler().hasDemonicEnchantment(this.slot);
            return constant;
        }

        @ModifyVariable(method = "drawBackground", ordinal = 9, at = @At(value = "STORE"))
        private int modifyHieroglyphLength(int size)
        {
            // Writing less letters for making space for the hearth cost
            if(!this.isDemonicEnchantment)
                return size;
            return size-20;
        }

        @ModifyArg(method = "drawBackground", index = 2, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/DrawContext;drawTextWrapped(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/StringVisitable;IIII)V"
        ))
        private int modifyHieroglyphPosition(int x)
        {
            // Moving the letters for making space for the hearth cost
            if(!this.isDemonicEnchantment)
                return x;
            return x+22;
        }

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
                ordinal = 3,
                shift = At.Shift.AFTER
        ))
        private void addUnsaturatedHealthRequirementRendering(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            if(!this.isDemonicEnchantment)
                return;
            drawHeartRequirement(context, this.slot, false);
        }

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
                ordinal = 6,
                shift = At.Shift.AFTER
        ))
        private void addSaturatedHealthRequirementRendering(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            if(!this.isDemonicEnchantment)
                return;
            drawHeartRequirement(context, this.slot, true);
        }

        @Unique
        private void drawHeartRequirement(DrawContext context, int slot, boolean saturated)
        {
            EnchantmentScreen self = (EnchantmentScreen)(Object)this;
            int centerWidth = (self.width - 176) / 2;
            int centerHeight = (self.height - 166) / 2;
            int offset = saturated ? 223 : 239;

            context.drawTexture(Resources.DEMONIC_ENCHANTING_TABLE_TEXTURE, centerWidth+75, centerHeight+15 + 19*slot, 48+24*slot, offset, 24, 16);
        }

        @Unique
        private _IDemonicEnchantmentScreenHandler getDemonicScreenHandler()
        {
            return ((_IDemonicEnchantmentScreenHandler)((EnchantmentScreen)(Object)this).getScreenHandler());
        }
    }
}