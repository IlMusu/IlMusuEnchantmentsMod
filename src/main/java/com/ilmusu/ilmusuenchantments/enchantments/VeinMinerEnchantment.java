package com.ilmusu.ilmusuenchantments.enchantments;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
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
        return !(other instanceof UnearthingEnchantment) && !(other instanceof VeinMinerEnchantment);
    }

    static
    {
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
                                UnearthingEnchantment.tryBreakBlock(world, player, newPos);
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
