package com.ilmusu.musuen.entities.utils;

import net.minecraft.nbt.NbtCompound;

public interface _IAdditionalSpawnData
{
    NbtCompound writeSpawnData(NbtCompound nbt);

    void readSpawnData(NbtCompound nbt);
}
