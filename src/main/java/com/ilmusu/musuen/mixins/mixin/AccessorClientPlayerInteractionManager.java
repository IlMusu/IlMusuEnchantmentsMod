package com.ilmusu.musuen.mixins.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerInteractionManager.class)
public interface AccessorClientPlayerInteractionManager
{
    @Accessor("currentBreakingPos")
    BlockPos getCurrentMiningPos();
}
