package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Function;

public interface RegisterDamageTypeTagsCallback
{
    Event<RegisterDamageTypeTagsCallback> AFTER = EventFactory.createArrayBacked(RegisterDamageTypeTagsCallback.class,
            (listeners) -> (getOrCreateTagBuilder) ->
            {
                for (RegisterDamageTypeTagsCallback listener : listeners)
                    listener.handler(getOrCreateTagBuilder);
            });

    void handler(Function<TagKey<DamageType>, TagProvider.ProvidedTagBuilder<DamageType>> getOrCreateTagBuilder);
}
