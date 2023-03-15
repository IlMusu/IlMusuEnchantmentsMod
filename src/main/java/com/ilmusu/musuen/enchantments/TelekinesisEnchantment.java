package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.EntityDropCallback;
import com.ilmusu.musuen.client.particles.colored.ColoredParticleEffect;
import com.ilmusu.musuen.mixins.interfaces._IPlayerTickers;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
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
        return ModEnchantments.getMinLevel(this, 0);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 1);
    }

    static
    {
        EntityDropCallback.EVENT.register(((entity, item, source) ->
        {
            if(source == null || entity.world.isClient || !(source.getAttacker() instanceof PlayerEntity player))
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
                    ((ServerWorld)item.world).spawnParticles(particle, item.getX(), item.getY()+0.25F, item.getZ(), count, 0, 0, 0, 0.02F);
                    // Playing teleport sound
                    Vec3d pos = item.getPos();
                    float pitch = ModUtils.range(player.world.getRandom(), 1.0F, 1.2F);
                    item.world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.AMBIENT, 0.8F, pitch);

                    // Teleporting the stack to the player
                    item.setPosition(player.getPos());
                }));

            return true;
        }));
    }
}
