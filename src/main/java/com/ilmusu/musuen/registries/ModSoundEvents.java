package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class ModSoundEvents
{
    public static final SoundEvent LIVING_ELYTRA_WING_FLAP = new SoundEvent(Resources.identifier("entity.living.elytra_wing_flap"));

    public static void register()
    {
        Registry.register(Registry.SOUND_EVENT, LIVING_ELYTRA_WING_FLAP.getId(), LIVING_ELYTRA_WING_FLAP);
    }
}
