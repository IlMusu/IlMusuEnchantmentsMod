package com.ilmusu.musuen.utils;

import com.ilmusu.musuen.mixins.mixin.AccessorClientPlayerInteractionManager;
import com.ilmusu.musuen.mixins.mixin.AccessorServerPlayerInteractionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

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

    public static int clamp(int min, int value, int max)
    {
        if(value < min)
            return min;
        if(value > max)
            return max;
        return value;
    }

    public static float clamp(float min, float value, float max)
    {
        if(value < min)
            return min;
        if(value > max)
            return max;
        return value;
    }

    // ### RANDOM UTILITIES ############################################################################

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

    public static Color randomizeColor(Random random, Color color, int amount)
    {
        int red = color.getRed() + range(random, -amount, amount);
        int green = color.getGreen() + range(random, -amount, amount);
        int blue = color.getBlue() + range(random, -amount, amount);
        return new Color(clamp(0, red, 255), clamp(0, green, 255), clamp(0, blue, 255));
    }

    public static Vec3d randomInSphere(Random rand)
    {
        return Vec3d.fromPolar(rand.nextFloat()*360, rand.nextFloat()*360);
    }

    public static Vec3d randomInCircle(Random rand)
    {
        return Vec3d.fromPolar(0, rand.nextFloat()*360);
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

    public static int findInInventory(PlayerEntity player, Item item)
    {
        for(int i=0; i<player.getInventory().size(); ++i)
            if(player.getInventory().getStack(i).getItem() == item)
                return i;
        return -1;
    }

    public static void tryBreakBlockIfSuitable(World world, PlayerEntity player, ItemStack tool, BlockPos pos)
    {
        BlockState blockState = world.getBlockState(pos);
        if(!tool.isSuitableFor(blockState))
            return;

        // Using the minecraft logic for breaking the item
        ((ServerPlayerEntity)player).interactionManager.tryBreakBlock(pos);
    }
}
