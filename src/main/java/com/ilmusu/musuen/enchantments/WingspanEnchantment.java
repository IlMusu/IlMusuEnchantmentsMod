package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.LivingEntityElytraLandCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.registries.ModSoundEvents;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WingspanEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public WingspanEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, ModEnchantmentTargets.ELYTRA, new EquipmentSlot[]{EquipmentSlot.CHEST});
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

    public static void takeWingspanKnockback(LivingEntity winged, LivingEntity entity, double strength)
    {
        if ((strength *= 1.0 - entity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) <= 0.0)
            return;

        entity.velocityDirty = true;
        Vec3d offset = entity.getPos().subtract(winged.getPos()).normalize();
        Vec3d knockback = new Vec3d(offset.getX(), 0.4F, offset.getZ()).multiply(strength);
        entity.setVelocity(entity.getVelocity().multiply(0.5F).add(knockback));
    }

    static
    {
        LivingEntityElytraLandCallback.EVENT.register((entity ->
        {
            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.WINGSPAN, entity);
            if(level == 0)
                return;

            float radius = 1.5F+level*1.5F;
            // The power of the knockback is based on both the level of the enchantment and the
            // velocity that player has when landing on the ground
            float power = (level*0.25F) + Math.min(3.0F, (float)entity.getVelocity().length()*1.4F);

            Box box = Box.from(entity.getPos()).expand(radius);
            entity.getWorld().getOtherEntities(entity, box, (other -> other instanceof LivingEntity))
                .forEach((living) -> takeWingspanKnockback(entity, (LivingEntity)living, power));

            float volume = Math.min(0.5F, power*0.35F);
            float pitch = ModUtils.range(entity.getWorld().getRandom(), 1.2F, 1.6F);
            entity.getWorld().playSoundFromEntity(null, entity, ModSoundEvents.LIVING_ELYTRA_WING_FLAP, SoundCategory.PLAYERS, volume, pitch);
        }));
    }
}
