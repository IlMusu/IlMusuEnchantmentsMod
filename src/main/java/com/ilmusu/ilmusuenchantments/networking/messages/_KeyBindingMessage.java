package com.ilmusu.ilmusuenchantments.networking.messages;

import net.minecraft.network.PacketByteBuf;

public abstract class _KeyBindingMessage extends _Message
{
    protected int action;
    protected int modifiers;

    public _KeyBindingMessage(String name)
    {
        super(name);
    }

    public _KeyBindingMessage(String name, int action, int modifiers)
    {
        this(name);
        this.action = action;
        this.modifiers = modifiers;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeInt(this.action);
        buf.writeInt(this.modifiers);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.action = buf.readInt();
        this.modifiers = buf.readInt();
    }
}
