package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface PlayerAttackCallback
{
    Event<PlayerAttackCallback> BEFORE_ENCHANTMENT_DAMAGE = EventFactory.createArrayBacked(PlayerAttackCallback.class,
            (listeners) -> (owner, stack, entity, hand) ->
            {
                for (PlayerAttackCallback listener : listeners)
                    listener.handler(owner, stack, entity, hand);
            });

    Event<PlayerAttackCallback> AFTER_ENCHANTMENT_DAMAGE = EventFactory.createArrayBacked(PlayerAttackCallback.class,
            (listeners) -> (owner, stack, entity, hand) ->
            {
                for (PlayerAttackCallback listener : listeners)
                    listener.handler(owner, stack, entity, hand);
            });

    void handler(Entity owner, ItemStack stack, Entity entity, Hand hand);
}
