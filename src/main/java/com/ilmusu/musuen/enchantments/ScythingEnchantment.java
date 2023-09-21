package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ScythingEnchantment extends Enchantment
{
    private static final String LABEL = "is_scything";

    public ScythingEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, ModEnchantmentTargets.HOE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        ((_IEnchantmentLevels)this).setConfigurationLevels(minLevel, maxLevel);
    }

    @Override
    public int getMinLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMinLevel();
    }

    @Override
    public int getMaxLevel()
    {
        return ((_IEnchantmentLevels)this).getConfigurationMaxLevel();
    }

    static
    {
        PlayerBlockBreakEvents.AFTER.register(((world, player, pos, state, blockEntity) ->
        {
            if(world.isClient)
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.SCYTHING, player);
            if(level == 0)
                return;

            // If the player is currently scything, ignore this event
            NbtCompound nbt = ((_IEntityPersistentNbt)player).getPNBT();
            if(nbt.contains(LABEL) && Math.abs(nbt.getInt(LABEL)-player.age) < 5)
                return;

            // Marking the player as using scything
            nbt.putInt(LABEL, player.age);

            for(int x=-level; x<=level; ++x)
                for(int z=-level; z<=level; ++z)
                {
                    BlockPos nearPos = pos.add(x, 0, z);
                    BlockState block = world.getBlockState(nearPos);
                    if(!(block.getBlock() instanceof CropBlock))
                        continue;

                    if(!(((ServerPlayerEntity)player).interactionManager.tryBreakBlock(nearPos)))
                        continue;

                    // Damaging the stack a bit
                    player.getMainHandStack().damage(1, player.getRandom(), (ServerPlayerEntity) player);
                }

            // Unmarking the player as using scything
            nbt.remove(LABEL);
        }));
    }
}
