package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerBreakSpeedCallback;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class UnearthingEnchantment extends Enchantment implements _IDemonicEnchantment
{
    public UnearthingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.DIGGER, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer()
    {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection()
    {
        return false;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    protected boolean canAccept(Enchantment other)
    {
        return !(other instanceof UnearthingEnchantment) &&
               !(other instanceof VeinMinerEnchantment);
    }

    protected int getSideBreakingLength(int level)
    {
        return (int)Math.min(3, 1+level*0.2F);
    }

    protected int getForwardBreakingLength(int level)
    {
        return (int) (level*1.5F);
    }

    static
    {
        // Reduce the player break speed when using the unearthing enchantment
        PlayerBreakSpeedCallback.AFTER.register(((player, stack, pos) ->
        {
            // Check if there is a tunneling enchantment on the stack
            int level = EnchantmentHelper.getLevel(ModEnchantments.UNEARTHING, stack);
            if(level <= 0)
                return 1.0F;

            // Check if the state the player is trying the break is suitable for the tool
            BlockState state = player.world.getBlockState(pos);
            if(!(stack.getItem() instanceof MiningToolItem tool) || !tool.isSuitableFor(state))
                return 1.0F;

            UnearthingEnchantment ench = ((UnearthingEnchantment)ModEnchantments.UNEARTHING);
            int side = ench.getSideBreakingLength(level);
            int forward = ench.getForwardBreakingLength(level);
            int volume = (1+2*side)*(1+2*side)*(1+forward);
            return 1 / (volume*0.1F);
        }));

        // The break logic of the unearthing enchantment
        PlayerBlockBreakEvents.BEFORE.register(((world, player, pos, state, blockEntity) ->
        {
            // Check if there is a tunneling enchantment on the stack
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.UNEARTHING, player);
            if(level <= 0)
                return true;

            // Check if the state the player is trying the break is suitable for the tool
            ItemStack stack = player.getMainHandStack();
            if(!(stack.getItem() instanceof MiningToolItem tool) || !tool.isSuitableFor(state))
                return true;

            // Ray cast is necessary because side is only on client
            HitResult hit = player.raycast(500F, 0.0F, false);
            if(!(hit instanceof BlockHitResult blockHit))
                return true;

            UnearthingEnchantment ench = ((UnearthingEnchantment)ModEnchantments.UNEARTHING);
            int side = ench.getSideBreakingLength(level);
            int forward = ench.getForwardBreakingLength(level);

            // Computing percentage of blocks that the player can break with the consumed health
            float healthPercentage = new ModUtils.Linear(ench.getMinLevel(), 0.20F, ench.getMaxLevel(), 0.30F).of(level);
            float consumedPercentage = _IDemonicEnchantment.consumeHealthPercentage(player, healthPercentage, true);
            // Computing how many blocks the enchantment can break
            int amount = (int) ((1+2*side)*(1+2*side)*(1+forward) * consumedPercentage);
            int counter = 0;

            // Computing the directions for digging the tunnel
            Vec3d forwardDir = new Vec3d(blockHit.getSide().getOpposite().getUnitVector());
            Vec3d sideDir = new Vec3d(forwardDir.y, forwardDir.z, forwardDir.x);
            Vec3d upDir = sideDir.crossProduct(forwardDir);
            // Makes a tunnel with size depending on level
            outer_loop: for(int z=0; z<forward; ++z)
                for(int y=-side; y<=side; ++y)
                    for(int x=-side; x<=side; ++x)
                    {
                        if(counter++ > amount)
                            break outer_loop;

                        Vec3d offset = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                        offset = offset.add(forwardDir.multiply(z)).add(sideDir.multiply(x)).add(upDir.multiply(y));
                        tryBreakBlock(world, player, stack, BlockPos.ofFloored(offset));
                    }

            return true;
        }));
    }

    // Copied and modified from the ServerPlayerInteractionManager function
    public static void tryBreakBlock(World world, PlayerEntity player, ItemStack tool, BlockPos pos)
    {
        BlockState blockState = world.getBlockState(pos);
        if(!tool.isSuitableFor(blockState))
            return;
        // Avoid breaking bedrock or air
        if(blockState.getBlock().getHardness() < 0 || blockState.isAir())
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        Block block = blockState.getBlock();

        if (block instanceof OperatorBlock && !player.isCreativeLevelTwoOp())
        {
            world.updateListeners(pos, blockState, blockState, Block.NOTIFY_ALL);
            return;
        }

        if (player.isBlockBreakingRestricted(world, pos, ((ServerPlayerEntity)player).interactionManager.getGameMode()))
            return;

        // Called before the player actually breaks the block
        block.onBreak(world, pos, blockState, player);
        // Actually breaking the block and related logic
        boolean isBlockRemoved = world.removeBlock(pos, false);
        if (isBlockRemoved)
            block.onBroken(world, pos, blockState);

        if (((ServerPlayerEntity)player).interactionManager.isCreative())
            return;

        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = itemStack.copy();
        itemStack.postMine(world, blockState, pos, player);
        // This makes the block drops its item stacks
        if (isBlockRemoved && player.canHarvest(blockState))
            block.afterBreak(world, player, pos, blockState, blockEntity, itemStack2);

    }
}
