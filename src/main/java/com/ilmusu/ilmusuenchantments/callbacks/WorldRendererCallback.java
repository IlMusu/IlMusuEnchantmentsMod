package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface WorldRendererCallback
{
    Event<WorldRendererCallback> AFTER = EventFactory.createArrayBacked(WorldRendererCallback.class,
            (listeners) -> (matrices, tickDelta, provider) ->
            {
                for (WorldRendererCallback listener : listeners)
                    listener.handler(matrices, tickDelta, provider);
            });

    void handler(MatrixStack matrices, float tickDelta, VertexConsumerProvider provider);
}
