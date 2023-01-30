package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;

public interface FireworkElytraSpeedCallback
{
    Event<FireworkElytraSpeedCallback> EVENT = EventFactory.createArrayBacked(FireworkElytraSpeedCallback.class,
            (listeners) -> (player, firework, rotation) ->
            {
                for (FireworkElytraSpeedCallback listener : listeners)
                    rotation = listener.handler(player, firework, rotation);
                return rotation;
            });

    Vec3d handler(LivingEntity shooter, FireworkRocketEntity firework, Vec3d rotation);
}
