package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerEquipCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.mixins.interfaces._IPlayerPockets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class PocketedEnchantment extends Enchantment
{
    public PocketedEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{ EquipmentSlot.LEGS });
        ((_IEnchantmentLevels)this).setConfigurationLevels(minLevel, maxLevel);
    }

    @Override
    public int getMinLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMinLevel();
    }

    @Override
    public int getMaxLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMaxLevel();
    }

    static
    {
        PlayerEquipCallback.ARMOR.register(((player, stack, slot) ->
        {
            if(player.getWorld().isClient || slot != EquipmentSlot.LEGS)
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.POCKETED, stack);
            ((_IPlayerPockets)player).updatePocketsLevel(player.getWorld(), level);
        }));

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) ->
        {
            ItemStack stack = handler.player.getEquippedStack(EquipmentSlot.LEGS);
            int level = EnchantmentHelper.getLevel(ModEnchantments.POCKETED, stack);
            ((_IPlayerPockets)handler.player).updatePocketsLevel(handler.player.getWorld(), level);
        }));
    }
}
