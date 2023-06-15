package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.mixins.interfaces._IDemonicEnchantmentScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class DemonicEnchantingTableRenderingMixin
{
    @Mixin(EnchantmentScreen.class)
    public abstract static class HeartRequirementsRendering
    {
        private int musuen$slot = 0;
        private boolean musuen$isDemonicEnchantment = false;

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/screen/EnchantmentScreenHandler;getLapisCount()I"
        ))
        private void drawBackgroundEnchantmentPreHook(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            // Resetting the slot count before entering the for-loop cycle
            this.musuen$slot = -1;
        }

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/screen/ingame/EnchantingPhrases;generatePhrase(Lnet/minecraft/client/font/TextRenderer;I)Lnet/minecraft/text/StringVisitable;"
        ))
        private void drawBackgroundEnchantmentHook(DrawContext matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            // Increasing the slot count, this is called at every iteration
            this.musuen$slot += 1;
            // Storing if the enchantment for slot is demonic so that this is done only once
            this.musuen$isDemonicEnchantment = getDemonicScreenHandler().hasDemonicEnchantment(this.musuen$slot);
        }

        @ModifyVariable(method = "drawBackground", ordinal = 10, at = @At(value = "STORE"))
        private int modifyHieroglyphLength(int size)
        {
            // Writing less letters for making space for the hearth cost
            if(!this.musuen$isDemonicEnchantment)
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
            if(!this.musuen$isDemonicEnchantment)
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
            if(!this.musuen$isDemonicEnchantment)
                return;
            drawHeartRequirement(context, this.musuen$slot, false);
        }

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
                ordinal = 6,
                shift = At.Shift.AFTER
        ))
        private void addSaturatedHealthRequirementRendering(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            if(!this.musuen$isDemonicEnchantment)
                return;
            drawHeartRequirement(context, this.musuen$slot, true);
        }

        private void drawHeartRequirement(DrawContext context, int slot, boolean saturated)
        {
            EnchantmentScreen self = (EnchantmentScreen)(Object)this;
            int centerWidth = (self.width - 176) / 2;
            int centerHeight = (self.height - 166) / 2;
            int offset = saturated ? 223 : 239;

            context.drawTexture(Resources.DEMONIC_ENCHANTING_TABLE_TEXTURE, centerWidth+75, centerHeight+15 + 19*slot, 48+24*slot, offset, 24, 16);
        }

        private _IDemonicEnchantmentScreenHandler getDemonicScreenHandler()
        {
            return ((_IDemonicEnchantmentScreenHandler)((EnchantmentScreen)(Object)this).getScreenHandler());
        }
    }
}