package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.entity.damage.DemonicDamageSource;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;

public class DemonctionEnchantment extends Enchantment
{
    public DemonctionEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 0);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 5);
    }

    public float getDemonicProtectionAmount(int level, DamageSource source)
    {
        return level * 0.2F;
    }

    protected static float getDemonctionAmount(Iterable<ItemStack> equipment, DamageSource source)
    {
        MutableFloat totalDemonction = new MutableFloat(0.0F);
        for (ItemStack stack : equipment)
        {
            if (stack.isEmpty())
                continue;

            NbtList nbtList = stack.getEnchantments();
            for(int i = 0; i < nbtList.size(); ++i)
            {
                NbtCompound compound = nbtList.getCompound(i);
                Registry.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(compound))
                    .ifPresent(enchantment -> {
                        if(enchantment instanceof DemonctionEnchantment demonction)
                        {
                            int level = EnchantmentHelper.getLevelFromNbt(compound);
                            totalDemonction.add(demonction.getDemonicProtectionAmount(level, source));
                        }
                    });
            }
        }

        return totalDemonction.floatValue();
    }

    static
    {
        LivingEntityDamageCallback.BEFORE_PROTECTION.register(((entity, source, damage) ->
        {
            // The demonction protects until the damage is 0.5F
            if(damage <= 0.5F)
                return damage;
            // Check if the source is the right one for the demonction protection
            if(!(source instanceof DemonicDamageSource demonicSource) || demonicSource.bypassesDemonction())
                return damage;

            // Computes the total demonction on the armor
            float demonctionAmount = getDemonctionAmount(entity.getArmorItems(), source);
            if(demonctionAmount == 0.0F)
                return damage;

            // Returns the remaining damage
            return Math.max(0.5F, damage-demonctionAmount);
        }));
    }
}
