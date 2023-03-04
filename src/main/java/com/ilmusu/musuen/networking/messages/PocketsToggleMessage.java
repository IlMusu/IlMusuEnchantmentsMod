package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.mixins.interfaces._IPlayerPockets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class PocketsToggleMessage extends _Message
{
    private boolean areOpen;

    public PocketsToggleMessage()
    {
        super("pockets_toggle");
    }

    public PocketsToggleMessage(PlayerEntity player)
    {
        this();
        this.areOpen = ((_IPlayerPockets)player).arePocketsOpen();
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeBoolean(this.areOpen);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.areOpen = buf.readBoolean();
    }

    @Override
    public void handle(PlayerEntity player)
    {
        ((_IPlayerPockets)player).setPocketsOpen(this.areOpen);
    }
}

