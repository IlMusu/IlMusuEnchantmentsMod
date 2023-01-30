package com.ilmusu.ilmusuenchantments.utils;

import com.ilmusu.ilmusuenchantments.mixins.mixin.AccessorClientPlayerInteractionManager;
import com.ilmusu.ilmusuenchantments.mixins.mixin.AccessorServerPlayerInteractionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ModUtils
{
    public static class Linear
    {
        private final float m;
        private final float q;

        public Linear(float x1, float y1, float x2, float y2)
        {
            this.m = (y2-y1)/(x2-x1);
            this.q = y1 - this.m * x1;
        }

        public float of(float x)
        {
            return this.m * x + q;
        }
    }

    public static double range(Random rand, double min, double max)
    {
        return min + rand.nextDouble()*(max - min);
    }

    public static float range(Random rand, float min, float max)
    {
        return min + rand.nextFloat()*(max - min);
    }

    public static int range(Random rand, int min, int max)
    {
        return min + rand.nextInt(max - min);
    }


    // ### PLAYER UTILITIES ############################################################################

    public static BlockPos getCurrentMiningPos(PlayerEntity player)
    {
        if(player.world.isClient)
            return getCurrentMiningPosClient();
        return getCurrentMiningPosServer((ServerPlayerEntity) player);
    }

    private static BlockPos getCurrentMiningPosServer(ServerPlayerEntity player)
    {
        AccessorServerPlayerInteractionManager manager = (AccessorServerPlayerInteractionManager)player.interactionManager;
        return manager.isMining() ? manager.getCurrentMiningPos() : null;
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("DataFlowIssue")
    private static BlockPos getCurrentMiningPosClient()
    {
        ClientPlayerInteractionManager manager = MinecraftClient.getInstance().interactionManager;
        AccessorClientPlayerInteractionManager accessor = (AccessorClientPlayerInteractionManager)manager;
        return manager.isBreakingBlock() ? accessor.getCurrentMiningPos() : null;
    }
}
