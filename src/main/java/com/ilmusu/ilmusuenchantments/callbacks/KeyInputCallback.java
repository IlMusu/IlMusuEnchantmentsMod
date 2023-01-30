package com.ilmusu.ilmusuenchantments.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface KeyInputCallback
{
    Event<KeyInputCallback> EVENT = EventFactory.createArrayBacked(KeyInputCallback.class,
            (listeners) -> (key, scancode, action, modifiers) ->
            {
                for (KeyInputCallback listener : listeners)
                    listener.handler(key, scancode, action, modifiers);
            });

    void handler(int key, int scancode, int action, int modifiers);
}
