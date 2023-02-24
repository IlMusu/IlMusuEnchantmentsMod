package com.ilmusu.ilmusuenchantments.networking.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

public class SwingHandMessage extends _Message
{
    public SwingHandMessage()
    {
        super("swing_hand");
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf) {

    }

    @Override
    public void handle(PlayerEntity player)
    {
        // Swinging the player hand
        player.swingHand(Hand.MAIN_HAND);
    }
}
