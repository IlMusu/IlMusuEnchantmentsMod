package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityElytraLandCallback
{
    Event<LivingEntityElytraLandCallback> EVENT = EventFactory.createArrayBacked(LivingEntityElytraLandCallback.class,
            (listeners) -> (entity) ->
            {
                for (LivingEntityElytraLandCallback listener : listeners)
                    listener.handler(entity);
            });

    void handler(LivingEntity entity);
}
