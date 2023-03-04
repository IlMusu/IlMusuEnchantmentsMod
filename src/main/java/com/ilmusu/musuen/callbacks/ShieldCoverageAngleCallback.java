package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public interface ShieldCoverageAngleCallback
{
    Event<ShieldCoverageAngleCallback> BEFORE = EventFactory.createArrayBacked(ShieldCoverageAngleCallback.class,
            (listeners) -> (user, shield, source) ->
            {
                double angle = 0.0;
                for(ShieldCoverageAngleCallback listener : listeners)
                   angle = listener.handler(user, shield, source);
                return angle;
            });

    double handler(LivingEntity user, ItemStack shield, DamageSource source);
}
