package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

public interface EntityItemDropCallback
{
    Event<EntityItemDropCallback> BEFORE = EventFactory.createArrayBacked(EntityItemDropCallback.class,
            (listeners) -> (entity, item, source) ->
            {
                for (EntityItemDropCallback listener : listeners)
                    if(!listener.handler(entity, item, source))
                        return false;
                return true;
            });

    boolean handler(Entity entity, ItemEntity item, @Nullable DamageSource source);
}
