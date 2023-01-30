package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerTickCallback
{
    Event<PlayerTickCallback> BEFORE = EventFactory.createArrayBacked(PlayerTickCallback.class,
            (listeners) -> (player) ->
            {
                for (PlayerTickCallback listener : listeners)
                    listener.handler(player);
            });

    Event<PlayerTickCallback> AFTER = EventFactory.createArrayBacked(PlayerTickCallback.class,
            (listeners) -> (player) ->
            {
                for (PlayerTickCallback listener : listeners)
                    listener.handler(player);
            });

    void handler(PlayerEntity player);
}
