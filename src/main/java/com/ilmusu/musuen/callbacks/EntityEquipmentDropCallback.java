package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;

public interface EntityEquipmentDropCallback
{
    Event<EntityEquipmentDropCallback> EVENT = EventFactory.createArrayBacked(EntityEquipmentDropCallback.class,
            (listeners) -> (entity, source, lootingMultiplier, allowDrops) ->
            {
                for (EntityEquipmentDropCallback listener : listeners)
                    listener.handler(entity, source, lootingMultiplier, allowDrops);
            });

    void handler(Entity entity, DamageSource source, int lootingMultiplier, boolean allowDrops);
}
