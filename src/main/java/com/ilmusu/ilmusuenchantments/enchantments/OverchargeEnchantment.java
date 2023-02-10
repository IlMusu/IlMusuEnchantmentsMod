package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.callbacks.ProjectileShotCallback;
import com.ilmusu.ilmusuenchantments.callbacks.PlayerTickCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEnchantmentExtensions;
import com.ilmusu.ilmusuenchantments.registries.ModDamageSources;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.text.Text;

public class OverchargeEnchantment extends Enchantment implements _IDemonicEnchantment, _IEnchantmentExtensions
{
    private static final String OVERCHARGE_START_AGE_TAG = Resources.MOD_ID+".overcharge_start_age";
    private static final String OVERCHARGE_STEPS = Resources.MOD_ID+".overcharge_steps";
    private static final String OVERCHARGE_DAMAGE = Resources.MOD_ID+".overcharge_damage";

    public OverchargeEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack)
    {
        return  EnchantmentTarget.BOW.isAcceptableItem(stack.getItem()) ||
                EnchantmentTarget.CROSSBOW.isAcceptableItem(stack.getItem()) ||
                EnchantmentTarget.TRIDENT.isAcceptableItem(stack.getItem());
    }

    private static float getPullProgress(PlayerEntity player, ItemStack stack)
    {
        if(!player.isUsingItem())
            return -1;

        if(stack.getItem() instanceof BowItem)
            return BowItem.getPullProgress(stack.getItem().getMaxUseTime(stack) - player.getItemUseTimeLeft());
        if(stack.getItem() instanceof CrossbowItem)
            return CrossbowItem.getPullProgress(stack.getItem().getMaxUseTime(stack) - player.getItemUseTimeLeft(), stack);
        if(stack.getItem() instanceof TridentItem)
            return 1.0F;

        return -1;
    }

    protected static float getDemonicDamage(ItemStack stack)
    {
        if(!stack.hasNbt())
            return 0;

        int level = EnchantmentHelper.getLevel(ModEnchantments.OVERCHARGED, stack);
        int steps = stack.getNbt().getInt(OVERCHARGE_STEPS);
        return steps*(0.3F + level*0.2F);
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

            // Prevent the stack equip animation when changing the stack nbt data
            stack.getNbt().putBoolean(Resources.DONT_ANIMATE_TAG, true);

            // Initializing the start age at the end of the bow loading
            if(!stack.getOrCreateNbt().contains(OVERCHARGE_START_AGE_TAG))
                stack.getNbt().putInt(OVERCHARGE_START_AGE_TAG, player.age);

            // The delta time between the overcharging steps
            int overchargingTicks = player.age - stack.getNbt().getInt(OVERCHARGE_START_AGE_TAG);
            if(overchargingTicks % 20 != 19)
                return;

            // Removing and eventual overcharge damage tag
            stack.getNbt().remove(OVERCHARGE_DAMAGE);
            // Increasing the overcharging_steps
            int steps = stack.getOrCreateNbt().getInt(OVERCHARGE_STEPS)+1;
            stack.getNbt().putInt(OVERCHARGE_STEPS, steps+1);
            player.damage(ModDamageSources.DEMONIC_DAMAGE, 1.0F);
        }));

        ProjectileShotCallback.AFTER_ARROW.register(((player, stack, arrow) ->
        {
            if(stack.hasNbt() && stack.getNbt().contains(OVERCHARGE_START_AGE_TAG))
            {
                float additional = OverchargeEnchantment.getDemonicDamage(stack);
                ((ArrowEntity)arrow).setDamage(((ArrowEntity)arrow).getDamage() + additional);

                // Removing all the additional tags in the chargeable stack
                stack.getNbt().remove(Resources.DONT_ANIMATE_TAG);
                stack.getNbt().remove(OVERCHARGE_START_AGE_TAG);
                stack.getNbt().remove(OVERCHARGE_STEPS);
            }
        }));

        ProjectileShotCallback.AFTER_TRIDENT.register(((player, stack, projectile) ->
        {
            if(stack.hasNbt() && stack.getNbt().contains(OVERCHARGE_START_AGE_TAG))
            {
                // Putting the damage tag
                stack.getNbt().putFloat(OVERCHARGE_DAMAGE, OverchargeEnchantment.getDemonicDamage(stack));
                // Removing all the additional tags in the chargeable stack
                stack.getNbt().remove(Resources.DONT_ANIMATE_TAG);
                stack.getNbt().remove(OVERCHARGE_START_AGE_TAG);
                stack.getNbt().remove(OVERCHARGE_STEPS);
            }
        }));
    }

    @Override
    public float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        // The trident entity gets the damage from the enchantments
        return stack.getNbt().getFloat(OVERCHARGE_DAMAGE);
    }
}
