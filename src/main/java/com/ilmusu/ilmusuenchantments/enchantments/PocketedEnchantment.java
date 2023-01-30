package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.callbacks.PlayerEquipArmorCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IPlayerPockets;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
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
    public int getMaxLevel()
    {
        return 5;
    }

    static
    {
        PlayerEquipArmorCallback.EVENT.register(((player, stack, slot) ->
        {
            if(player.world.isClient || slot != EquipmentSlot.LEGS)
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.POCKETED, stack);
            ((_IPlayerPockets)player).setPocketLevel(player.world, level);
        }));
    }
}
