package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.client.particles.colored.ColoredParticleEffect;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class MoonJumpEffectsMessage extends _Message
{
    private Vec3d pos;

    public MoonJumpEffectsMessage()
    {
        super("moon_jump_effects");
    }

    public MoonJumpEffectsMessage(LivingEntity jumper)
    {
        this();
        this.pos = jumper.getPos();
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeDouble(this.pos.x);
        buf.writeDouble(this.pos.y);
        buf.writeDouble(this.pos.z);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void handle(PlayerEntity player)
    {
        if(!player.world.isClient)
        {
            this.sendToClientsTrackingAndSelf(player);
            return;
        }

        float pitch = ModUtils.range(player.getRandom(), 0.4F, 0.6F);
        player.world.playSound(this.pos.x, this.pos.y, this.pos.z, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 0.4F, pitch, false);

        Color gray = new Color(220, 220, 220);
        for(int i=0; i<10; ++i)
        {
            Color color = ModUtils.randomizeColor(player.getRandom(), gray, 20);
            Vec3d pos = this.pos.add(ModUtils.randomInCircle(player.getRandom()).multiply(0.5F));
            Vec3d vel = ModUtils.randomInCircle(player.getRandom()).multiply(0.1F);
            ParticleEffect particle = new ColoredParticleEffect(color).life(30).size(0.4F);
            player.world.addParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
        }
    }
}
