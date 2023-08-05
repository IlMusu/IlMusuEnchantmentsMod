package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.EntityItemDropCallback;
import com.ilmusu.musuen.client.particles.colored.ColoredParticleEffect;
import com.ilmusu.musuen.mixins.interfaces._IPlayerTickers;
import com.ilmusu.musuen.mixins.mixin.AccessorTridentEntity;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class TelekinesisEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public TelekinesisEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.CHARGEABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel()
    {
        return ModConfigurations.getEnchantmentMinLevel(this, 1);
    }

    @Override
    public int getMaxLevel()
    {
        return ModConfigurations.getEnchantmentMaxLevel(this, 1);
    }

    static
    {
        EntityItemDropCallback.BEFORE.register(((entity, item, source) ->
        {
            if(source == null || entity.getWorld().isClient || !(source.getAttacker() instanceof PlayerEntity player))
                return true;

            int level;
            if(source.getSource() instanceof AccessorTridentEntity trident)
                level = EnchantmentHelper.getLevel(ModEnchantments.TELEKINESIS, trident.getTridentStack());
            else
                level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.TELEKINESIS, player);

            if(level == 0)
                return true;

            ((_IPlayerTickers)player).addTicker(new _IPlayerTickers.Ticker(ModUtils.range(player.getRandom(), 25, 50))
                .onExiting((ticker) ->
                {
                    if(!item.isAlive() || !player.isAlive())
                        return;

                    // Spawning effect particle
                    int count = ModUtils.range(player.getRandom(), 10, 20);
                    ParticleEffect particle = new ColoredParticleEffect(new Color(113, 50, 168)).life(20).size(0.2F);
                    ((ServerWorld)item.getWorld()).spawnParticles(particle, item.getX(), item.getY()+0.25F, item.getZ(), count, 0, 0, 0, 0.02F);
                    // Playing teleport sound
                    Vec3d pos = item.getPos();
                    float pitch = ModUtils.range(player.getWorld().getRandom(), 1.0F, 1.2F);
                    item.getWorld().playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.AMBIENT, 0.8F, pitch);

                    // Teleporting the stack to the player
                    item.setPosition(player.getPos());
                }));

            return true;
        }));
    }
}
