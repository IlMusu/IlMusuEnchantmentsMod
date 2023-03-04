package com.ilmusu.musuen.client.particles.eblock;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;

public class BlockParticleType extends ParticleType<BlockParticleEffect>
{
    public BlockParticleType()
    {
        super(true, BlockParticleEffect.DESERIALIZER);
    }

    @Override
    public Codec<BlockParticleEffect> getCodec()
    {
        return BlockParticleEffect.CODEC;
    }
}
