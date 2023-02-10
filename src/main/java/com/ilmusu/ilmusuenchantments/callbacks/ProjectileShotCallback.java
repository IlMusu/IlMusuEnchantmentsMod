package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ProjectileShotCallback
{
    Event<ProjectileShotCallback> AFTER_ARROW = EventFactory.createArrayBacked(ProjectileShotCallback.class,
            (listeners) -> (shooter, stack, projectile) ->
            {
                for (ProjectileShotCallback listener : listeners)
                    listener.handler(shooter, stack, projectile);
            });

    Event<ProjectileShotCallback> AFTER_TRIDENT = EventFactory.createArrayBacked(ProjectileShotCallback.class,
            (listeners) -> (shooter, stack, trident) ->
            {
                for (ProjectileShotCallback listener : listeners)
                   listener.handler(shooter, stack, trident);
            });

    void handler(@Nullable Entity shooter, ItemStack stack, ProjectileEntity projectile);
}
