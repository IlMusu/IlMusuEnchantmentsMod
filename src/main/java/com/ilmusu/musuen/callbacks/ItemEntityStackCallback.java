package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface ItemEntityStackCallback
{
    Event<ItemEntityStackCallback> EVENT = EventFactory.createArrayBacked(ItemEntityStackCallback.class,
            (listeners) -> (entity, stack) ->
            {
                for (ItemEntityStackCallback listener : listeners)
                    listener.handler(entity, stack);
            });

    void handler(ItemEntity entity, ItemStack stack);
}
