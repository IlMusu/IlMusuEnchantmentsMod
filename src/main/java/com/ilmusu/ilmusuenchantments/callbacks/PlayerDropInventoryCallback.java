package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerDropInventoryCallback
{
    Event<PlayerDropInventoryCallback> AFTER = EventFactory.createArrayBacked(PlayerDropInventoryCallback.class,
            (listeners) -> (player) ->
            {
                for (PlayerDropInventoryCallback listener : listeners)
                    listener.handler(player);
            });

    void handler(PlayerEntity player);
}
