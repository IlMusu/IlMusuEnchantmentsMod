package com.ilmusu.musuen.client.particles.colored_enchant;

import com.ilmusu.musuen.registries.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

import java.awt.*;
import java.util.Locale;

public class ColoredGlyphParticleEffect implements ParticleEffect
{
    public static final Codec<ColoredGlyphParticleEffect> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
                    Codec.FLOAT.fieldOf("r").forGetter((data) -> data.red),
                    Codec.FLOAT.fieldOf("g").forGetter((data) -> data.green),
                    Codec.FLOAT.fieldOf("b").forGetter((data) -> data.blue),
                    Codec.FLOAT.fieldOf("a").forGetter((data) -> data.alpha),
                    Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size))
            .apply(builder, (r, g, b, a, s) -> new ColoredGlyphParticleEffect(r, g, b, a).size(s)));

    @SuppressWarnings("deprecation")
    public static final Factory<ColoredGlyphParticleEffect> DESERIALIZER = new Factory<>()
    {
        public ColoredGlyphParticleEffect read(ParticleType<ColoredGlyphParticleEffect> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' '); float r = reader.readFloat();
            reader.expect(' '); float g = reader.readFloat();
            reader.expect(' '); float b = reader.readFloat();
            reader.expect(' '); float a = reader.readFloat();
            reader.expect(' '); float size = reader.readFloat();
            return new ColoredGlyphParticleEffect(r, g, b, a).size(size);
        }

        public ColoredGlyphParticleEffect read(ParticleType<ColoredGlyphParticleEffect> particleTypeIn, PacketByteBuf buffer)
        {
            float r = buffer.readFloat();
            float g = buffer.readFloat();
            float b = buffer.readFloat();
            float a = buffer.readFloat();
            float size = buffer.readFloat();
            return new ColoredGlyphParticleEffect(r, g, b, a).size(size);
        }
    };

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    private float size = 1;

    public ColoredGlyphParticleEffect(float red, float green, float blue, float alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ColoredGlyphParticleEffect(Color color)
    {
        this.red = color.getRed() / 255.0F;
        this.green = color.getGreen() / 255.0F;
        this.blue = color.getBlue() / 255.0F;
        this.alpha = color.getAlpha() / 255.0F;
    }

    public ColoredGlyphParticleEffect size(float size)
    {
        this.size = size;
        return this;
    }

    @Override
    public ParticleType<?> getType()
    {
        return ModParticles.COLORED_GLYPH;
    }

    @Override
    public void write(PacketByteBuf buffer)
    {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.alpha);
        buffer.writeFloat(this.size);
    }

    @Override
    public String asString()
    {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f", ModParticles.COLORED_GLYPH,
                this.red, this.green, this.blue, this.alpha, this.size);
    }

    @Environment(EnvType.CLIENT)
    public float getRed()
    {
        return this.red;
    }

    @Environment(EnvType.CLIENT)
    public float getGreen()
    {
        return this.green;
    }

    @Environment(EnvType.CLIENT)
    public float getBlue()
    {
        return this.blue;
    }

    @Environment(EnvType.CLIENT)
    public float getAlpha()
    {
        return this.alpha;
    }

    @Environment(EnvType.CLIENT)
    public float getSize()
    {
        return this.size;
    }
}
