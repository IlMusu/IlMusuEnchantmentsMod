package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.client.particles.eblock.BlockParticleEffect;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ShockwaveEffectMessage extends _Message
{
    private Vec3d pos;
    private Vec3d direction;
    private float size;

    public ShockwaveEffectMessage()
    {
        super("shockwave_effect");
    }

    public ShockwaveEffectMessage(Vec3d pos, Vec3d direction, float size)
    {
        this();
        this.pos = pos;
        this.direction = direction;
        this.size = size;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeDouble(this.pos.x);
        buf.writeDouble(this.pos.y);
        buf.writeDouble(this.pos.z);
        buf.writeDouble(this.direction.x);
        buf.writeDouble(this.direction.y);
        buf.writeDouble(this.direction.z);
        buf.writeFloat(this.size);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.direction = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.size = buf.readFloat();
    }

    @Override
    public void handle(PlayerEntity player)
    {
        // The vector perpendicular to the direction
        Vec3d side = new Vec3d(-this.direction.z, 0, this.direction.x);
        // The state under the specified position
        BlockState state = player.world.getBlockState(new BlockPos(this.pos).down());

        for(float s=0.1F; s<=this.size; s+=0.3F)
        {
            for(float s1 : List.of(-s, s))
            {
                float parabola = -0.2F*s1*s1;
                Vec3d regression = this.direction.multiply(parabola);
                Vec3d pos1 = this.pos.add(side.multiply(s1)).add(regression).add(ModUtils.randomInCircle(player.getRandom()).multiply(0.5F));
                Vec3d vel = ModUtils.randomInCircle(player.getRandom()).multiply(0.1F);

                int life = ModUtils.range(player.getRandom(), 5, 8);
                float size = ModUtils.range(player.getRandom(), 0.10F, 0.20F);
                float height = ModUtils.range(player.getRandom(), 0.10F, 0.3F);

                // Spawning particles
                for(int i=0; i<3; ++i)
                {
                    BlockParticleEffect particle = new BlockParticleEffect(state).life(life).size(size).gravity(0.25F);
                    player.world.addParticle(particle, pos1.x, pos1.y+height, pos1.z, vel.x, vel.y, vel.z);
                }
            }
        }

        // Playing the block sound
        float pitch = ModUtils.range(player.getRandom(), 0.8F, 1.2F);
        player.world.playSound(this.pos.x, this.pos.y, this.pos.z, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 0.6F, pitch, false);
    }
}
