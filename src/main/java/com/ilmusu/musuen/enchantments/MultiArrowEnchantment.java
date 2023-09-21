package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.callbacks.ProjectileShotCallback;
import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MultiArrowEnchantment extends Enchantment
{
    public MultiArrowEnchantment(Rarity weight, int minLevel, int maxLevel)
    {
        super(weight, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
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
        ProjectileShotCallback.AFTER.register((shooter, projectileContainer, projectile) ->
        {
            if(!(projectileContainer.getItem() instanceof BowItem))
                return;
            if(!(shooter instanceof PlayerEntity player))
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.MULTI_ARROW, projectileContainer);
            if(level == 0)
                return;

            float maxSpreed = 0.4F;
            // Getting a vector perpendicular to the look direction
            Vec3d look = player.getRotationVector();
            Vec3d vec0 = new Vec3d(look.z, -look.x, look.y).normalize();
            Vec3d vec1 = look.crossProduct(vec0).normalize();
            for(int i=0; i<level; ++i)
            {
                Vec3d offset = vec0.multiply(ModUtils.range(shooter.getRandom(), 0.1, maxSpreed));
                offset = offset.add(vec1.multiply(ModUtils.range(shooter.getRandom(), 0.1, maxSpreed)));
                shootArrow(player, projectileContainer, player.getItemUseTimeLeft(), offset);
            }
        });
    }

    // Copied from the bow item since there is no dedicated function
    private static void shootArrow(PlayerEntity player, ItemStack bow, int remainingUseTicks, Vec3d offset)
    {
        World world = player.getWorld();

        // Getting the projectile from the player inventory (if creative returns a new arrow if none)
        ItemStack arrows = player.getProjectileType(bow);
        if(arrows.isEmpty())
            return;

        // The pull must be enough
        int maxUseTime = bow.getItem().getMaxUseTime(bow);
        float pull = BowItem.getPullProgress((int)BowItem.getPullProgress(maxUseTime) - remainingUseTicks);
        if(pull < 0.1)
            return;

        int infinity = EnchantmentHelper.getLevel(Enchantments.INFINITY, bow);

        if (!world.isClient)
        {
            ArrowItem arrowItem = (ArrowItem)(arrows.getItem() instanceof ArrowItem ? arrows.getItem() : Items.ARROW);
            PersistentProjectileEntity projectile = arrowItem.createArrow(world, arrows, player);
            projectile.setPosition(projectile.getPos().add(offset));
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, pull * 3.0f, 1.0f);
            if (pull == 1.0f)
                projectile.setCritical(true);

            int power = EnchantmentHelper.getLevel(Enchantments.POWER, bow);
            if (power > 0)
                projectile.setDamage(projectile.getDamage() + (double)power * 0.5 + 0.5);

            int punch = EnchantmentHelper.getLevel(Enchantments.PUNCH, bow);
            if (punch > 0)
                projectile.setPunch(punch);

            int flame = EnchantmentHelper.getLevel(Enchantments.FLAME, bow);
            if (flame > 0)
                projectile.setOnFireFor(100);

            bow.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand()));

            boolean isTypeNonGrabbable = arrows.isOf(Items.SPECTRAL_ARROW) || arrows.isOf(Items.TIPPED_ARROW);
            if (infinity > 0 || player.getAbilities().creativeMode && isTypeNonGrabbable)
                projectile.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

            world.spawnEntity(projectile);
        }

        float pitch = 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + pull * 0.5f;
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, pitch);

        if (infinity == 0 && !player.getAbilities().creativeMode)
            arrows.decrement(1);

        player.incrementStat(Stats.USED.getOrCreateStat(bow.getItem()));
    }
}
