package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.PlayerAttackCallback;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ImpalingEnchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkeweringEnchantment extends Enchantment implements _IDemonicEnchantment, _IEnchantmentExtensions
{
    private static final String NBT_DAMAGE_TAG = Resources.MOD_ID+".skewering_additional_damage";

    public SkeweringEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.TRIDENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 5);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer()
    {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection()
    {
        return false;
    }

    @Override
    protected boolean canAccept(Enchantment other)
    {
        return !(other instanceof SkeweringEnchantment) &&
               !(other instanceof ImpalingEnchantment) &&
               !(other instanceof OverchargeEnchantment);
    }

    @Override
    public float getAdditionalAttackDamage(ItemStack stack, int level, EntityGroup group)
    {
        return stack.getOrCreateNbt().getFloat(NBT_DAMAGE_TAG);
    }

    public float getDamageForHealthConsumed(float health, float level)
    {
        return (level*0.2F) + health*(2.0F+level*0.2F);
    }

    static
    {
        PlayerAttackCallback.BEFORE_ENCHANTMENT_DAMAGE.register((player, stack, entity, hand) ->
        {
            if (!(player instanceof LivingEntity living) || player.world.isClient)
                return;

            Map<Enchantment, Integer> allEnchantments = EnchantmentHelper.get(stack);
            List<Enchantment> skeweringEnchantments = new ArrayList<>(allEnchantments.keySet());
            skeweringEnchantments.removeIf((enchantment -> !(enchantment instanceof SkeweringEnchantment)));

            if(skeweringEnchantments.size() == 0)
                return;

            float additionalDamage = 0.0F;
            for (Enchantment enchantment : skeweringEnchantments)
            {
                int level = allEnchantments.get(enchantment);
                if (level == 0)
                    continue;

                // The percentage of health to consume at the current level of the enchantment
                // Not using min and max levels so that the enchantment is able to scale
                float percentage = new ModUtils.Linear(1, 0.10F, 5, 0.25F).of(level);
                // Getting the amount of actually consumed health and the related damage
                float consumed = _IDemonicEnchantment.consumeHealthValue(living, percentage, true);
                additionalDamage += ((SkeweringEnchantment) enchantment).getDamageForHealthConsumed(consumed, level);
            }

            // Setting the additional damage before attacking the entity
            if(additionalDamage > 0.0F)
                stack.getOrCreateNbt().putFloat(NBT_DAMAGE_TAG, additionalDamage);
        });

        PlayerAttackCallback.AFTER_ENCHANTMENT_DAMAGE.register(((player, stack, entity, hand) ->
            stack.removeSubNbt(NBT_DAMAGE_TAG))
        );
    }
}
