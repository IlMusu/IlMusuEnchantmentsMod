package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerPhantomSpawnCallback
{
    Event<PlayerPhantomSpawnCallback> BEFORE = EventFactory.createArrayBacked(PlayerPhantomSpawnCallback.class,
            (listeners) -> (player, insomniaAmount) ->
            {
                for (PlayerPhantomSpawnCallback listener : listeners)
                    insomniaAmount *= listener.handler(player, insomniaAmount);
                return insomniaAmount;
            });

    int handler(PlayerEntity player, int insomniaAmount);
}
