package com.ilmusu.musuen.mixins.interfaces;

import net.minecraft.nbt.NbtCompound;

public interface _IEntityPersistentNbt
{
    NbtCompound get();

    void clone(_IEntityPersistentNbt other);
}
