package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface KeybindInputCallback
{
    Event<KeybindInputCallback> EVENT = EventFactory.createArrayBacked(KeybindInputCallback.class,
            (listeners) -> (key, scancode, action, modifiers) ->
            {
                for (KeybindInputCallback listener : listeners)
                    listener.handler(key, scancode, action, modifiers);
            });

    void handler(int key, int scancode, int action, int modifiers);
}
