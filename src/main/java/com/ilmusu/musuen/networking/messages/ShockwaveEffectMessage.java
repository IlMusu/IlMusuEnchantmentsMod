package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.enchantments.ShockwaveEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

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
        ShockwaveEnchantment.spawnShockwaveEffects(player.world, player.getRandom(), this.pos, this.size, this.direction);
    }
}
