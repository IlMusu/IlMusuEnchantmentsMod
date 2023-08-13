package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public interface ShieldCoverageCallback
{
    Event<ShieldCoverageCallback> BEFORE = EventFactory.createArrayBacked(ShieldCoverageCallback.class,
            (listeners) -> (user, shield, source) ->
            {
                double dotProductResult = 0.0;
                for(ShieldCoverageCallback listener : listeners)
                   dotProductResult = listener.handler(user, shield, source);
                return dotProductResult;
            });

    double handler(LivingEntity user, ItemStack shield, DamageSource source);
}
