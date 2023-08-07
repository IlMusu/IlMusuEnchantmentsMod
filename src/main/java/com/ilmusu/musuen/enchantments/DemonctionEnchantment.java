package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModDamageTypeTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.concurrent.atomic.AtomicInteger;

public class DemonctionEnchantment extends Enchantment
{
    public DemonctionEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
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

    @SuppressWarnings("unused")
    public float getDemonicProtectionAmount(int level, DamageSource source)
    {
        return level * 0.2F;
    }

    public static float getDemonctionGranularity(int level)
    {
        return 0.5F - level/15.0F;
    }

    protected static Pair<Float, Integer> getDemonctionAmount(Iterable<ItemStack> equipment, DamageSource source)
    {
        MutableFloat totalDemonction = new MutableFloat(0.0F);
        AtomicInteger maxLevel = new AtomicInteger(0);
        for (ItemStack stack : equipment)
        {
            if (stack.isEmpty())
                continue;

            NbtList nbtList = stack.getEnchantments();
            for(int i = 0; i < nbtList.size(); ++i)
            {
                NbtCompound compound = nbtList.getCompound(i);
                Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(compound))
                    .ifPresent(enchantment -> {
                        if(enchantment instanceof DemonctionEnchantment demonction)
                        {
                            int level = EnchantmentHelper.getLevelFromNbt(compound);
                            maxLevel.set(Math.max(maxLevel.get(), level));
                            totalDemonction.add(demonction.getDemonicProtectionAmount(level, source));
                        }
                    });
            }
        }

        return new Pair<>(totalDemonction.floatValue(), maxLevel.get());
    }

    static
    {
        LivingEntityDamageCallback.BEFORE_PROTECTION.register(((entity, source, damage) ->
        {
            // Check if the source is the right one for the demonction protection
            if(!(source.isIn(ModDamageTypeTags.IS_DEMONIC)) || source.isIn(ModDamageTypeTags.BYPASSES_DEMONCTION))
                return damage;

            // Computes the total demonction on the armor
            Pair<Float, Integer> demonction = getDemonctionAmount(entity.getArmorItems(), source);
            float demonctionAmount = demonction.getLeft();
            if(demonctionAmount == 0.0F)
                return damage;

            // Returns the remaining damage
            int level = demonction.getRight();
            return Math.max(getDemonctionGranularity(level), damage-demonctionAmount);
        }));
    }
}
