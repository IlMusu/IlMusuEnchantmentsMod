package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.PlayerTickCallback;
import com.ilmusu.musuen.registries.ModConfigurations;
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
    public AttractionEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
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

    static
    {
        PlayerTickCallback.AFTER.register((player ->
        {
            if(player.getWorld().isClient)
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.ATTRACTION, player);
            if(level <= 0)
                return;

            // Computing the attraction radius
            int radius = level*2;
            Box box = Box.from(player.getPos()).expand(radius);

            // Getting item and experience orb entities to attract them toward the player
            List<Entity> entities = new ArrayList<>();
            entities.addAll(player.getWorld().getEntitiesByType(EntityType.ITEM, box, item -> true));
            entities.addAll(player.getWorld().getEntitiesByType(EntityType.EXPERIENCE_ORB, box, item -> true));

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
