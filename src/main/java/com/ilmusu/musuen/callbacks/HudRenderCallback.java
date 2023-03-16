package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;

public interface HudRenderCallback
{
    Event<HudRenderCallback> AFTER_OVERLAYS = EventFactory.createArrayBacked(HudRenderCallback.class,
            (listeners) -> (matrices, tickDelta) ->
            {
                for (HudRenderCallback listener : listeners)
                    listener.handler(matrices, tickDelta);
            });

    void handler(MatrixStack matrices, float tickDelta);
}
