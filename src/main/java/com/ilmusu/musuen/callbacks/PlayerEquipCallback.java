package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface PlayerEquipCallback
{
    Event<PlayerEquipCallback> ARMOR = EventFactory.createArrayBacked(PlayerEquipCallback.class,
            (listeners) -> (player, stack, slot) ->
            {
                for (PlayerEquipCallback listener : listeners)
                    listener.handler(player, stack, slot);
            });

    Event<PlayerEquipCallback> MAINHAND = EventFactory.createArrayBacked(PlayerEquipCallback.class,
            (listeners) -> (player, stack, slot) ->
            {
                for (PlayerEquipCallback listener : listeners)
                    listener.handler(player, stack, slot);
            });

    void handler(PlayerEntity player, ItemStack stack, EquipmentSlot slot);
}
