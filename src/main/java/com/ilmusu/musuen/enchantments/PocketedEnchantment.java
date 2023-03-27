package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerEquipCallback;
import com.ilmusu.musuen.mixins.interfaces._IPlayerPockets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class PocketedEnchantment extends Enchantment
{
    public PocketedEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{ EquipmentSlot.LEGS });
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

    static
    {
        PlayerEquipCallback.ARMOR.register(((player, stack, slot) ->
        {
            if(player.world.isClient || slot != EquipmentSlot.LEGS)
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.POCKETED, stack);
            ((_IPlayerPockets)player).setPocketLevel(player.world, level);
        }));
    }
}
