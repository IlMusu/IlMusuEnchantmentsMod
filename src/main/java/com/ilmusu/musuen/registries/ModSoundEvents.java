package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class ModSoundEvents
{
    public static final SoundEvent LIVING_ELYTRA_WING_FLAP = SoundEvent.of(Resources.identifier("entity.living.elytra_wing_flap"));
    public static final SoundEvent LIVING_STOMACH_RUMBLE = SoundEvent.of(Resources.identifier("entity.living.stomach_rumble"));

    public static void register()
    {
        Registry.register(Registries.SOUND_EVENT, LIVING_ELYTRA_WING_FLAP.getId(), LIVING_ELYTRA_WING_FLAP);
        Registry.register(Registries.SOUND_EVENT, LIVING_STOMACH_RUMBLE.getId(), LIVING_STOMACH_RUMBLE);
    }
}
