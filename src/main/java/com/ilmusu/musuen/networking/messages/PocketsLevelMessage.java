package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.mixins.interfaces._IPlayerPockets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class PocketsLevelMessage extends _Message
{
    private int level;

    public PocketsLevelMessage()
    {
        super("pockets_level");
    }

    public PocketsLevelMessage(PlayerEntity player)
    {
        this();
        this.level = ((_IPlayerPockets)player).getPocketLevel();
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeInt(this.level);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.level = buf.readInt();
    }

    @Override
    public void handle(PlayerEntity player)
    {
        ((_IPlayerPockets)player).updatePocketsLevel(player.getWorld(), this.level);
    }
}
