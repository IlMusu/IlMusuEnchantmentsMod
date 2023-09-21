package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.callbacks.PlayerItemUseCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.raycasting.ModRaycast;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.UUID;

public class CritterEnchantment extends Enchantment
{
    private static final String CRITTER_ENTITY_UUID = Resources.MOD_ID+".critter_entity";

    public CritterEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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

    protected static boolean validateRaycastTarget(Object target, LivingEntity ignore)
    {
        if(target instanceof BlockState state)
            return state.blocksMovement();
        if(target instanceof Entity)
            return target != ignore;
        return false;
    }

    static
    {
        PlayerItemUseCallback.EVENT.register(((player, hand, stack) ->
        {
            if(player.getWorld().isClient)
                return;

            // Checking the level
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.CRITTING, player);
            if(level == 0)
                return;

            HitResult result = ModRaycast.raycast(player, 20.0F, (object) -> validateRaycastTarget(object, player));
            if(!(result instanceof EntityHitResult entityHit))
                return;

            // Marking the entity that should be critted
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putUuid(CRITTER_ENTITY_UUID, entityHit.getEntity().getUuid());
        }));

        LivingEntityDamageCallback.BEFORE_PROTECTION.register(((entity, source, damage) ->
        {
            if(!(source.getAttacker() instanceof PlayerEntity player))
                return damage;

            // Checking the level
            ItemStack inHandStack = player.getMainHandStack();
            int level = EnchantmentHelper.getLevel(ModEnchantments.CRITTING, inHandStack);
            if(level == 0)
                return damage;

            // Unmarking the critted entity
            NbtCompound nbt = inHandStack.getOrCreateNbt();
            UUID uuid = nbt.getUuid(CRITTER_ENTITY_UUID);
            nbt.remove(CRITTER_ENTITY_UUID);

            float multiplier = entity.getUuid().equals(uuid) ? 2.5F : 0.5F;
            return damage * multiplier;
        }));
    }
}
