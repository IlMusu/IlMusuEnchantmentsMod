package com.ilmusu.musuen.client.particles.eblock;

import com.ilmusu.musuen.registries.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

import java.util.Locale;

public class BlockParticleEffect implements ParticleEffect
{
    public static final Codec<BlockParticleEffect> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
                    Codec.INT.fieldOf("state").forGetter((data) -> Block.STATE_IDS.getRawId(data.state)),
                    Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size),
                    Codec.FLOAT.fieldOf("gravity").forGetter((data) -> data.gravity),
                    Codec.INT.fieldOf("life").forGetter((data) -> data.life))
            .apply(builder, (id, s, gr, l) -> new BlockParticleEffect(Block.STATE_IDS.get(id)).size(s).life(l).gravity(gr)));

    @SuppressWarnings("deprecation")
    public static final Factory<BlockParticleEffect> DESERIALIZER = new Factory<>()
    {
        public BlockParticleEffect read(ParticleType<BlockParticleEffect> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' '); BlockState state = Block.STATE_IDS.get(reader.readInt());
            reader.expect(' '); float size = reader.readFloat();
            reader.expect(' '); float gravity = reader.readFloat();
            reader.expect(' '); int life = reader.readInt();
            return new BlockParticleEffect(state).size(size).life(life).gravity(gravity);
        }

        public BlockParticleEffect read(ParticleType<BlockParticleEffect> particleTypeIn, PacketByteBuf buffer)
        {
            BlockState state = Block.STATE_IDS.get(buffer.readInt());
            float size = buffer.readFloat();
            float gravity = buffer.readFloat();
            int life = buffer.readInt();
            return new BlockParticleEffect(state).size(size).life(life).gravity(gravity);
        }
    };

    private final BlockState state;

    private float size = 1;
    private float gravity = 0.0F;
    private int life = 20;

    public BlockParticleEffect(BlockState state)
    {
        this.state = state;
    }

    public BlockParticleEffect size(float size)
    {
        this.size = size;
        return this;
    }

    public BlockParticleEffect life(int life)
    {
        this.life = life;
        return this;
    }

    public BlockParticleEffect gravity(float gravity)
    {
        this.gravity = gravity;
        return this;
    }

    @Override
    public ParticleType<?> getType()
    {
        return ModParticles.BLOCK;
    }

    @Override
    public void write(PacketByteBuf buffer)
    {
        buffer.writeInt(Block.STATE_IDS.getRawId(this.state));
        buffer.writeFloat(this.size);
        buffer.writeFloat(this.gravity);
        buffer.writeInt(this.life);
    }

    @Override
    public String asString()
    {
        return String.format(Locale.ROOT, "%s %d %.2f %.2f %d", ModParticles.COLORED,
                Block.STATE_IDS.getRawId(this.state), this.size, this.gravity, this.life);
    }

    @Environment(EnvType.CLIENT)
    public BlockState getState()
    {
        return this.state;
    }

    @Environment(EnvType.CLIENT)
    public float getSize()
    {
        return this.size;
    }

    @Environment(EnvType.CLIENT)
    public int getLife()
    {
        return this.life;
    }

    @Environment(EnvType.CLIENT)
    public float getGravity()
    {
        return this.gravity;
    }
}
