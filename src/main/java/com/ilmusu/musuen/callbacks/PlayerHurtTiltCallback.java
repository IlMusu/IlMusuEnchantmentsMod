package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerHurtTiltCallback
{
    Event<PlayerHurtTiltCallback> EVENT = EventFactory.createArrayBacked(PlayerHurtTiltCallback.class,
            (listeners) -> (player) ->
            {
                float dumper = 1.0F;
                for (PlayerHurtTiltCallback listener : listeners)
                    dumper *= listener.handler(player);
                return dumper;
            });

    float handler(PlayerEntity player);
}
