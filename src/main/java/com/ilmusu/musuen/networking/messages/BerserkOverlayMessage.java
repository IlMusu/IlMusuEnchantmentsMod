package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.enchantments.BerserkerEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class BerserkOverlayMessage extends _Message
{
    private int duration;

    public BerserkOverlayMessage()
    {
        super("berserk_overlay");
    }

    public BerserkOverlayMessage(int duration)
    {
        this();
        this.duration = duration;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeInt(this.duration);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.duration = buf.readInt();
    }

    @Override
    public void handle(PlayerEntity player)
    {
        BerserkerEnchantment.setBerserkOverlay(this.duration);
    }
}
