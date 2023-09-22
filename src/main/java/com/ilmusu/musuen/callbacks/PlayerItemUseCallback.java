package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface PlayerItemUseCallback
{
    Event<PlayerItemUseCallback> EVENT = EventFactory.createArrayBacked(PlayerItemUseCallback.class,
            (listeners) -> (player, hand, stack) ->
            {
                for (PlayerItemUseCallback listener : listeners)
                    listener.handler(player, hand, stack);
            });

    void handler(PlayerEntity player, Hand hand, ItemStack stack);
}
