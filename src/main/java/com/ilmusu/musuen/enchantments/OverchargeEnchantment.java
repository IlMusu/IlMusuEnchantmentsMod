package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.PlayerTickCallback;
import com.ilmusu.musuen.callbacks.ProjectileHitCallback;
import com.ilmusu.musuen.callbacks.ProjectileLoadCallback;
import com.ilmusu.musuen.callbacks.ProjectileShotCallback;
import com.ilmusu.musuen.mixins.interfaces._IModDamageSources;
import com.ilmusu.musuen.mixins.mixin.AccessorCrossbowItem;
import com.ilmusu.musuen.mixins.mixin.AccessorTridentEntity;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.*;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;

public class OverchargeEnchantment extends Enchantment implements _IDemonicEnchantment, _IEnchantmentExtensions
{
    private static final String OVERCHARGE_DAMAGE_TAG = Resources.MOD_ID+".overcharge_damage";

    private static final int OVERCHARGE_START_TICK = 40;
    private static final int DELTA_TIME_DAMAGE = 10;

    public OverchargeEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.CHARGEABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public int getMinLevel()
    {
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 5);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer()
    {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection()
    {
        return !ModConfigurations.isDemonicEnchantingEnabled();
    }

    @Override
    protected boolean canAccept(Enchantment other)
    {
        return !(other instanceof OverchargeEnchantment) &&
               !(other instanceof SkeweringEnchantment) &&
               !(other instanceof DamageEnchantment) &&
               !(other instanceof ImpalingEnchantment) &&
               !(other instanceof PowerEnchantment) &&
                super.canAccept(other);
    }

    private static float getPullProgress(PlayerEntity player, ItemStack stack)
    {
        if(!player.isUsingItem())
            return -1;

        if(stack.getItem() instanceof BowItem)
            return BowItem.getPullProgress(player.getItemUseTime());
        if(stack.getItem() instanceof CrossbowItem)
            return AccessorCrossbowItem.getPullProgressAccess(player.getItemUseTime(), stack);
        if(stack.getItem() instanceof TridentItem)
            return 1.0F;

        return -1;
    }

    private static int getPullTime(ItemStack stack)
    {
        if(stack.getItem() instanceof BowItem)
            return 20;
        if(stack.getItem() instanceof CrossbowItem)
            return CrossbowItem.getPullTime(stack);
        if(stack.getItem() instanceof TridentItem)
            return 0;
        return -1;
    }

    protected static float getOverchargeAdditionalDamage(LivingEntity player, ItemStack stack)
    {
        int timeSinceFullCharge = player.getItemUseTime() - OverchargeEnchantment.getPullTime(stack);
        int damagesApplied = (int)Math.ceil(Math.max(0, timeSinceFullCharge-OVERCHARGE_START_TICK)/(float)DELTA_TIME_DAMAGE);
        int level = EnchantmentHelper.getLevel(ModEnchantments.OVERCHARGED, stack);
        return damagesApplied*(0.3F + level*0.2F);
    }

    static
    {
        PlayerTickCallback.AFTER.register((player ->
        {
            if(player.world.isClient || !player.isUsingItem())
                return;

            // The item must be chargeable
            // Check if the enchantment is available
            ItemStack stack = player.getMainHandStack();
            int level = EnchantmentHelper.getLevel(ModEnchantments.OVERCHARGED, stack);
            if(level == 0)
                return;

            // If the bow is not fully charged, remove the tag just to be sure
            float progress = OverchargeEnchantment.getPullProgress(player, stack);
            if(progress < 1.0F)
                return;

            // Gets the time since the stack was fully charged
            // And checks if the damage needs to be applied
            int timeSinceFullCharge = player.getItemUseTime() - OverchargeEnchantment.getPullTime(stack);
            if(timeSinceFullCharge < OVERCHARGE_START_TICK || timeSinceFullCharge % DELTA_TIME_DAMAGE != 0)
                return;

            // Applying damage to the player
            DamageSource source = ((_IModDamageSources)player.world.getDamageSources()).demonicDamage();
            player.damage(source, 2.0F);
        }));

        ProjectileLoadCallback.BEFORE.register((shooter, stack) ->
        {
            // The stack must have the enchantment
            int level = EnchantmentHelper.getLevel(ModEnchantments.OVERCHARGED, stack);
            if(level == 0)
                return;
            // Adding additional damage to the trident to the nbt
            float additionalDamage = OverchargeEnchantment.getOverchargeAdditionalDamage(shooter, stack);
            stack.getNbt().putFloat(OVERCHARGE_DAMAGE_TAG, additionalDamage);
        });

        ProjectileShotCallback.AFTER.register((shooter, stack, projectile) ->
        {
            if(!(projectile instanceof PersistentProjectileEntity projectile1))
                return;

            // The stack must have the enchantment
            int level = EnchantmentHelper.getLevel(ModEnchantments.OVERCHARGED, stack);
            if(level == 0)
                return;

            // If trident, the damage cannot be done here, storing it
            if(stack.getItem() instanceof TridentItem)
            {
                // Adding additional damage to the trident to the nbt
                float additionalDamage = OverchargeEnchantment.getOverchargeAdditionalDamage(shooter, stack);
                stack.getNbt().putFloat(OVERCHARGE_DAMAGE_TAG, additionalDamage);
                return;
            }

            // Computing the additional damage
            float additionalDamage = 0;
            if(stack.getItem() instanceof BowItem)
                additionalDamage = OverchargeEnchantment.getOverchargeAdditionalDamage(shooter, stack);
            else if(stack.getItem() instanceof CrossbowItem)
                additionalDamage = stack.getNbt().getFloat(OVERCHARGE_DAMAGE_TAG);

            // Adding damage to the arrow
            projectile1.setDamage(projectile1.getDamage() + additionalDamage);
        });
    }

    @Override
    public float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        // The trident entity gets the damage from the enchantments
        if(stack.hasNbt())
            return stack.getNbt().getFloat(OVERCHARGE_DAMAGE_TAG);
        return 0.0F;
    }

    static
    {
        ProjectileShotCallback.AFTER_MULTIPLE.register((shooter, stack, projectile) ->
            // Remove the eventual tag of the crossbow after shooting all projectiles
            stack.removeSubNbt(OVERCHARGE_DAMAGE_TAG)
        );

        ProjectileHitCallback.AFTER.register(((projectile, result) ->
        {
            if(projectile.world.isClient || result.getType() == HitResult.Type.MISS)
                return;

            // Removing the eventual tag of the trident after landing
            if(projectile instanceof AccessorTridentEntity trident)
                trident.getTridentStack().removeSubNbt(OVERCHARGE_DAMAGE_TAG);
        }));
    }
}
