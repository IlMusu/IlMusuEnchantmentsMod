package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.callbacks.PlayerAttackCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEnchantmentExtensions;
import com.ilmusu.ilmusuenchantments.utils.ModUtils;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LacerationEnchantment extends DamageEnchantment implements _IDemonicEnchantment, _IEnchantmentExtensions
{
    private static final String NBT_DAMAGE_TAG = Resources.MOD_ID+".laceration_additional_damage";

    public LacerationEnchantment(Enchantment.Rarity weight, int typeIndex)
    {
        super(weight, typeIndex, EquipmentSlot.MAINHAND);
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group)
    {
        // The base attack damage for this enchantment is 0
        return 0;
    }

    @Override
    public float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        return stack.getOrCreateNbt().getFloat(NBT_DAMAGE_TAG);
    }

    public float getDamageForHealthConsumed(float health, float level)
    {
        return (level*0.5F) + health*(4.0F+level);
    }

    static
    {
        PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.register((player, stack, entity, hand) ->
        {
            if (!(player instanceof LivingEntity living) || player.world.isClient)
                return;

            Map<Enchantment, Integer> allEnchantments = EnchantmentHelper.get(stack);
            List<Enchantment> lacerationEnchantments = new ArrayList<>(allEnchantments.keySet());
            lacerationEnchantments.removeIf((enchantment -> !(enchantment instanceof LacerationEnchantment)));

            float additionalDamage = 0.0F;
            for (Enchantment enchantment : lacerationEnchantments)
            {
                int level = allEnchantments.get(enchantment);
                if (level == 0)
                   continue;

                // The percentage of health to consume at the current level of the enchantment
                float percentage = new ModUtils.Linear(enchantment.getMinLevel(), 0.05F, enchantment.getMaxLevel(), 0.10F).of(level);
                // Getting the amount of actually consumed health and the related damage
                float consumed = _IDemonicEnchantment.consumeHealthValue(living, percentage, true);
                additionalDamage += ((LacerationEnchantment) enchantment).getDamageForHealthConsumed(consumed, level);
            }

            // Setting the additional damage before attacking the entity
            stack.getOrCreateNbt().putFloat(NBT_DAMAGE_TAG, additionalDamage);
        });

        PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.register(((player, stack, entity, hand) ->
            stack.removeSubNbt(NBT_DAMAGE_TAG))
        );
    }
}
