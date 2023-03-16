package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.client.particles.eblock.BlockParticleEffect;
import com.ilmusu.musuen.mixins.interfaces._IPlayerTickers;
import com.ilmusu.musuen.networking.messages.ShockwaveEffectMessage;
import com.ilmusu.musuen.networking.messages.SwingHandMessage;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShockwaveEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public ShockwaveEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.SHIELD, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel()
    {
        return ModEnchantments.getMinLevel(this, 0);
    }

    @Override
    public int getMaxLevel()
    {
        return ModEnchantments.getMaxLevel(this, 3);
    }

    public static void damageEntitiesWithShockwave(PlayerEntity user, Vec3d pos, Vec3d direction, Vec3d perpendicular, float size, float damage)
    {
        Vec3d sideVec = perpendicular.multiply(size);
        Box box = new Box(pos, pos).expand(sideVec.x, 0.5F, sideVec.z);
        user.world.getOtherEntities(user, box).forEach((entity ->
        {
            if(entity instanceof LivingEntity living && living.isAlive())
            {
                Vec3d knockbackVec = direction.multiply(-1).add(ModUtils.randomInCircle(user.getRandom()).multiply(0.2F)).normalize();
                living.takeKnockback(ModUtils.range(living.getRandom(), 0.2F, 0.3F), knockbackVec.x, knockbackVec.z);
                entity.damage(user.world.getDamageSources().playerAttack(user), damage);
            }
        }));
    }

    @SuppressWarnings("unused")
    public static void onShockwaveKeyBindingPress(PlayerEntity player, int modifiers)
    {
        // The player must have a shield in its hand and be using it
        ItemStack stack = player.getMainHandStack();
        if(!(stack.getItem() instanceof ShieldItem) || !player.isUsingItem())
            return;
        if(player.getItemCooldownManager().isCoolingDown(stack.getItem()))
            return;

        int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.SHOCKWAVE, player);
        if(level == 0)
            return;

        AtomicReference<Vec3d> posAtom = new AtomicReference<>(player.getPos());
        Vec3d direction = player.getRotationVector().multiply(1, 0, 1).normalize();
        Vec3d perpendicular = new Vec3d(-direction.z, 0, direction.x);

        float size = 1.0F + level;
        float damage = 1.0F + level*0.7F;
        int duration = 20*(1+level);

        // Swinging player hand
        new SwingHandMessage().sendToClient((ServerPlayerEntity)player);
        // Playing shield hit sound
        float pitch = ModUtils.range(player.world.getRandom(), 0.6F, 0.8F);
        player.world.playSoundFromEntity(null, player, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, pitch);

        ((_IPlayerTickers)player).addTicker(new _IPlayerTickers.Ticker(duration)
            .onTicking(ticker ->
            {
                // Updating the position
                Vec3d pos = posAtom.get().add(direction.multiply(0.4F));
                posAtom.set(pos);

                BlockState stateDown = player.world.getBlockState(BlockPos.ofFloored(pos).down());
                BlockState state = player.world.getBlockState(BlockPos.ofFloored(pos));
                if(!stateDown.getMaterial().blocksMovement() || state.getMaterial().blocksMovement())
                {
                    ticker.setFinished();
                    return;
                }

                // Spawning particles on the client
                new ShockwaveEffectMessage(pos, direction, size).sendToClientsTrackingAndSelf(player);
                // Damaging entities
                ShockwaveEnchantment.damageEntitiesWithShockwave(player, pos, direction, perpendicular, size, damage);
            }));

        // Damaging the shield
        stack.damage(5, player, e -> e.sendToolBreakStatus(Hand.MAIN_HAND));
        // Disabling the shield for some time
        player.getItemCooldownManager().set(stack.getItem(), 20);
    }

    public static void spawnShockwaveEffects(World world, Random rand, Vec3d pos, float size, Vec3d direction)
    {
        // The vector perpendicular to the direction
        Vec3d side = new Vec3d(-direction.z, 0, direction.x);
        // The state under the specified position
        BlockState state = world.getBlockState(BlockPos.ofFloored(pos).down());

        for(float s=0.1F; s<=size; s+=0.3F)
        {
            for(float s1 : List.of(-s, s))
            {
                float parabola = -0.2F*s1*s1;
                Vec3d regression = direction.multiply(parabola);
                Vec3d pos1 = pos.add(side.multiply(s1)).add(regression).add(ModUtils.randomInCircle(rand).multiply(0.5F));
                Vec3d vel = ModUtils.randomInCircle(rand).multiply(0.1F);

                int life = ModUtils.range(rand, 5, 8);
                float size0 = ModUtils.range(rand, 0.10F, 0.20F);
                float height = ModUtils.range(rand, 0.10F, 0.3F);

                // Spawning particles
                for(int i=0; i<3; ++i)
                {
                    BlockParticleEffect particle = new BlockParticleEffect(state).life(life).size(size0).gravity(0.25F);
                    world.addParticle(particle, pos1.x, pos1.y+height, pos1.z, vel.x, vel.y, vel.z);
                }
            }
        }

        // Playing the block sound
        float pitch = ModUtils.range(rand, 0.8F, 1.2F);
        world.playSound(pos.x, pos.y, pos.z, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 0.6F, pitch, false);
    }
}
