package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.enchantments.SkyJumpEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class SkyJumpEffectMessage extends _Message
{
    private Vec3d pos;

    public SkyJumpEffectMessage()
    {
        super("sky_jump_effects");
    }

    public SkyJumpEffectMessage(LivingEntity jumper)
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
        if(!player.getWorld().isClient)
        {
            this.sendToClientsTrackingAndSelf(player);
            return;
        }

        SkyJumpEnchantment.spawnSkyJumpEffects(player.getWorld(), this.pos, player.getRandom());
    }
}
