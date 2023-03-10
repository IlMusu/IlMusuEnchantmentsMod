package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityAirJumpCallback
{
    Event<LivingEntityAirJumpCallback> EVENT = EventFactory.createArrayBacked(LivingEntityAirJumpCallback.class,
            (listeners) -> (entity, jumpingCooldown) ->
            {
                boolean shouldAllowJump = false;
                for (LivingEntityAirJumpCallback listener : listeners)
                    shouldAllowJump = listener.handler(entity, jumpingCooldown);
                return shouldAllowJump;
            });

    boolean handler(LivingEntity entity, int jumpingCooldown);
}
