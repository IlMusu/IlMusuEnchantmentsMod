package com.ilmusu.ilmusuenchantments.networking.messages;

import com.ilmusu.ilmusuenchantments.mixins.interfaces._IPlayerPockets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class SynchronizePocketsMessage extends _Message
{
    private int level;

    public SynchronizePocketsMessage()
    {
        super("synchronize_pockets");
    }

    public SynchronizePocketsMessage(PlayerEntity player)
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
        ((_IPlayerPockets)player).setPocketLevel(player.world, this.level);
    }
}
