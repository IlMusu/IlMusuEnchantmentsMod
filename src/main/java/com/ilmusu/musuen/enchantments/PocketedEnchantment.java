package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerEquipCallback;
import com.ilmusu.musuen.mixins.interfaces._IPlayerPockets;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class PocketedEnchantment extends Enchantment
{
    public PocketedEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{ EquipmentSlot.LEGS });
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

    static
    {
        PlayerEquipCallback.ARMOR.register(((player, stack, slot) ->
        {
            if(player.world.isClient || slot != EquipmentSlot.LEGS)
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
