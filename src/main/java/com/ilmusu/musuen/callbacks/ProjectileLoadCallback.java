package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface ProjectileLoadCallback
{
    Event<ProjectileLoadCallback> BEFORE = EventFactory.createArrayBacked(ProjectileLoadCallback.class,
            (listeners) -> (shooter, projectileContainer) ->
            {
                for (ProjectileLoadCallback listener : listeners)
                    listener.handler(shooter, projectileContainer);
            });

    void handler(LivingEntity shooter, ItemStack projectileContainer);
}
