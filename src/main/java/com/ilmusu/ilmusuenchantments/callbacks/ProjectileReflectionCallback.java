package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;

public interface ProjectileReflectionCallback
{
    Event<ProjectileReflectionCallback> AFTER = EventFactory.createArrayBacked(ProjectileReflectionCallback.class,
            (listeners) -> (hit, projectile) ->
            {
                for (ProjectileReflectionCallback listener : listeners)
                    listener.handler(hit, projectile);
            });

    void handler(EntityHitResult hit, PersistentProjectileEntity projectile);
}
