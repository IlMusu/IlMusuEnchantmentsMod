package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface EntityRendererCallback
{
    Event<EntityRendererCallback> AFTER = EventFactory.createArrayBacked(EntityRendererCallback.class,
            (listeners) -> (entity, matrices, tickDelta, provider) ->
            {
                for (EntityRendererCallback listener : listeners)
                    listener.handler(entity, matrices, tickDelta, provider);
            });

    void handler(Entity entity, MatrixStack matrices, float tickDelta, VertexConsumerProvider provider);
}
