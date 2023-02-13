package com.ilmusu.ilmusuenchantments.mixins.mixin;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.callbacks.*;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEntityTrackableDrops;
import com.ilmusu.ilmusuenchantments.utils.ModUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

        @Inject(method = "onEntityHit", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getGroup()Lnet/minecraft/entity/EntityGroup;"
        ))
        public void beforeComputingEnchantmentDamage(EntityHitResult result, CallbackInfo ci)
        {
            Entity owner = ((TridentEntity)(Object)this).getOwner();
            PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.invoker().handler(owner, this.tridentStack, result.getEntity(), Hand.MAIN_HAND);
        }

        @Inject(method = "onEntityHit", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F",
            shift = At.Shift.AFTER
        ))
        public void afterComputingEnchantmentDamage(EntityHitResult result, CallbackInfo ci)
        {
            Entity owner = ((TridentEntity)(Object)this).getOwner();
            PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.invoker().handler(owner,  this.tridentStack, result.getEntity(), Hand.MAIN_HAND);
        }

        @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"
        ))
        public void onCreatedFromStack(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci)
        {
            ProjectileShotCallback.AFTER.invoker().handler(owner, stack, (TridentEntity)(Object)this);
        }
    }

    @Mixin(ArrowItem.class)
    public abstract static class ArrowItemCallbacks
    {
        @Inject(method = "createArrow", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
        public void afterArrowEntityCreated(World world, ItemStack stack, LivingEntity shooter,
            CallbackInfoReturnable<PersistentProjectileEntity> cir, ArrowEntity arrow)
        {
            ProjectileShotCallback.AFTER.invoker().handler(shooter, stack, arrow);
        }
    }

    @Mixin(CrossbowItem.class)
    public abstract static class CrossbowItemCallbacks
    {
        @Inject(method = "loadProjectiles", at = @At("HEAD"))
        private static void beforeLoadingProjectiles(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cir)
        {
            ProjectileLoadCallback.BEFORE.invoker().handler(shooter, crossbow);
        }

        @Inject(method = "shoot", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z")
        )
        private static void afterShootingProjectile(World world, LivingEntity shooter, Hand hand, ItemStack crossbow,
            ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated,
            CallbackInfo ci, boolean isFirework, ProjectileEntity projectileEntity)
        {
            ProjectileShotCallback.AFTER.invoker().handler(shooter, crossbow, projectileEntity);
        }

        @Inject(method = "postShoot", at = @At("HEAD"))
        private static void afterShootingAllProjectiles(World world, LivingEntity shooter, ItemStack stack, CallbackInfo ci)
        {
            ProjectileShotCallback.AFTER_MULTIPLE.invoker().handler(shooter, stack, null);
        }
    }

    @Mixin(PlayerEntity.class)
    public abstract static class PlayerCallbacks
    {
        @Inject(method = "attack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"
        ))
        public void beforeComputingEnchantmentDamage(Entity target, CallbackInfo ci)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.invoker().handler(player, player.getMainHandStack(), target, Hand.MAIN_HAND);
        }

        @Inject(method = "attack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"
        ))
        public void afterComputingEnchantmentDamage(Entity target, CallbackInfo ci)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.invoker().handler(player, player.getMainHandStack(), target, Hand.MAIN_HAND);
        }

        @ModifyVariable(method = "getBlockBreakingSpeed", at = @At(
            value = "TAIL",
            shift = At.Shift.BEFORE
        ))
        public float afterComputingBlockBreakingSpeed(float speed)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            BlockPos pos = ModUtils.getCurrentMiningPos(player);

            // If the player is not actually mining a block, return the default speed
            if(pos == null)
                return speed;

            // Multiplying the speed with the computed multiplier
            float multiplier = PlayerBreakSpeedCallback.AFTER.invoker().handler(player, player.getMainHandStack(), pos);
            return speed * multiplier;
        }

        @Inject(method = "tick", at = @At("TAIL"))
        public void afterPlayerTick(CallbackInfo ci)
        {
            PlayerTickCallback.BEFORE.invoker().handler((PlayerEntity)(Object)this);
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
        @Inject(method = "setStack", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"
        ))
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

        @Inject(method = "travel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V",
            shift = At.Shift.AFTER
        ))
        public void beforeElytraLanding(CallbackInfo ci)
        {
            if(this.isFallFlying())
                return;
            LivingEntityElytraLandCallback.EVENT.invoker().handler((LivingEntity)(Object)this);
        }

        @ModifyVariable(method = "handleFallDamage", at = @At(value = "LOAD", ordinal = 0))
        public int beforeApplyingFallDamageToLiving(int damage)
        {
            if(damage <= 0)
                return damage;

            LivingEntity entity = (LivingEntity)(Object)this;
            return (int)LivingEntityDamageCallback.BEFORE_FALL.invoker().handler(entity, DamageSource.FALL, damage);
        }
    }

    @Mixin(Entity.class)
    public abstract static class EntityCallbacks
    {
        @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
        ))
        public void beforeEntityDropsItemStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir, ItemEntity item)
        {
            DamageSource source = ((_IEntityTrackableDrops)this).getDeathDamageSource();
            Entity entity = (Entity)(Object)this;
            boolean shouldDrop = EntityDropCallback.EVENT.invoker().handler(entity, item, source);
            if(!shouldDrop)
                cir.setReturnValue(null);
        }
    }

    @Mixin(HeldItemRenderer.class)
    public abstract static class HeldItemRendererMixins
    {
        @Shadow private ItemStack mainHand;
        @Shadow private ItemStack offHand;

        @Inject(method = "updateHeldItems", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z",
            ordinal = 0
        ))
        public void beforeCheckingStackEquality(CallbackInfo ci)
        {
            PlayerEntity player = MinecraftClient.getInstance().player;

            // First check is for the main hand item
            ItemStack newMainHand = player.getMainHandStack();
            if(newMainHand != this.mainHand && newMainHand.getItem() == this.mainHand.getItem())
                if(shouldPreventNbtChangeAnimation(this.mainHand, newMainHand))
                    this.mainHand = newMainHand;

            // Then for the off hand item
            ItemStack newOffHand = player.getOffHandStack();
            if(newOffHand != this.offHand && newOffHand.getItem() == this.mainHand.getItem())
                if(shouldPreventNbtChangeAnimation(this.offHand, newOffHand))
                    this.offHand = newOffHand;
        }

        private static boolean shouldPreventNbtChangeAnimation(ItemStack prevStack, ItemStack newStack)
        {
            return  (newStack.hasNbt() && newStack.getNbt().getBoolean(Resources.DONT_ANIMATE_TAG)) ||
                    (prevStack.hasNbt() && prevStack.getNbt().getBoolean(Resources.DONT_ANIMATE_TAG));
        }
    }
}
