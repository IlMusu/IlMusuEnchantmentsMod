package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class EvolvingEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    private static final String EVOLVING_TAG = Resources.MOD_ID+"."+"evolving_level";

    public EvolvingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        return !(other instanceof DamageEnchantment) &&
                super.canAccept(other);
    }

    @Override
    public float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        return stack.getOrCreateNbt().getFloat(EVOLVING_TAG);
    }

    static
    {
        LivingEntityDamageCallback.AFTER.register(((entity, source, damage) ->
        {
            // Check the attacker, should be a living entity
            if(!(source.getAttacker() instanceof LivingEntity living))
                return 0.0F;

            ItemStack stack = living.getMainHandStack();
            int level = EnchantmentHelper.getLevel(ModEnchantments.EVOLVING, stack);
            if(level == 0)
            {
                // Removing the ta just in case the enchantment was removed
                stack.removeSubNbt(EVOLVING_TAG);
                return 0.0F;
            }

            // Leveling the damage on the amount of damage
            NbtCompound nbt = stack.getOrCreateNbt();
            float evolvingAmount = nbt.getFloat(EVOLVING_TAG);
            evolvingAmount += damage * (0.0001F*level);
            nbt.putFloat(EVOLVING_TAG, evolvingAmount);
            return 0.0F;
        }));
    }
}
