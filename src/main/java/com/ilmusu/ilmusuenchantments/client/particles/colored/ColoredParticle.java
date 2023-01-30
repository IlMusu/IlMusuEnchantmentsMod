package com.ilmusu.ilmusuenchantments.client.particles.colored;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ColoredParticle extends SpriteBillboardParticle
{
	private final SpriteProvider animatedSprite;

	protected ColoredParticle(ClientWorld level, Vec3d pos, Vec3d vel, ColoredParticleEffect options, SpriteProvider sprite)
	{
		super(level, pos.x, pos.y, pos.z, 0.0F, 0.0F, 0.0F);

		this.animatedSprite = sprite;
		this.red = options.getRed();
		this.green = options.getGreen();
		this.blue = options.getBlue();
		this.alpha = options.getAlpha();
		this.scale = options.getSize();
		this.maxAge = options.getLife();
		this.age = 0;
		this.velocityX = vel.x;
		this.velocityY = vel.y;
		this.velocityZ = vel.z;
		this.collidesWithWorld = true;
		this.gravityStrength = options.getGravity();
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void buildGeometry(VertexConsumer buffer, Camera camera, float partialTicks)
	{
		this.setSpriteForAge(this.animatedSprite);
		super.buildGeometry(buffer, camera, partialTicks);
	}

	public static class Factory implements ParticleFactory<ColoredParticleEffect>
	{
		private final SpriteProvider sprites;

		public Factory(SpriteProvider sprite)
		{
			this.sprites = sprite;
		}

		@Override
		public Particle createParticle(ColoredParticleEffect data, ClientWorld world, double x, double y, double z, double xs, double ys, double zs)
		{
			return new ColoredParticle(world, new Vec3d(x, y, z), new Vec3d(xs, ys, zs), data, this.sprites);
		}
	}
}
