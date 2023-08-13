package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;

public interface FireworkElytraSpeedCallback
{
    Event<FireworkElytraSpeedCallback> EVENT = EventFactory.createArrayBacked(FireworkElytraSpeedCallback.class,
            (listeners) -> (player, firework, velocity) ->
            {
                for (FireworkElytraSpeedCallback listener : listeners)
                    velocity = listener.handler(player, firework, velocity);
                return velocity;
            });

    Vec3d handler(LivingEntity shooter, FireworkRocketEntity firework, Vec3d velocity);
}
