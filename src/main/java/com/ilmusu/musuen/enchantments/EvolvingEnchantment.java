package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.callbacks.ProjectileShotCallback;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.enchantment.*;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.nbt.NbtCompound;

public class EvolvingEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    private static final String EVOLVING_TAG = Resources.MOD_ID+"."+"evolving_level";

    // The multipliers for computing the actual additional attack value
    private static final float ATTACK_MULTIPLIER = 0.5F;
    // The multipliers for computing the actual additional speed value
    private static final float SPEED_MULTIPLIER = 1.0F;

    public EvolvingEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.TOOL, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
    protected boolean canAccept(Enchantment other)
    {
        return !(other instanceof OverchargeEnchantment) &&
               !(other instanceof SkeweringEnchantment) &&
               !(other instanceof DamageEnchantment) &&
               !(other instanceof ImpalingEnchantment) &&
               !(other instanceof PowerEnchantment) &&
               !(other instanceof EfficiencyEnchantment) &&
               !(other instanceof VeinMinerEnchantment) &&
               !(other instanceof UnearthingEnchantment) &&
                super.canAccept(other);
    }

    @Override
    public float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        return getEvolvingAttackDamage(stack);

    }

    protected static float getMaxEvolvingAmount(int level)
    {
        return 10.0F*level;
    }

    protected static float getEvolvingAttackDamage(ItemStack stack)
    {
        if(stack.getItem() instanceof MiningToolItem)
            return 0.0F;
        return stack.getOrCreateNbt().getFloat(EVOLVING_TAG) * ATTACK_MULTIPLIER;
    }

    @Override
    public float getAdditionalBreakSpeed(ItemStack stack, int level)
    {
        return getEvolvingBreakSpeed(stack);
    }

    protected static float getEvolvingBreakSpeed(ItemStack stack)
    {
        if(!(stack.getItem() instanceof MiningToolItem))
            return 0.0F;
        return stack.getOrCreateNbt().getFloat(EVOLVING_TAG) * SPEED_MULTIPLIER;
    }

    static
    {
        LivingEntityDamageCallback.AFTER.register(((entity, source, damage) ->
        {
            // Check the attacker, should be a living entity
            if(!(source.getAttacker() instanceof LivingEntity living))
                return 0.0F;
            // Does not activate when using a mining tool for attacking
            ItemStack stack = living.getMainHandStack();
            if(stack.getItem() instanceof MiningToolItem)
                return 0.0F;

            int level = EnchantmentHelper.getLevel(ModEnchantments.EVOLVING, stack);
            if(level == 0)
            {
                // Removing the tag just in case the enchantment was removed
                stack.removeSubNbt(EVOLVING_TAG);
                return 0.0F;
            }

            // Leveling the damage on the amount of damage
            NbtCompound nbt = stack.getOrCreateNbt();
            float evolvingAmount = nbt.getFloat(EVOLVING_TAG);
            evolvingAmount += damage * (0.0001F*level);
            nbt.putFloat(EVOLVING_TAG, Math.min(evolvingAmount, getMaxEvolvingAmount(level)));
            return 0.0F;
        }));

        PlayerBlockBreakEvents.AFTER.register(((world, player, pos, state, entity) ->
        {
            // Check the stack, should be a mining and suited for the state
            ItemStack stack = player.getMainHandStack();
            if(!(stack.getItem() instanceof MiningToolItem tool) || !(tool.isSuitableFor(state)))
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.EVOLVING, stack);
            if(level == 0)
            {
                // Removing the tag just in case the enchantment was removed
                stack.removeSubNbt(EVOLVING_TAG);
                return;
            }

            // Leveling the damage on the amount of damage
            NbtCompound nbt = stack.getOrCreateNbt();
            float evolvingAmount = nbt.getFloat(EVOLVING_TAG);
            evolvingAmount += state.getBlock().getHardness() * 0.5F * (0.0001F*level);
            nbt.putFloat(EVOLVING_TAG, evolvingAmount);
        }));

        ProjectileShotCallback.AFTER.register((shooter, stack, projectile) ->
        {
            if(!(projectile instanceof PersistentProjectileEntity projectile1))
                return;

            // The stack must have the enchantment
            int level = EnchantmentHelper.getLevel(ModEnchantments.EVOLVING, stack);
            if(level == 0)
                return;

            // Getting the additional damage
            float additionalDamage = 0;
            if(stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem)
                additionalDamage = EvolvingEnchantment.getEvolvingAttackDamage(stack);

            // Adding damage to the arrow
            projectile1.setDamage(projectile1.getDamage() + additionalDamage);
        });
    }
}
