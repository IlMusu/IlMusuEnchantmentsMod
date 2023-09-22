package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.*;
import com.ilmusu.musuen.mixins.interfaces._IEntityDeathSource;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.networking.messages.PlayerJumpMessage;
import com.ilmusu.musuen.registries.ModDamageSources;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.spawner.PhantomSpawner;
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
        private void beforeComputingEnchantmentDamage(EntityHitResult result, CallbackInfo ci)
        {
            Entity owner = ((TridentEntity)(Object)this).getOwner();
            PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.invoker().handler(owner, this.tridentStack, result.getEntity(), Hand.MAIN_HAND);
        }

        @Inject(method = "onEntityHit", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F",
                shift = At.Shift.AFTER
        ))
        private void afterComputingEnchantmentDamage(EntityHitResult result, CallbackInfo ci)
        {
            Entity owner = ((TridentEntity)(Object)this).getOwner();
            PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.invoker().handler(owner, this.tridentStack, result.getEntity(), Hand.MAIN_HAND);
        }
    }

    @Mixin(TridentItem.class)
    public abstract static class TridentItemCallbacks
    {
        @Inject(method = "onStoppedUsing", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
                shift = At.Shift.AFTER
        ))
        private void afterShootingTrident(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci,
                                          PlayerEntity player, int i, int j, TridentEntity projectile)
        {
            ProjectileShotCallback.AFTER.invoker().handler(user, ((AccessorTridentEntity)projectile).getTridentStack(), projectile);
        }
    }

    @Mixin(BowItem.class)
    public abstract static class ArrowItemCallbacks
    {
        @Inject(method = "onStoppedUsing", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
                shift = At.Shift.AFTER
        ))
        private void afterArrowEntityCreated(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci,
                                             PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f,
                                             boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity projectile)
        {
            ProjectileShotCallback.AFTER.invoker().handler(user, stack, projectile);
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
                target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
                shift = At.Shift.AFTER
        ))
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

    @Mixin(PersistentProjectileEntity.class)
    public abstract static class PersistentProjectileEntityCallbacks
    {
        @Inject(method = "onEntityHit", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Entity;setFireTicks(I)V",
                shift = At.Shift.AFTER
        ))
        private void beforeProjectileReflected(EntityHitResult hit, CallbackInfo ci)
        {
            ProjectileReflectionCallback.BEFORE.invoker().handler(hit, (PersistentProjectileEntity)(Object)this);
        }
    }

    @Mixin(PlayerEntity.class)
    public abstract static class PlayerCallbacks
    {
        @Inject(method = "attack", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"
        ))
        private void beforeComputingEnchantmentDamage(Entity target, CallbackInfo ci)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.invoker().handler(player, player.getMainHandStack(), target, Hand.MAIN_HAND);
        }

        @Inject(method = "attack", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"
        ))
        private void afterComputingEnchantmentDamage(Entity target, CallbackInfo ci)
        {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.invoker().handler(player, player.getMainHandStack(), target, Hand.MAIN_HAND);
        }

        @ModifyVariable(method = "getBlockBreakingSpeed", at = @At(
                value = "TAIL",
                shift = At.Shift.BEFORE
        ))
        private float afterComputingBlockBreakingSpeed(float speed)
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

        @Inject(method = "handleFallDamage", at = @At("HEAD"))
        private void onPlayerLandingOnBlock(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir)
        {
            PlayerLandCallback.EVENT.invoker().handler((PlayerEntity)(Object)this, fallDistance);
        }

        @Inject(method = "tick", at = @At("HEAD"))
        private void afterPlayerTick(CallbackInfo ci)
        {
            PlayerTickCallback.BEFORE.invoker().handler((PlayerEntity)(Object)this);
        }

        @Inject(method = "tick", at = @At("TAIL"))
        private void beforePlayerTick(CallbackInfo ci)
        {
            PlayerTickCallback.AFTER.invoker().handler((PlayerEntity)(Object)this);
        }

        @Inject(method = "equipStack", at = @At("TAIL"))
        private void afterEquippingStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci)
        {
            if(slot.getType() == EquipmentSlot.Type.ARMOR)
                PlayerEquipCallback.ARMOR.invoker().handler((PlayerEntity) (Object) this, stack, slot);
            else if(slot == EquipmentSlot.MAINHAND)
                PlayerEquipCallback.MAINHAND.invoker().handler((PlayerEntity) (Object) this, stack, slot);
        }

        @Inject(method = "dropInventory", at = @At("TAIL"))
        private void afterDroppingInventory(CallbackInfo ci)
        {
            PlayerDropInventoryCallback.AFTER.invoker().handler((PlayerEntity)(Object)this);
        }
    }

    @Mixin(PlayerInventory.class)
    public abstract static class PlayerInventoryCallbacks
    {
        @Inject(method = "setStack", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"
        ))
        private void afterSettingStackInPlayerInventory(int slot, ItemStack stack, CallbackInfo ci, DefaultedList<ItemStack> list)
        {
            PlayerInventory inventory = (PlayerInventory)(Object)this;
            if(list != inventory.armor)
                return;

            EquipmentSlot equipmentSlot = EquipmentSlot.values()[2+slot];
            PlayerEquipCallback.ARMOR.invoker().handler(inventory.player, stack, equipmentSlot);
        }
    }

    @Mixin(FireworkRocketEntity.class)
    public abstract static class FireworkRocketEntityCallbacks
    {
        @Shadow private @Nullable LivingEntity shooter;

        @ModifyVariable(method = "tick", at = @At("STORE"))
        private Vec3d beforeComputingElytraVelocity(Vec3d rotation)
        {
            return FireworkElytraSpeedCallback.EVENT.invoker().handler(this.shooter, (FireworkRocketEntity)(Object)this, rotation);
        }
    }

    @Mixin(ProjectileEntity.class)
    public abstract static class ProjectileEntityCallback
    {
        @Inject(method = "onCollision", at = @At("TAIL"))
        private void afterCollision(HitResult hitResult, CallbackInfo ci)
        {
            ProjectileHitCallback.AFTER.invoker().handler((ProjectileEntity)(Object)this, hitResult);
        }
    }

    @Mixin(LivingEntity.class)
    public abstract static class LivingEntityCallbacks
    {
        @Shadow public abstract boolean isFallFlying();
        @Shadow protected abstract void jump();

        @Shadow private int jumpingCooldown;
        @Shadow protected boolean jumping;

        @Unique private static boolean shieldTriedToBlock;

        @Inject(method = "travel", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V",
                shift = At.Shift.AFTER
        ))
        private void beforeElytraLanding(CallbackInfo ci)
        {
            if(this.isFallFlying())
                return;
            LivingEntityElytraLandCallback.EVENT.invoker().handler((LivingEntity)(Object)this);
        }

        @ModifyVariable(method = "applyArmorToDamage", at = @At(value = "LOAD", ordinal = 2), argsOnly = true)
        private float beforeApplyingProtectionToDamage(float damage, DamageSource source)
        {
            if(damage <= 0)
                return damage;

            LivingEntity entity = (LivingEntity)(Object)this;
            return LivingEntityDamageCallback.BEFORE_PROTECTION.invoker().handler(entity, source, damage);
        }

        @ModifyVariable(method = "handleFallDamage", at = @At(value = "LOAD", ordinal = 0))
        private int beforeApplyingFallDamageToLiving(int damage, float fallDistance, float damageMultiplier, DamageSource damageSource)
        {
            if(damage <= 0)
                return damage;

            LivingEntity entity = (LivingEntity)(Object)this;
            return (int)LivingEntityDamageCallback.BEFORE_FALL.invoker().handler(entity, damageSource, damage);
        }

        @Inject(method = "applyDamage", at = @At(value = "RETURN", ordinal = 2))
        private void afterApplyingDamage(DamageSource source, float amount, CallbackInfo ci)
        {
            LivingEntity entity = (LivingEntity)(Object)this;
            LivingEntityDamageCallback.AFTER.invoker().handler(entity, source, amount);
        }

        @Inject(method = "tickMovement", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
                ordinal = 2
        ))
        private void afterJumpCheck(CallbackInfo ci)
        {
            LivingEntity entity = (LivingEntity)(Object)this;
            if(entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().flying)
                return;
            if(!this.jumping || entity.isOnGround() || entity.getBlockStateAtPos().getBlock() instanceof FluidBlock)
                return;

            if(LivingEntityAirJumpCallback.EVENT.invoker().handler(entity, this.jumpingCooldown))
            {
                // Making the player perform another jump
                this.jump();
                new PlayerJumpMessage().sendToServer();
                // The jump cooldown needs to be set correctly
                this.jumpingCooldown = 10;
                // Also resetting the fall distance
                entity.fallDistance = 0.0F;
            }
        }

        @Inject(method = "jump", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z")
        )
        private void onLivingJump(CallbackInfo ci)
        {
            LivingEntity entity = (LivingEntity)(Object)this;
            Vec3d velocity = LivingEntityJumpCallback.EVENT.invoker().handler(entity, entity.getVelocity());
            entity.setVelocity(velocity);
        }

        @Inject(method = "blockedByShield", at = @At(value = "HEAD"))
        private void afterShieldFailedToBlockResetHook(DamageSource source, CallbackInfoReturnable<Boolean> cir)
        {
            LivingEntityCallbacks.shieldTriedToBlock = false;
        }

        @Inject(method = "blockedByShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;dotProduct(Lnet/minecraft/util/math/Vec3d;)D"))
        private void afterShieldFailedToBlockSetHook(DamageSource source, CallbackInfoReturnable<Boolean> cir)
        {
            LivingEntityCallbacks.shieldTriedToBlock = true;
        }

        @Inject(method = "blockedByShield", at = @At("TAIL"), cancellable = true)
        private void afterShieldFailedToBlock(DamageSource source, CallbackInfoReturnable<Boolean> cir)
        {
            // Check if the shield tried to block the projectile but failed because of angle
            if(!LivingEntityCallbacks.shieldTriedToBlock)
                return;

            LivingEntity user = (LivingEntity)(Object)this;

            Vec3d vec3d2 = user.getRotationVec(1.0f);
            Vec3d vec3d3 = source.getPosition().relativize(user.getPos()).normalize().multiply(1, 0, 1);

            // Considering the new coverage angle
            double angle = ShieldCoverageCallback.BEFORE.invoker().handler(user, user.getActiveItem(), source);
            if (vec3d3.dotProduct(vec3d2) < angle)
                cir.setReturnValue(true);
        }

        @Inject(method = "dropEquipment", at = @At("HEAD"))
        private void onDroppingEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci)
        {
            LivingEntity dropper = (LivingEntity)(Object)this;
            EntityEquipmentDropCallback.EVENT.invoker().handler(dropper, source, lootingMultiplier, allowDrops);
        }
    }

    @Mixin(LivingEntity.class)
    public abstract static class ClientLivingEntityCallbacks
    {
        @Shadow private @Nullable DamageSource lastDamageSource;

        @Inject(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTime()J"))
        private void overrideDamageSourceWhenDemonic(byte status, CallbackInfo ci)
        {
            LivingEntity user = (LivingEntity)(Object)this;
            if(user != MinecraftClient.getInstance().player)
                return;

            NbtCompound nbt = ((_IEntityPersistentNbt)user).getPNBT();
            if(!nbt.contains("was_damage_demonic"))
                return;

            this.lastDamageSource = ModDamageSources.DEMONIC_DAMAGE;
            nbt.remove("was_damage_demonic");
        }
    }

    @Mixin(PhantomSpawner.class)
    public abstract static class PhantomSpawnerCallbacks
    {
        @Unique private static ServerPlayerEntity player;

        @ModifyVariable(method = "spawn", at = @At(value = "LOAD", ordinal = 0))
        private PlayerEntity beforeSpawningPhantom(PlayerEntity player)
        {
            PhantomSpawnerCallbacks.player = (ServerPlayerEntity) player;
            return player;
        }

        @ModifyVariable(method = "spawn", ordinal = 1, at = @At(value = "STORE"))
        private int beforeSpawningPhantom(int insomnia)
        {
            insomnia = (int) PhantomSpawnCallback.BEFORE.invoker().handler(PhantomSpawnerCallbacks.player, insomnia);
            return Math.max(1, insomnia);
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
        private void beforeEntityDropsItemStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir, ItemEntity item)
        {
            DamageSource source = ((_IEntityDeathSource)this).getDeathDamageSource();
            Entity entity = (Entity)(Object)this;
            boolean shouldDrop = EntityItemDropCallback.BEFORE.invoker().handler(entity, item, source);
            if(!shouldDrop)
                cir.setReturnValue(null);
        }
    }

    @Mixin(ItemEntity.class)
    public abstract static class ItemEntityCallbacks
    {
        @Inject(method = "setStack", at = @At("TAIL"))
        private void onEntityCreation(ItemStack stack, CallbackInfo ci)
        {
            ItemEntityStackCallback.EVENT.invoker().handler((ItemEntity)(Object)this, stack);
        }
    }

    @Mixin(ItemStack.class)
    public abstract static class ItemStackCallbacks
    {
        @Inject(method = "use", at = @At("HEAD"))
        private void onItemStackUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir)
        {
            ItemStack stack = (ItemStack)(Object)this;
            PlayerItemUseCallback.EVENT.invoker().handler(user, hand, stack);
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
        private void beforeCheckingStackEquality(CallbackInfo ci)
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

        @Unique
        private static boolean shouldPreventNbtChangeAnimation(ItemStack prevStack, ItemStack newStack)
        {
            return  (newStack.hasNbt() && newStack.getNbt().getBoolean(Resources.DONT_ANIMATE_TAG)) ||
                    (prevStack.hasNbt() && prevStack.getNbt().getBoolean(Resources.DONT_ANIMATE_TAG));
        }
    }

    @Mixin(EntityRenderDispatcher.class)
    public abstract static class EntityRenderDispatcherCallbacks<E extends Entity>
    {
        @Inject(method = "render", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                shift = At.Shift.AFTER
        ))
        private void afterRenderingEntity(E entity, double x, double y, double z, float yaw, float tickDelta,
                                          MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
        {
            EntityRendererCallback.AFTER.invoker().handler(entity, matrices, tickDelta, vertexConsumers, light);
        }
    }

    @Mixin(GameRenderer.class)
    public static abstract class GameRendererCallbacks
    {
        @Shadow private float fovMultiplier;
        @Shadow private float lastFovMultiplier;
        @Unique private static PlayerFovMultiplierCallback.FovParams fovParams;

        @Unique private static float hurtTiltDumper = 1.0F;

        @ModifyVariable(method = "updateFovMultiplier", index = 1, at = @At(value = "STORE", ordinal = 1))
        private float afterGettingCameraFovMultiplier(float multiplier)
        {
            PlayerEntity player = MinecraftClient.getInstance().player;
            fovParams = PlayerFovMultiplierCallback.AFTER.invoker().handler(player);

            if(fovParams.shouldNotChange())
                return multiplier;

            return multiplier * fovParams.getAdditionalMultiplier();
        }

        @Inject(method = "updateFovMultiplier", at = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/render/GameRenderer;fovMultiplier:F",
                ordinal = 3,
                shift = At.Shift.AFTER
        ))
        private void modifyUpdateSpeed(CallbackInfo ci)
        {
            if(fovParams.shouldNotChange())
                return;

            float delta = (this.fovMultiplier - this.lastFovMultiplier) * 2;
            float deltaSpeed = (fovParams.getUpdateVelocity() - 0.5F);
            this.fovMultiplier += delta * deltaSpeed;
        }

        @Inject(method = "updateFovMultiplier", cancellable = true, at = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/render/GameRenderer;fovMultiplier:F",
                ordinal = 4
        ))
        private void beforeClampingFov(CallbackInfo ci)
        {
            if(fovParams.isUnclamped())
                ci.cancel();
        }

        @Inject(method = "bobViewWhenHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"))
        private void retrieveHurtTiltDumper(MatrixStack matrices, float tickDelta, CallbackInfo ci)
        {
            GameRendererCallbacks.hurtTiltDumper = PlayerHurtTiltCallback.EVENT.invoker().handler(MinecraftClient.getInstance().player);
        }

        @SuppressWarnings("InvalidInjectorMethodSignature")
        @ModifyVariable(method = "bobViewWhenHurt", index = 5, at = @At(value = "STORE", ordinal = 1))
        private float changeHurtTiltingAmountYaw(float tiltYaw)
        {
            return tiltYaw * GameRendererCallbacks.hurtTiltDumper;
        }

        @ModifyVariable(method = "bobViewWhenHurt", index = 4, at = @At(value = "STORE", ordinal = 2))
        private float changeHurtTiltingAmountPitch(float tiltYaw)
        {
            return tiltYaw * GameRendererCallbacks.hurtTiltDumper;
        }
    }

    @Mixin(InGameHud.class)
    public static abstract class InGameHudCallbacks
    {
        @Inject(method = "render", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"
        ))
        private void afterRenderingOverlays(MatrixStack matrices, float tickDelta, CallbackInfo ci)
        {
            HudRenderCallback.AFTER_OVERLAYS.invoker().handler(matrices, tickDelta);
        }
    }
}