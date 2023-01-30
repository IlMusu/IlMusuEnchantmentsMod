package com.ilmusu.ilmusuenchantments.client.particles.colored;

import com.ilmusu.ilmusuenchantments.registries.ModParticles;
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

public class ColoredParticleEffect implements ParticleEffect
{
	public static final Codec<ColoredParticleEffect> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
			Codec.FLOAT.fieldOf("r").forGetter((data) -> data.red),
			Codec.FLOAT.fieldOf("g").forGetter((data) -> data.green),
			Codec.FLOAT.fieldOf("b").forGetter((data) -> data.blue),
			Codec.FLOAT.fieldOf("a").forGetter((data) -> data.alpha),
			Codec.FLOAT.fieldOf("size").forGetter((data) -> data.size),
			Codec.INT.fieldOf("life").forGetter((data) -> data.life),
			Codec.FLOAT.fieldOf("gravity").forGetter((data) -> data.gravity))
			.apply(builder, (r, g, b, a, s, l, gr) -> new ColoredParticleEffect(r, g, b, a).size(s).life(l).gravity(gr)));

	@SuppressWarnings("deprecation")
	public static final Factory<ColoredParticleEffect> DESERIALIZER = new Factory<>()
	{
		public ColoredParticleEffect read(ParticleType<ColoredParticleEffect> particleTypeIn, StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' '); float r = (float)reader.readDouble();
			reader.expect(' '); float g = (float)reader.readDouble();
			reader.expect(' '); float b = (float)reader.readDouble();
			reader.expect(' '); float a = (float)reader.readDouble();
			reader.expect(' '); float size = (float)reader.readDouble();
			reader.expect(' '); int life = reader.readInt();
			reader.expect(' '); float gravity = (float)reader.readDouble();
			return new ColoredParticleEffect(r, g, b, a).size(size).life(life).gravity(gravity);
		}

		public ColoredParticleEffect read(ParticleType<ColoredParticleEffect> particleTypeIn, PacketByteBuf buffer)
		{
			float r = (float)buffer.readDouble();
			float g = (float)buffer.readDouble();
			float b = (float)buffer.readDouble();
			float a = (float)buffer.readDouble();
			float size = (float)buffer.readDouble();
			int life = buffer.readInt();
			float gravity = (float)buffer.readDouble();
			return new ColoredParticleEffect(r, g, b, a).size(size).life(life).gravity(gravity);
		}
	};

	private final float red;
	private final float green;
	private final float blue;
	private final float alpha;

	private float size = 1;
	private int life = 20;
	private float gravity = 0.0F;

	public ColoredParticleEffect(float red, float green, float blue, float alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public ColoredParticleEffect(Color color)
	{
		this.red = color.getRed() / 255.0F;
		this.green = color.getGreen() / 255.0F;
		this.blue = color.getBlue() / 255.0F;
		this.alpha = color.getAlpha() / 255.0F;
	}

	public ColoredParticleEffect size(float size)
	{
		this.size = size;
		return this;
	}

	public ColoredParticleEffect life(int life)
	{
		this.life = life;
		return this;
	}

	public ColoredParticleEffect gravity(float gravity)
	{
		this.gravity = gravity;
		return this;
	}

	@Override
	public ParticleType<?> getType()
	{
		return ModParticles.COLORED;
	}

	@Override
	public void write(PacketByteBuf buffer)
	{
		buffer.writeFloat(red);
		buffer.writeFloat(green);
		buffer.writeFloat(blue);
		buffer.writeFloat(alpha);
		buffer.writeFloat(size);
		buffer.writeFloat(life);
	}

	@Override
	public String asString()
	{
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %d", ModParticles.COLORED, this.red, this.green, this.blue, this.alpha, this.size, this.life);
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
