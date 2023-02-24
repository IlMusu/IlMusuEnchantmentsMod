package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.callbacks.LivingEntityJumpCheckCallback;
import com.ilmusu.ilmusuenchantments.callbacks.PlayerLandCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.ilmusuenchantments.networking.messages.MoonJumpEffectsMessage;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class MoonJumpEnchantment extends Enchantment
{
    private static final String MOON_JUMPS_TAG = Resources.MOD_ID+".is_phasing";

    public MoonJumpEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    protected boolean canAccept(Enchantment other)
    {
        return other != Enchantments.FEATHER_FALLING && super.canAccept(other);
    }

    static
    {
        LivingEntityJumpCheckCallback.EVENT.register((entity, jumpCooldown) ->
        {
            // Player must be in air for this to work
            if(!(entity instanceof PlayerEntity) || entity.isTouchingWater() || jumpCooldown > 5)
                return false;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.MOON_JUMP, entity);
            if(level == 0)
                return false;

            NbtCompound tag = ((_IEntityPersistentNbt)entity).get();
            int additionalJumps = tag.getInt(MOON_JUMPS_TAG);
            if(additionalJumps >= level*2)
                return false;

            entity.fallDistance = 0.0F;
            tag.putInt(MOON_JUMPS_TAG, additionalJumps+1);

            new MoonJumpEffectsMessage(entity).sendToServer();
            return true;
        });

        PlayerLandCallback.EVENT.register(((player, fallDistance) ->
            ((_IEntityPersistentNbt)player).get().remove(MOON_JUMPS_TAG))
        );
    }
}
