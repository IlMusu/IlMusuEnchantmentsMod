package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerLandCallback
{
    Event<PlayerLandCallback> EVENT = EventFactory.createArrayBacked(PlayerLandCallback.class,
            (listeners) -> (player, fallDistance) ->
            {
                for (PlayerLandCallback listener : listeners)
                    listener.handler(player, fallDistance);
            });

    void handler(PlayerEntity player, float fallDistance);
}
