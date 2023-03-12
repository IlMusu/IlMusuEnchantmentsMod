package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;

public interface RegisterDamageTypesCallback
{
    Event<RegisterDamageTypesCallback> AFTER = EventFactory.createArrayBacked(RegisterDamageTypesCallback.class,
            (listeners) -> (registerable) ->
            {
                for (RegisterDamageTypesCallback listener : listeners)
                    listener.handler(registerable);
            });

    void handler(Registerable<DamageType> registerable);
}
