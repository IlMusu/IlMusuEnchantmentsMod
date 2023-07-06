package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.LivingEntityAirJumpCallback;
import com.ilmusu.musuen.callbacks.PlayerLandCallback;
import com.ilmusu.musuen.client.particles.colored.ColoredParticleEffect;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.networking.messages.SkyJumpEffectMessage;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.awt.*;

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
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 5);
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

            new SkyJumpEffectMessage(entity).sendToServer();
            return true;
        });

        PlayerLandCallback.EVENT.register(((player, fallDistance) ->
            ((_IEntityPersistentNbt)player).getPNBT().remove(SKY_JUMPS_TAG))
        );
    }

    public static void spawnSkyJumpEffects(World world, Vec3d pos, Random random)
    {
        float pitch = ModUtils.range(random, 0.4F, 0.6F);
        world.playSound(pos.x, pos.y, pos.z, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 0.4F, pitch, false);

        Color gray = new Color(220, 220, 220);
        for(int i=0; i<10; ++i)
        {
            Color color = ModUtils.randomizeColor(random, gray, 20);
            Vec3d pos0 = pos.add(ModUtils.randomInCircle(random).multiply(0.5F));
            Vec3d vel = ModUtils.randomInCircle(random).multiply(0.1F);
            ParticleEffect particle = new ColoredParticleEffect(color).life(30).size(0.4F);
            world.addParticle(particle, pos0.x, pos0.y, pos0.z, vel.x, vel.y, vel.z);
        }
    }
}
