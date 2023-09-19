package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.EntityEquipmentDropCallback;
import com.ilmusu.musuen.recipes.HeadRecipe;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModCustomRecipes;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GuillotiningEnchantment extends Enchantment implements _IDemonicEnchantment
{
    public GuillotiningEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public int getMinLevel()
    {
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 3);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer()
    {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection()
    {
        return !ModConfigurations.isDemonicEnchantingEnabled();
    }

    static
    {
        EntityEquipmentDropCallback.EVENT.register(((entity, source, multiplier, drops) ->
        {
            Entity killer = source.getAttacker();
            if(!(killer instanceof LivingEntity living))
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.GUILLOTINING, living);
            if(level == 0)
                return;

            Identifier entityIdentifier = Registry.ENTITY_TYPE.getId(entity.getType());
            HeadRecipe recipe = ModCustomRecipes.HEAD_RECIPES.get(entityIdentifier);
            if(recipe == null)
                return;

            float probability = new ModUtils.Linear(1, 0.05F, 3, 0.10F).of(level);
            if(entity.getWorld().random.nextFloat() > probability)
                return;

            // Consumes a bit of the player health since this is demonic
            float percentage = new ModUtils.Linear(1, 0.2F, 3, 0.3F).of(level);
            _IDemonicEnchantment.consumeHealthValue(living, percentage, false);

            int idx = entity.getWorld().random.nextInt(recipe.getHeads().size());
            Identifier headIdentifier = recipe.getHeads().get(idx);
            Item headItem = Registry.ITEM.get(headIdentifier);
            ItemStack headStack = new ItemStack(headItem);

            // Exception for when the mob is a player
            if(entity instanceof PlayerEntity player)
            {
                NbtCompound nbt = headStack.getOrCreateNbt();
                nbt.putString("SkullOwner", player.getGameProfile().getName());
            }

            entity.dropStack(headStack);
        }));
    }
}
