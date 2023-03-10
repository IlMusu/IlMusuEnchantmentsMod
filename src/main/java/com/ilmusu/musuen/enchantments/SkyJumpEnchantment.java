package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.LivingEntityAirJumpCallback;
import com.ilmusu.musuen.callbacks.PlayerLandCallback;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.networking.messages.SkyJumpEffectsMessage;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class SkyJumpEnchantment extends Enchantment
{
    private static final String SKY_JUMPS_TAG = Resources.MOD_ID+".is_phasing";

    public SkyJumpEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 0);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 5);
    }

    @Override
    protected boolean canAccept(Enchantment other)
    {
        return !(other instanceof SkyJumpEnchantment) &&
               !(other instanceof LongJumpEnchantment) &&
               !(other == Enchantments.FEATHER_FALLING);
    }

    static
    {
        LivingEntityAirJumpCallback.EVENT.register((entity, jumpCooldown) ->
        {
            // Player must be in air for this to work
            if(!(entity instanceof PlayerEntity) || entity.isTouchingWater() || jumpCooldown > 5)
                return false;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.SKY_JUMP, entity);
            if(level == 0)
                return false;

            NbtCompound tag = ((_IEntityPersistentNbt)entity).getPNBT();
            int additionalJumps = tag.getInt(SKY_JUMPS_TAG);
            if(additionalJumps >= level*2)
                return false;

            entity.fallDistance = 0.0F;
            tag.putInt(SKY_JUMPS_TAG, additionalJumps+1);

            new SkyJumpEffectsMessage(entity).sendToServer();
            return true;
        });

        PlayerLandCallback.EVENT.register(((player, fallDistance) ->
            ((_IEntityPersistentNbt)player).getPNBT().remove(SKY_JUMPS_TAG))
        );
    }
}
