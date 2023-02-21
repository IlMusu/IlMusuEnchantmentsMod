package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.callbacks.LivingEntityJumpCheckCallback;
import com.ilmusu.ilmusuenchantments.callbacks.PlayerLandCallback;
import com.ilmusu.ilmusuenchantments.client.particles.colored.ColoredParticleEffect;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import com.ilmusu.ilmusuenchantments.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

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

            float pitch = ModUtils.range(entity.getRandom(), 0.4F, 0.6F);
            entity.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 0.4F, pitch, false);

            Color gray = new Color(220, 220, 220);
            for(int i=0; i<10; ++i)
            {
                Color color = ModUtils.randomizeColor(entity.getRandom(), gray, 20);
                Vec3d pos = entity.getPos().add(ModUtils.randomInCircle(entity.getRandom()).multiply(0.5F));
                Vec3d vel = ModUtils.randomInCircle(entity.getRandom()).multiply(0.1F);
                ParticleEffect particle = new ColoredParticleEffect(color).life(30).size(0.4F);
                entity.world.addParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
            }
            return true;
        });

        PlayerLandCallback.EVENT.register(((player, fallDistance) ->
        {
            ((_IEntityPersistentNbt)player).get().remove(MOON_JUMPS_TAG);
        }));
    }
}
