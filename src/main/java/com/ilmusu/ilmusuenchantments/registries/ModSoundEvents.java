package com.ilmusu.ilmusuenchantments.registries;

import com.ilmusu.ilmusuenchantments.Resources;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class ModSoundEvents
{
    public static final SoundEvent LIVING_ELYTRA_WING_FLAP = SoundEvent.of(Resources.identifier("entity.living.elytra_wing_flap"));

    public static void register()
    {
        Registry.register(Registries.SOUND_EVENT, LIVING_ELYTRA_WING_FLAP.getId(), LIVING_ELYTRA_WING_FLAP);
    }
}
