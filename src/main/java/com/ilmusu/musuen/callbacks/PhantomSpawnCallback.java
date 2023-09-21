package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PhantomSpawnCallback
{
    Event<PhantomSpawnCallback> BEFORE = EventFactory.createArrayBacked(PhantomSpawnCallback.class,
            (listeners) -> (player, insomniaAmount) ->
            {
                for (PhantomSpawnCallback listener : listeners)
                    insomniaAmount *= listener.handler(player, insomniaAmount);
                return insomniaAmount;
            });

    float handler(PlayerEntity player, float insomniaAmount);
}
