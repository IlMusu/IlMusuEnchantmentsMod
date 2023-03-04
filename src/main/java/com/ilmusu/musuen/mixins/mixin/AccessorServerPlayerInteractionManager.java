package com.ilmusu.musuen.mixins.mixin;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerInteractionManager.class)
public interface AccessorServerPlayerInteractionManager
{
    @Accessor("miningPos")
    BlockPos getCurrentMiningPos();

    @Accessor("mining")
    boolean isMining();
}
