package com.ilmusu.musuen.networking.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class PlayerJumpMessage extends _Message
{
    public PlayerJumpMessage()
    {
        super("player_jump");
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {

    }

    @Override
    public void handle(PlayerEntity player)
    {
        // The player jumping mechanic is client side only but there are many problems with air jumps
        // Because of this reason the velocity needs to be synchronized on the server
        player.jump();
        // The velocity does not need to be synced with the clients
        player.velocityDirty = false;
        // Resetting the fall distance also here
        player.fallDistance = 0.0F;
    }
}
