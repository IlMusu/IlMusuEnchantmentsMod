package com.ilmusu.ilmusuenchantments.mixins.mixin;

import com.ilmusu.ilmusuenchantments.callbacks.*;
import com.ilmusu.ilmusuenchantments.utils.ModUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

public abstract class CustomCallbacksMixins
{
    @Mixin(TridentEntity.class)
    public abstract static class TridentEntityCallbacks
    {
        @Shadow private ItemStack tridentStack;

        @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getGroup()Lnet/minecraft/entity/EntityGroup;"))
        public void beforeComputingEnchantmentDamage(EntityHitResult result, CallbackInfo ci)
        {
            Entity owner = ((TridentEntity)(Object)this).getOwner();
            PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.invoker().handler(owner, this.tridentStack, result.getEntity(), Hand.MAIN_HAND);
        }

        @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F", shift = At.Shift.AFTER))
        public void afterComputingEnchantmentDamage(EntityHitResult result, CallbackInfo ci)
        {
            Entity owner = ((TridentEntity)(Object)this).getOwner();
            PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.invoker().handler(owner,  this.tridentStack, result.getEntity(), Hand.MAIN_HAND);
        }
    }

    @Mixin(PlayerEntity.class)
    public abstract static class PlayerCallbacks
    {
        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
        public void beforeComputingEnchantmentDamage(Entity target, CallbackInfo ci)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.invoker().handler(player, player.getMainHandStack(), target, Hand.MAIN_HAND);
        }

        @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
        public void afterComputingEnchantmentDamage(Entity target, CallbackInfo ci)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.invoker().handler(player, player.getMainHandStack(), target, Hand.MAIN_HAND);
        }

        @ModifyVariable(method = "getBlockBreakingSpeed", at = @At(value = "TAIL", shift = At.Shift.BEFORE))
        public float afterComputingBlockBreakingSpeed(float speed)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            BlockPos pos = ModUtils.getCurrentMiningPos(player);

            // If the player is not actually mining a block, return the default speed
            if(pos == null)
                return speed;

            // Multiplying the speed with the computed multiplier
            float multiplier = PlayerBreakSpeedCallback.AFTER_VANILLA_COMPUTATION.invoker().handler(player, player.getMainHandStack(), pos);
            return speed * multiplier;
        }

        @Inject(method = "tick", at = @At("TAIL"))
        public void afterPlayerTick(CallbackInfo ci)
        {
            PlayerTickCallback.AFTER.invoker().handler((PlayerEntity)(Object)this);
        }

        @Inject(method = "tick", at = @At("HEAD"))
        public void beforePlayerTick(CallbackInfo ci)
        {
            PlayerTickCallback.AFTER.invoker().handler((PlayerEntity)(Object)this);
        }
    }

    @Mixin(GameRenderer.class)
    public static abstract class GameRendererCallbacks
    {
        private PlayerFovMultiplierCallback.FovParams params;

        @ModifyVariable(method = "updateFovMultiplier", at = @At("STORE"))
        protected float afterComputingNewFovMultiplier(float multiplier)
        {
            PlayerEntity player = MinecraftClient.getInstance().player;
            this.params = PlayerFovMultiplierCallback.AFTER.invoker().handler(player);

            if(this.params.shouldNotChange())
                return multiplier;

            return multiplier * this.params.getMultiplier();
        }

        @ModifyConstant(method = "updateFovMultiplier", constant = @Constant(floatValue = 0.5F))
        protected float beforeUpdatingCurrentFovMultiplier(float constant)
        {
            return this.params.getUpdateVelocityOr(constant);
        }

        @ModifyConstant(method = "updateFovMultiplier", constant = @Constant(floatValue = 1.5F))
        protected float beforeClampingCurrentFovMultiplier(float constant)
        {
            if(this.params.isUnclamped())
                return 2000.0F;
            return constant;
        }
    }

    @Mixin(Keyboard.class)
    public static abstract class KeyboardCallbacks
    {
        @Inject(method = "onKey", at = @At(value = "RETURN", ordinal = 4))
        protected void afterHandlingVanillaKeybindings(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci)
        {
            KeyInputCallback.EVENT.invoker().handler(key, scancode, action, modifiers);
        }
    }

    @Mixin(PlayerInventory.class)
    public abstract static class PlayerInventoryCallbacks
    {
        @Inject(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
        public void afterSettingStackInPlayerInventory(int slot, ItemStack stack, CallbackInfo ci, DefaultedList<ItemStack> list)
        {
            PlayerInventory inventory = (PlayerInventory)(Object)this;
            if(list != inventory.armor)
                return;

            EquipmentSlot equipmentSlot = EquipmentSlot.values()[2+slot];
            PlayerEquipArmorCallback.EVENT.invoker().handler(inventory.player, stack, equipmentSlot);
        }
    }

    @Mixin(FireworkRocketEntity.class)
    public abstract static class FireworkRocketEntityCallbacks
    {
        @Shadow private @Nullable LivingEntity shooter;

        @ModifyVariable(method = "tick", at = @At("STORE"))
        public Vec3d beforeComputingElytraVelocity(Vec3d rotation)
        {
            return FireworkElytraSpeedCallback.EVENT.invoker().handler(this.shooter, (FireworkRocketEntity)(Object)this, rotation);
        }
    }

    @Mixin(LivingEntity.class)
    public abstract static class LivingEntityCallbacks
    {
        @Shadow public abstract boolean isFallFlying();

        @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V", shift = At.Shift.AFTER))
        public void beforeElytraLanding(CallbackInfo ci)
        {
            LivingEntity entity = (LivingEntity)(Object)this;
            if(this.isFallFlying())
                return;

            LivingEntityElytraLandCallback.EVENT.invoker().handler((LivingEntity)(Object)this);
        }

        @Inject(method = "damage", at = @At(value = "HEAD"))
        public void beforeApplyingDamageToLiving(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
        {
            LivingEntity entity = (LivingEntity)(Object)this;
            LivingEntityDamageCallback.EVENT.invoker().handler(entity, source, amount);
        }

        @ModifyVariable(method = "handleFallDamage", at = @At(value = "LOAD", ordinal = 0))
        public int beforeApplyingFallDamageToLiving(int damage)
        {
            if(damage <= 0)
                return damage;

            LivingEntity entity = (LivingEntity)(Object)this;
            return (int)LivingEntityDamageCallback.FALL.invoker().handler(entity, DamageSource.FALL, damage);
        }
    }
}
