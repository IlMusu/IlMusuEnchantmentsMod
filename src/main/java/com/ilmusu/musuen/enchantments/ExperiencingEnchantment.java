package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.LivingEntityDamageCallback;
import com.ilmusu.musuen.callbacks.ProjectileShotCallback;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.registries.ModConfigurations;
import com.ilmusu.musuen.registries.ModEnchantmentTargets;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ExperiencingEnchantment extends Enchantment
{
    private static final String EXPERIENCING_TAG = Resources.MOD_ID+"."+"experiencing";

    public ExperiencingEnchantment(Rarity weight)
    {
        super(weight, ModEnchantmentTargets.TOOL, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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

    protected static float getExperienceDropped(int level)
    {
        return 1.0F + level*1.0F;
    }

    protected static float getExperienceDropProbability(int level)
    {
        return new ModUtils.Linear(1, 0.2F, 3, 0.5F).of(level);
    }

    static
    {
        ProjectileShotCallback.AFTER.register((shooter, stack, projectile) ->
        {
            if(!(projectile instanceof PersistentProjectileEntity))
                return;

            // The stack must have the enchantment
            int level = EnchantmentHelper.getLevel(ModEnchantments.EXPERIENCING, stack);
            if(level == 0)
                return;

            // Setting the tag for dropping more experience
            NbtCompound nbt = ((_IEntityPersistentNbt)projectile).getPNBT();
            nbt.putInt(EXPERIENCING_TAG, level);
        });

        LivingEntityDamageCallback.AFTER.register(((entity, source, damage) ->
        {
            int experiencingLevel = 0;
            // Getting the correct level from the correct source
            if(source.getSource() instanceof PersistentProjectileEntity projectile)
            {
                NbtCompound nbt = ((_IEntityPersistentNbt)projectile).getPNBT();
                experiencingLevel = nbt.getInt(EXPERIENCING_TAG);
                nbt.remove(EXPERIENCING_TAG);
            }
            else if(source.getAttacker() instanceof LivingEntity living)
            {
                // Does not activate when using a mining tool for attacking
                ItemStack stack = living.getMainHandStack();
                if (stack.getItem() instanceof MiningToolItem)
                    return 0.0F;
                experiencingLevel = EnchantmentHelper.getLevel(ModEnchantments.EXPERIENCING, stack);
            }

            float probability = getExperienceDropProbability(experiencingLevel);
            if(experiencingLevel == 0 || entity.getRandom().nextFloat() < probability)
                return 0.0F;

            float experience = getExperienceDropped(experiencingLevel) * damage * 0.5F;
            ExperienceOrbEntity.spawn((ServerWorld) entity.getWorld(), entity.getPos(), (int)Math.ceil(experience));
            return 0.0F;
        }));

        PlayerBlockBreakEvents.AFTER.register(((world, player, pos, state, entity) ->
        {
            // Check the stack, should be a mining and suited for the state
            ItemStack stack = player.getMainHandStack();
            if(!(stack.getItem() instanceof MiningToolItem tool) || !(tool.isSuitableFor(state)))
                return;

            int level = EnchantmentHelper.getLevel(ModEnchantments.EXPERIENCING, stack);
            float probability = getExperienceDropProbability(level);
            if(level == 0 || world.getRandom().nextFloat() < probability)
                return;

            float experience = getExperienceDropped(level) * state.getBlock().getHardness() * 0.2F;
            Vec3d center = new Vec3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
            ExperienceOrbEntity.spawn((ServerWorld) player.getWorld(), center, (int)Math.ceil(experience));
        }));
    }
}
