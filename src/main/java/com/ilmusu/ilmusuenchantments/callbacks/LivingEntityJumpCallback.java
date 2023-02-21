package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public interface LivingEntityJumpCallback
{
    Event<LivingEntityJumpCallback> EVENT = EventFactory.createArrayBacked(LivingEntityJumpCallback.class,
            (listeners) -> (entity, velocity) ->
            {
                for (LivingEntityJumpCallback listener : listeners)
                    velocity = listener.handler(entity, velocity);
                return velocity;
            });

    Vec3d handler(LivingEntity entity, Vec3d velocity);
}
