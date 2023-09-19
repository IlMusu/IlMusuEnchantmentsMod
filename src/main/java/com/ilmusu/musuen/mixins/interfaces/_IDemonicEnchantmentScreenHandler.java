package com.ilmusu.musuen.mixins.interfaces;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface _IDemonicEnchantmentScreenHandler
{
    List<BlockPos> SKULLS_OFFSETS = BlockPos.stream(-1, 0, -1, 1, 0, 1).map(BlockPos::toImmutable).toList();

    boolean hasDemonicEnchantment(int slot);

    static boolean isValidSkull(BlockState state)
    {
        Block block = state.getBlock();
        return block == Blocks.WITHER_SKELETON_SKULL || block == Blocks.WITHER_SKELETON_WALL_SKULL;
    }
}