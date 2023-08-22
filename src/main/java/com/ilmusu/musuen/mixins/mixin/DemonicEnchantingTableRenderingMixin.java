package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.mixins.interfaces._IDemonicEnchantmentScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        @Unique private int slot = 0;
        @Unique private boolean isDemonicEnchantment = false;

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/screen/EnchantmentScreenHandler;getLapisCount()I"
        ))
        private void drawBackgroundEnchantmentPreHook(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
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

        @ModifyVariable(method = "drawBackground", ordinal = 10, at = @At(value = "STORE"))
        private int modifyHieroglyphLength(int size)
        {
            // Writing less letters for making space for the hearth cost
            if(!this.isDemonicEnchantment)
                return size;
            return size-20;
        }

        @ModifyArg(method = "drawBackground", index = 2, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/font/TextRenderer;drawTrimmed(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/StringVisitable;IIII)V"
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
                target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                ordinal = 3,
                shift = At.Shift.AFTER
        ))
        private void addUnsaturatedHealthRequirementRendering(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            if(!this.isDemonicEnchantment)
                return;
            drawHeartRequirement(matrices, this.slot, false);
        }

        @Inject(method = "drawBackground", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                ordinal = 6,
                shift = At.Shift.AFTER
        ))
        private void addSaturatedHealthRequirementRendering(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            if(!this.isDemonicEnchantment)
                return;
            drawHeartRequirement(matrices, this.slot, true);
        }

        @Unique
        private void drawHeartRequirement(MatrixStack matrices, int slot, boolean saturated)
        {
            EnchantmentScreen self = (EnchantmentScreen)(Object)this;
            int centerWidth = (self.width - 176) / 2;
            int centerHeight = (self.height - 166) / 2;
            int offset = saturated ? 223 : 239;

            RenderSystem.setShaderTexture(0, Resources.DEMONIC_ENCHANTING_TABLE_TEXTURE);
            DrawableHelper.drawTexture(matrices, centerWidth+75, centerHeight+15 + 19*slot, 48+24*slot, offset, 24, 16);
        }

        @Unique
        private _IDemonicEnchantmentScreenHandler getDemonicScreenHandler()
        {
            return ((_IDemonicEnchantmentScreenHandler)((EnchantmentScreen)(Object)this).getScreenHandler());
        }
    }
}