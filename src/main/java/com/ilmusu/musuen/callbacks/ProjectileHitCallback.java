package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;

public interface ProjectileHitCallback
{
    Event<ProjectileHitCallback> AFTER = EventFactory.createArrayBacked(ProjectileHitCallback.class,
            (listeners) -> (projectile, result) ->
            {
                for (ProjectileHitCallback listener : listeners)
                    listener.handler(projectile, result);
            });

    void handler(ProjectileEntity projectile, HitResult result);
}
