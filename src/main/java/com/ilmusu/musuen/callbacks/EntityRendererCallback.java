package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface EntityRendererCallback
{
    Event<EntityRendererCallback> AFTER = EventFactory.createArrayBacked(EntityRendererCallback.class,
            (listeners) -> (entity, matrices, tickDelta, provider, light) ->
            {
                for (EntityRendererCallback listener : listeners)
                    listener.handler(entity, matrices, tickDelta, provider, light);
            });

    void handler(Entity entity, MatrixStack matrices, float tickDelta, VertexConsumerProvider provider, int light);
}
