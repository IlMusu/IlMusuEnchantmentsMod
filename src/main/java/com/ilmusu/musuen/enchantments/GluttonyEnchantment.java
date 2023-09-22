package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerTickCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.registries.ModSoundEvents;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

public class GluttonyEnchantment extends Enchantment
{
    public GluttonyEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
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
        PlayerTickCallback.AFTER.register(player ->
        {
            if(player.getWorld().isClient)
                return;

            // Check if the player needs to eat something
            if(!player.getHungerManager().isNotFull())
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.GLUTTONY, player);
            if(level == 0)
                return;

            // To avoid useless checks at each tick, this is done at most every two second
            if(player.age % 40 != 0)
                return;

            // Getting food from the player inventory
            ItemStack stack = GluttonyEnchantment.findFoodInInventory(player);
            if(stack == null)
            {
                if(player.age % (20*20) != 0)
                    return;
                // Playing a hungry sound to inform the player that there is no more edible food in the inventory
                float pitch = ModUtils.range(player.getRandom(), 0.7F, 1.3F);
                player.playSound(ModSoundEvents.LIVING_STOMACH_RUMBLE, SoundCategory.PLAYERS, 0.8F, pitch);
                return;
            }

            // Getting the missing food
            int foodLevel = player.getHungerManager().getFoodLevel();
            int restorableFood = stack.getItem().getFoodComponent().getHunger();
            // Avoiding wasting too much food
            if((20.0 - foodLevel) < restorableFood*0.8)
                return;

            // Eating food if existing
            player.eatFood(player.getWorld(), stack);
            // Inefficiency when the enchantment is not at max level
            float inefficiencyAmount = new ModUtils.Linear(1, 0.7F, 3, 1.0F).of(level);
            int newRestorableFood = Math.min((int)(restorableFood*inefficiencyAmount), 1);
            int newFoodLevel = Math.min(foodLevel + newRestorableFood, 20);
            player.getHungerManager().setFoodLevel(newFoodLevel);
        });
    }

    protected static ItemStack findFoodInInventory(PlayerEntity player)
    {
        for(int i=0; i<player.getInventory().size(); ++i)
        {
            ItemStack stack = player.getInventory().getStack(i);
            if(stack.getItem().isFood())
                return stack;
        }
        return null;
    }
}
