package com.ilmusu.musuen.client.particles.eblock;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockParticle extends SpriteBillboardParticle
{
    private final float sampleU;
    private final float sampleV;

    protected BlockParticle(ClientWorld level, Vec3d pos, Vec3d vel, BlockParticleEffect options, BlockState state)
    {
        super(level, pos.x, pos.y, pos.z, 0.0F, 0.0F, 0.0F);
        this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state));
        this.scale = options.getSize();
        this.maxAge = options.getLife();
        this.age = 0;
        this.velocityX = vel.x;
        this.velocityY = vel.y;
        this.velocityZ = vel.z;
        this.collidesWithWorld = true;
        this.gravityStrength = options.getGravity();
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
    }

    @Override
    public ParticleTextureSheet getType()
    {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    @Override
    protected float getMinU()
    {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxU()
    {
        return this.sprite.getFrameU(this.sampleU / 4.0f * 16.0f);
    }

    @Override
    protected float getMinV()
    {
        return this.sprite.getFrameV(this.sampleV / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxV()
    {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getBrightness(float tint)
    {
        int i = super.getBrightness(tint);
        BlockPos pos = new BlockPos(this.x, this.y, this.z);
        if (i == 0 && this.world.isChunkLoaded(pos))
            return WorldRenderer.getLightmapCoordinates(this.world, pos);
        return i;
    }

    public static class Factory implements ParticleFactory<BlockParticleEffect>
    {
        @SuppressWarnings("unused")
        public Factory(SpriteProvider sprite)
        {
        }

        @Override
        public Particle createParticle(BlockParticleEffect data, ClientWorld world, double x, double y, double z, double xs, double ys, double zs)
        {
            return new BlockParticle(world, new Vec3d(x, y, z), new Vec3d(xs, ys, zs), data, data.getState());
        }
    }
}
