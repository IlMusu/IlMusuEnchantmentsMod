package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class PlayerDemonicDamageMessage extends _Message
{
    public PlayerDemonicDamageMessage()
    {
        super("player_demonic_damage");
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
        NbtCompound nbt = ((_IEntityPersistentNbt)player).getPNBT();
        nbt.putBoolean("was_damage_demonic", true);
    }
}
