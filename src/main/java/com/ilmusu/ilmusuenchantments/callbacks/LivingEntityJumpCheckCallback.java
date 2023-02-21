package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityJumpCheckCallback
{
    Event<LivingEntityJumpCheckCallback> EVENT = EventFactory.createArrayBacked(LivingEntityJumpCheckCallback.class,
            (listeners) -> (entity, jumpingCooldown) ->
            {
                boolean shouldAllowJump = false;
                for (LivingEntityJumpCheckCallback listener : listeners)
                    shouldAllowJump = listener.handler(entity, jumpingCooldown);
                return shouldAllowJump;
            });

    boolean handler(LivingEntity entity, int jumpingCooldown);
}
