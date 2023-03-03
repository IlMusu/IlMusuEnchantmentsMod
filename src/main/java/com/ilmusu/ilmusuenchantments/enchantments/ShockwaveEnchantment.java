package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEnchantmentExtensions;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IPlayerTickers;
import com.ilmusu.ilmusuenchantments.networking.messages.ShockwaveEffectMessage;
import com.ilmusu.ilmusuenchantments.networking.messages.SwingHandMessage;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import com.ilmusu.ilmusuenchantments.utils.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

import java.util.concurrent.atomic.AtomicReference;

public class ShockwaveEnchantment extends Enchantment implements _IEnchantmentExtensions
{
    public ShockwaveEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean shouldUseStackInsteadOfTargetCheck()
    {
        return true;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack)
    {
        return stack.getItem() instanceof ShieldItem;
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
                entity.damage(DamageSource.player(user), damage);
            }
        }));
    }

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

                BlockState stateDown = player.world.getBlockState(new BlockPos(pos).down());
                BlockState state = player.world.getBlockState(new BlockPos(pos));
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
}
