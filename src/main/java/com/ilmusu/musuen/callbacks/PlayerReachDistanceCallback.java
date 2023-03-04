package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerReachDistanceCallback
{
    Event<PlayerReachDistanceCallback> BEFORE = EventFactory.createArrayBacked(PlayerReachDistanceCallback.class,
            (listeners) -> (player, vanilla) ->
            {
                double reach = -1;
                for (PlayerReachDistanceCallback listener : listeners)
                    reach = listener.handler(player, vanilla);
                return reach;
            });

    double handler(PlayerEntity player, double vanilla);
}
