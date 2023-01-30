package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface PlayerEquipArmorCallback
{
    Event<PlayerEquipArmorCallback> EVENT = EventFactory.createArrayBacked(PlayerEquipArmorCallback.class,
            (listeners) -> (player, stack, slot) ->
            {
                for (PlayerEquipArmorCallback listener : listeners)
                    listener.handler(player, stack, slot);
            });

    void handler(PlayerEntity player, ItemStack stack, EquipmentSlot slot);
}
