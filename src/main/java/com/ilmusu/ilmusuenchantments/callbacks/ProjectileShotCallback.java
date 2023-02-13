package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ProjectileShotCallback
{
    Event<ProjectileShotCallback> AFTER = EventFactory.createArrayBacked(ProjectileShotCallback.class,
            (listeners) -> (shooter, projectileContainer, projectile) ->
            {
                for (ProjectileShotCallback listener : listeners)
                    listener.handler(shooter, projectileContainer, projectile);
            });

    Event<ProjectileShotCallback> AFTER_MULTIPLE = EventFactory.createArrayBacked(ProjectileShotCallback.class,
            (listeners) -> (shooter, projectileContainer, projectile) ->
            {
                for(ProjectileShotCallback listener : listeners)
                    listener.handler(shooter, projectileContainer, projectile);
            });

    void handler(@Nullable LivingEntity shooter, ItemStack projectileContainer, @Nullable ProjectileEntity projectile);
}
