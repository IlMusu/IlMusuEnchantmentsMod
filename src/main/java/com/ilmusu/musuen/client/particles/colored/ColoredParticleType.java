package com.ilmusu.musuen.client.particles.colored;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;

public class ColoredParticleType extends ParticleType<ColoredParticleEffect>
{
	public ColoredParticleType()
	{
		super(true, ColoredParticleEffect.DESERIALIZER);
	}

	@Override
	public Codec<ColoredParticleEffect> getCodec()
	{
		return ColoredParticleEffect.CODEC;
	}
}
