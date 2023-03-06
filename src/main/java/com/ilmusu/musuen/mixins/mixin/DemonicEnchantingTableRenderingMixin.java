package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.client.particles.colored_enchant.ColoredGlyphParticleEffect;
import com.ilmusu.musuen.mixins.interfaces._IDemonicEnchantmentScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

public abstract class DemonicEnchantingTableRenderingMixin
{
    @Mixin(EnchantmentScreen.class)
    public abstract static class HeartRequirementsRendering
    {
        private static final Identifier TEXTURE = Resources.identifier("textures/gui/container/enchanting_table.png");

        private int slot = 0;
        private boolean isDemonicEnchantment = false;

        @Inject(method = "drawBackground", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/EnchantmentScreenHandler;getLapisCount()I"
        ))
        public void drawBackgroundEnchantmentPreHook(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            // Resetting the slot count before entering the for-loop cycle
            this.slot = -1;
        }

        @Inject(method = "drawBackground", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;setZOffset(I)V"
        ))
        public void drawBackgroundEnchantmentHook(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            // Increasing the slot count, this is called at every iteration
            this.slot += 1;
            // Storing if the enchantment for slot is demonic so that this is done only once
            this.isDemonicEnchantment = getDemonicScreenHandler().hasDemonicEnchantment(this.slot);
        }

        @ModifyVariable(method = "drawBackground", ordinal = 10, at = @At(value = "STORE"))
        public int modifyHieroglyphLength(int size)
        {
            // Writing less letters for making space for the hearth cost
            if(!this.isDemonicEnchantment)
                return size;
            return size-20;
        }

        @ModifyArg(method = "drawBackground", index = 1, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;drawTrimmed(Lnet/minecraft/text/StringVisitable;IIII)V"
        ))
        public int modifyHieroglyphPosition(int x)
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
        public void addUnsaturatedHealthRequirementRendering(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
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
        public void addSaturatedHealthRequirementRendering(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            if(!this.isDemonicEnchantment)
                return;
            drawHeartRequirement(matrices, this.slot, true);
        }

        private void drawHeartRequirement(MatrixStack matrices, int slot, boolean saturated)
        {
            EnchantmentScreen self = (EnchantmentScreen)(Object)this;
            int centerWidth = (self.width - 176) / 2;
            int centerHeight = (self.height - 166) / 2;
            int offset = saturated ? 223 : 239;

            RenderSystem.setShaderTexture(0, TEXTURE);
            ((EnchantmentScreen)(Object)this).drawTexture(matrices, centerWidth+75, centerHeight+15 + 19*slot, 48+24*slot, offset, 24, 16);
        }

        private _IDemonicEnchantmentScreenHandler getDemonicScreenHandler()
        {
            return ((_IDemonicEnchantmentScreenHandler)((EnchantmentScreen)(Object)this).getScreenHandler());
        }
    }

    @Mixin(EnchantingTableBlock.class)
    public abstract static class AddDemonicEnchantingGlyphParticles
    {
        @Inject(method = "randomDisplayTick", at = @At("TAIL"))
        public void addDemonicEnchantingGlyphParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
        {
            for(BlockPos offset : _IDemonicEnchantmentScreenHandler.SKULLS_OFFSETS)
            {
                if(random.nextInt(8) != 0 || !_IDemonicEnchantmentScreenHandler.isValidSkull(world.getBlockState(pos.add(offset))))
                    continue;

                Vec3d pos0 = new Vec3d(pos.getX() + 0.5, (double)pos.getY() + 2.0, (double)pos.getZ() + 0.5);
                Vec3d vel = new Vec3d(offset.getX()+random.nextFloat()-0.5, offset.getY()-random.nextFloat()-1.0, offset.getZ()+random.nextFloat()-0.5);
                ParticleEffect effect = new ColoredGlyphParticleEffect(new Color(107, 15, 15));
                world.addParticle(effect, pos0.x, pos0.y, pos0.z, vel.x, vel.y, vel.z);
            }
        }
    }
}
