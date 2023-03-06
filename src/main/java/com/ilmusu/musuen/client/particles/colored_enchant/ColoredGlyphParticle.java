package com.ilmusu.musuen.client.particles.colored_enchant;

import com.ilmusu.musuen.client.particles.colored.ColoredParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ColoredGlyphParticle extends EnchantGlyphParticle
{
	protected ColoredGlyphParticle(ClientWorld level, Vec3d pos, Vec3d vel, ColoredGlyphParticleEffect options, SpriteProvider sprite)
	{
		super(level, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);

		this.setSprite(sprite);
		this.red = options.getRed();
		this.green = options.getGreen();
		this.blue = options.getBlue();
		this.alpha = options.getAlpha();
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	public static class Factory implements ParticleFactory<ColoredGlyphParticleEffect>
	{
		private final SpriteProvider sprites;

		public Factory(SpriteProvider sprite)
		{
			this.sprites = sprite;
		}

		@Override
		public Particle createParticle(ColoredGlyphParticleEffect data, ClientWorld world, double x, double y, double z, double xs, double ys, double zs)
		{
			return new ColoredGlyphParticle(world, new Vec3d(x, y, z), new Vec3d(xs, ys, zs), data, this.sprites);
		}
	}
}
