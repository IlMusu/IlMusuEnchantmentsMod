package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface PlayerBreakSpeedCallback
{
    Event<PlayerBreakSpeedCallback> AFTER = EventFactory.createArrayBacked(PlayerBreakSpeedCallback.class,
            (listeners) -> (player, stack, pos) ->
            {
                float multiplier = 1;
                for (PlayerBreakSpeedCallback listener : listeners)
                    multiplier *= listener.handler(player, stack, pos);
                return multiplier;
            });

    float handler(PlayerEntity player, ItemStack stack, BlockPos pos);
}
