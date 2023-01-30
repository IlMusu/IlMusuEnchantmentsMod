package com.ilmusu.ilmusuenchantments.networking.messages;

import com.ilmusu.ilmusuenchantments.enchantments.PhasingEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class PhasingSwitchMessage extends _Message
{
    private boolean entering;

    public PhasingSwitchMessage()
    {
        super("phasing_switch");
    }

    public PhasingSwitchMessage(boolean entering)
    {
        this();
        this.entering = entering;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeBoolean(this.entering);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.entering = buf.readBoolean();
    }

    @Override
    public void handle(PlayerEntity player)
    {
        PhasingEnchantment.onClientPlayerPhasing(this.entering);
    }
}
