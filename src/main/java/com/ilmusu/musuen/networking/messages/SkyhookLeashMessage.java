package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.enchantments.SkyhookEnchantment;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class SkyhookLeashMessage extends _Message
{
    private int leashedID;
    private int holderID;
    private boolean isLeashed;

    public SkyhookLeashMessage()
    {
        super("skyhook_leash_message");
    }

    public SkyhookLeashMessage(Entity leashed, Entity holder, boolean isLeashed)
    {
        this();
        this.leashedID = leashed.getId();
        this.holderID = holder.getId();
        this.isLeashed = isLeashed;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf)
    {
        buf.writeInt(this.leashedID);
        buf.writeInt(this.holderID);
        buf.writeBoolean(this.isLeashed);
        return buf;
    }

    @Override
    public void decode(PacketByteBuf buf)
    {
        this.leashedID = buf.readInt();
        this.holderID = buf.readInt();
        this.isLeashed = buf.readBoolean();
    }

    @Override
    public void handle(PlayerEntity player)
    {
        Entity leashed = player.getWorld().getEntityById(this.leashedID);
        if(leashed == null)
            return;

        if(this.isLeashed)
            ((_IEntityPersistentNbt)leashed).getPNBT().putInt(SkyhookEnchantment.SKYHOOK_HOLDER, this.holderID);
        else
            ((_IEntityPersistentNbt)leashed).getPNBT().remove(SkyhookEnchantment.SKYHOOK_HOLDER);
    }
}
