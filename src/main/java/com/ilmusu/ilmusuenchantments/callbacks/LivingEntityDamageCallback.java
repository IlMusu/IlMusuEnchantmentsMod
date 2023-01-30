package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface LivingEntityDamageCallback
{
    Event<LivingEntityDamageCallback> EVENT = EventFactory.createArrayBacked(LivingEntityDamageCallback.class,
            (listeners) -> (entity, source, damage) ->
            {
                for (LivingEntityDamageCallback listener : listeners)
                    damage = listener.handler(entity, source, damage);
                return damage;
            });

    Event<LivingEntityDamageCallback> FALL = EventFactory.createArrayBacked(LivingEntityDamageCallback.class,
            (listeners) -> (entity, source, damage) ->
            {
                for (LivingEntityDamageCallback listener : listeners)
                    damage = listener.handler(entity, source, damage);
                return damage;
            });

    float handler(LivingEntity entity, DamageSource source, float damage);
}
