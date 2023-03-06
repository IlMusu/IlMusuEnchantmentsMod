package com.ilmusu.musuen.client.particles.colored_enchant;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;

public class ColoredGlyphParticleType extends ParticleType<ColoredGlyphParticleEffect>
{
    public ColoredGlyphParticleType()
    {
        super(true, ColoredGlyphParticleEffect.DESERIALIZER);
    }

    @Override
    public Codec<ColoredGlyphParticleEffect> getCodec()
    {
        return ColoredGlyphParticleEffect.CODEC;
    }
}
