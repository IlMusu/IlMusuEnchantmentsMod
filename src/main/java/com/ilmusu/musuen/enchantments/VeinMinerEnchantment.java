package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerBreakSpeedCallback;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VeinMinerEnchantment extends Enchantment
{
    private static final String LABEL = "is_veinmining";

    public VeinMinerEnchantment(Rarity rarity)
    {
        super(rarity, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel()
    {
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 5);
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

    protected static boolean canUseVeinMining(PlayerEntity player, BlockState state)
    {
        if(player.isSneaking() && !ModConfigurations.shouldEnableVeinMiningWhileSneaking())
            return false;
        return ModConfigurations.isBlockVeinMiningWhiteListed(state);
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

            // Disable the enchantment if the player cannot use it now
            BlockState state = player.getWorld().getBlockState(pos);
            if(!canUseVeinMining(player, state))
                return 1.0F;

            // Check if the state the player is trying the break is suitable for the tool
            if(!(stack.getItem() instanceof MiningToolItem tool) || !tool.isSuitableFor(state))
                return 1.0F;

            VeinMinerEnchantment ench = (VeinMinerEnchantment)ModEnchantments.VEIN_MINER;
            int breakables = ench.getMaxBreakableBlocks(level);
            return 1 / Math.max(1, breakables*0.05F);
        }));

        PlayerBlockBreakEvents.BEFORE.register(((world, player, pos, state, blockEntity) ->
        {
            // Disable the enchantment if the player cannot use it now
            if(!canUseVeinMining(player, state))
                return true;

            // If the player is currently vein mining, ignore this event
            NbtCompound nbt = ((_IEntityPersistentNbt)player).getPNBT();
            if(nbt.contains(LABEL) && Math.abs(nbt.getInt(LABEL)-player.age) < 5)
                return true;

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

            // Marking the player as using vein mining
            nbt.putInt(LABEL, player.age);

            List<BlockPos> posesToCheck = new ArrayList<>();
            posesToCheck.add(pos);

            outer_loop: while(!posesToCheck.isEmpty())
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
                                ModUtils.tryBreakBlockIfSuitable(world, player, stack, newPos);
                                // Check is done here so that no useless poses are added to the list
                                if(remainingBlocksToBreak-- <= 0)
                                    break outer_loop;
                            }
                        }
            }

            // Unmarking the player as using vein mining
            nbt.remove(LABEL);
            return true;
        }));
    }
}
