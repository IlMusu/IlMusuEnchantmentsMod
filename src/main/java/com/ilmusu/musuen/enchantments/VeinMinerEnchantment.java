package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerBreakSpeedCallback;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VeinMinerEnchantment extends Enchantment
{
    public VeinMinerEnchantment(Rarity rarity)
    {
        super(rarity, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    public int getMaxBreakableBlocks(int level)
    {
        return 10+level*level*4;
    }

    @Override
    protected boolean canAccept(Enchantment other)
    {
        return !(other instanceof VeinMinerEnchantment) &&
               !(other instanceof UnearthingEnchantment);
    }

    static
    {
        // Reduce the player break speed when using the vein miner enchantment
        PlayerBreakSpeedCallback.AFTER.register(((player, stack, pos) ->
        {
            // Check if there is a tunneling enchantment on the stack
            int level = EnchantmentHelper.getLevel(ModEnchantments.VEIN_MINER, stack);
            if(level <= 0)
                return 1.0F;

            // Check if the state the player is trying the break is suitable for the tool
            BlockState state = player.world.getBlockState(pos);
            if(!(stack.getItem() instanceof MiningToolItem tool) || !tool.isSuitableFor(state))
                return 1.0F;

            VeinMinerEnchantment ench = (VeinMinerEnchantment)ModEnchantments.UNEARTHING;
            int breakables = ench.getMaxBreakableBlocks(level);
            return 1 / Math.max(1, breakables*0.05F);
        }));

        PlayerBlockBreakEvents.BEFORE.register(((world, player, pos, state, blockEntity) ->
        {
            // To activate the vein mining, the tool must be suitable for the state
            ItemStack stack = player.getMainHandStack();
            if(!(stack.getItem() instanceof MiningToolItem tool) || !tool.isSuitableFor(state))
                return true;

            // Computing the total blocks that the VeinMiner enchantments on the stack can break
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
            int remainingBlocksToBreak = enchantments.keySet().stream()
                .filter(enchantment -> enchantment instanceof VeinMinerEnchantment)
                .map(enchantment -> ((VeinMinerEnchantment)enchantment).getMaxBreakableBlocks(enchantments.get(enchantment)))
                .reduce(0, Integer::sum);

            if(remainingBlocksToBreak <= 0)
                return true;

            List<BlockPos> posesToCheck = new ArrayList<>();
            posesToCheck.add(pos);

            outer_loop: while(posesToCheck.size() > 0)
            {
                BlockPos cachedPos = posesToCheck.remove(0);

                for(int x=-1; x<=1; ++x)
                    for(int y=-1; y<=1; ++y)
                        for(int z =-1; z<=1; ++z)
                        {
                            BlockPos newPos = cachedPos.add(x, y, z);

                            if(newPos.getSquaredDistance(pos) > 4096)
                                continue;

                            if(world.getBlockState(newPos).getBlock() == state.getBlock())
                            {
                                posesToCheck.add(newPos);
                                // Block is removed immediately to avoid storing the same position
                                UnearthingEnchantment.tryBreakBlock(world, player, stack, newPos);
                                // Check is done here so that no useless poses are added to the list
                                if(remainingBlocksToBreak-- <= 0)
                                    break outer_loop;
                            }
                        }
            }
            return true;
        }));
    }
}
