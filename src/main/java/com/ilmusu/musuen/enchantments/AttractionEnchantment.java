package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerTickCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AttractionEnchantment extends Enchantment
{
    public AttractionEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
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
        PlayerTickCallback.AFTER.register((player ->
        {
            if(player.world.isClient)
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.ATTRACTION, player);
            if(level <= 0)
                return;

            // Computing the attraction radius
            int radius = level*2;
            Box box = Box.from(player.getPos()).expand(radius);

            // Getting item and experience orb entities to attract them toward the player
            List<Entity> entities = new ArrayList<>();
            entities.addAll(player.world.getEntitiesByType(EntityType.ITEM, box, item -> true));
            entities.addAll(player.world.getEntitiesByType(EntityType.EXPERIENCE_ORB, box, item -> true));

            // Attracting the entities toward the player
            entities.forEach(item ->
            {
                // Moving entity and updating on client
                Vec3d vec = player.getPos().subtract(item.getPos()).normalize();
                Vec3d delta = item.getVelocity();
                item.setVelocity(delta.add(vec.multiply(0.03F)));
                item.velocityModified = true;
            });
        }));
    }
}
