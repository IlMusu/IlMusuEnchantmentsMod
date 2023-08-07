package com.ilmusu.musuen.mixins.mixin;

import com.google.common.collect.Lists;
import com.ilmusu.musuen.client.particles.colored_enchant.ColoredGlyphParticleEffect;
import com.ilmusu.musuen.enchantments._IDemonicEnchantment;
import com.ilmusu.musuen.mixins.MixinSharedData;
import com.ilmusu.musuen.mixins.interfaces._IDemonicEnchantmentScreenHandler;
import com.ilmusu.musuen.mixins.interfaces._IModDamageSources;
import com.ilmusu.musuen.registries.ModConfigurations;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DemonicEnchantingTableLogicMixin
{
    @Mixin(EnchantmentScreenHandler.class)
    public abstract static class DemonicEnchantmentScreenHandler implements _IDemonicEnchantmentScreenHandler
    {
        @Shadow @Final private ScreenHandlerContext context;
        @Shadow @Final public int[] enchantmentId;

        @Unique private static final int DEMONIC_ENCHANTING_ENTITY_RADIUS = 7;
        @Unique private final int[] demonicEnchantments = new int[3];

        @Override
        public boolean hasDemonicEnchantment(int slot)
        {
            return demonicEnchantments[slot] == 1;
        }

        @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
        private void addDemonicEnchantmentProperty(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci)
        {
            // Adding properties to the screen handler so that this data is synced between client and server
            AccessorScreenHandler self = (AccessorScreenHandler)this;
            self.addPropertyAccess(Property.create(this.demonicEnchantments, 0));
            self.addPropertyAccess(Property.create(this.demonicEnchantments, 1));
            self.addPropertyAccess(Property.create(this.demonicEnchantments, 2));
        }

        @Inject(method = "method_17411", at = @At(value = "HEAD"))
        private void checkSkullsAroundEnchantingTable(ItemStack itemStack, World world, BlockPos pos, CallbackInfo ci)
        {
            // Checking if there are all the skulls around
            MixinSharedData.skullsAroundEnchantingTable = 0;

            // Count the number of skulls around the enchanting table
            for(BlockPos offset : _IDemonicEnchantmentScreenHandler.SKULLS_OFFSETS)
                if(_IDemonicEnchantmentScreenHandler.isValidSkull(world.getBlockState(pos.add(offset))))
                    ++MixinSharedData.skullsAroundEnchantingTable;
        }

        @Inject(method = "generateEnchantments", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/enchantment/EnchantmentHelper;generateEnchantments(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;"
        ))
        private void storeGeneratingFromEnchantingTableFlag(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir)
        {
            MixinSharedData.isGeneratingFromEnchantingTable = true;
        }

        @Inject(method = "generateEnchantments", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/enchantment/EnchantmentHelper;generateEnchantments(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;",
                shift = At.Shift.AFTER
        ))
        private void removeGeneratingFromEnchantingTableFlag(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir)
        {
            MixinSharedData.isGeneratingFromEnchantingTable = false;
        }

        @Inject(method = "method_17411", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
                value = "INVOKE",
                target = "Ljava/util/List;isEmpty()Z"
        ))
        private void modifyEnchantmentListForSlotDisplay(ItemStack itemStack, World world, BlockPos pos, CallbackInfo ci,
                                                         int i, int j, List<EnchantmentLevelEntry> list)
        {
            // Demonic enchanting should be enabled
            if(!ModConfigurations.isDemonicEnchantingEnabled())
                return;

            // This is a fix which prevents using the @Redirect
            EnchantmentLevelEntry entry = list.get(0);
            if(entry.enchantment instanceof _IDemonicEnchantment)
            {
                // Marking that the enchantment in that slot is demonic
                this.demonicEnchantments[j] = 1;
                // Leaving only that demonic enchantment so that it will be the one displayed
                list.clear();
                list.add(entry);
            }
            else
            {
                // Resetting the flag if there is not a demonic enchantment
                this.demonicEnchantments[j] = 0;
            }
        }

        @Inject(method = "onButtonClick", cancellable = true, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"
        ))
        private void takeHealthFromNearbyEntities(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir)
        {
            // Demonic enchanting should be enabled
            if(!ModConfigurations.isDemonicEnchantingEnabled())
                return;

            this.context.run((world, pos) ->
            {
                Enchantment enchantment = Enchantment.byRawId(this.enchantmentId[id]);
                if(!(enchantment instanceof _IDemonicEnchantment))
                    return;

                float healthToConsume = (id == 0 ? 10 : id == 1 ? 20 : 40) * 2;
                Box box = new Box(pos.up()).expand(DEMONIC_ENCHANTING_ENTITY_RADIUS, 2, DEMONIC_ENCHANTING_ENTITY_RADIUS);

                // Consuming the health of non player entities
                List<LivingEntity> nearEntities = world.getNonSpectatingEntities(LivingEntity.class, box);
                nearEntities.removeIf((entity) -> entity instanceof PlayerEntity);
                healthToConsume = takeHealthFromEntitiesRandomly(world, nearEntities, healthToConsume);

                if(healthToConsume <= 0)
                    return;

                // Consuming the health of player entities
                List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, box);
                players.removeIf(PlayerEntity::isCreative);
                healthToConsume = takeHealthFromEntitiesRandomly(world, players, healthToConsume);

                if(healthToConsume <= 0 || player.isCreative())
                    return;

                cir.setReturnValue(false);

            });
        }

        @Unique
        private static float takeHealthFromEntitiesRandomly(World world, List<? extends LivingEntity> entities, float healthToConsume)
        {
            while(healthToConsume > 0 && !entities.isEmpty())
            {
                int index = world.getRandom().nextInt(entities.size());
                LivingEntity entity = entities.get(index);
                // Computing the health do remove
                float damage = Math.min(entity.getHealth(), healthToConsume);
                // Prevent non-attackable entities from blocking loop
                DamageSource source = ((_IModDamageSources)world.getDamageSources()).demonicEnchanting();
                if(!entity.damage(source, damage) || entity.isDead())
                    entities.remove(index);
                // Removing the health and check for death
                // Updating the remaining health to consume
                healthToConsume -= damage;
            }

            return healthToConsume;
        }
    }


    @Mixin(EnchantmentHelper.class)
    public abstract static class FixEnchantmentGenerationList
    {
        @Unique private static int enchantingPower = 0;

        @Inject(method = "generateEnchantments", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
                value = "INVOKE",
                target = "Ljava/util/List;isEmpty()Z",
                ordinal = 0
        ))
        private static void storeEnchantingPower(Random random, ItemStack stack, int level, boolean treasureAllowed,
                                                 CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir, List<?> list,
                                                 Item item, int i, float f, List<EnchantmentLevelEntry> list2)
        {
            // Storing the modified power level
            FixEnchantmentGenerationList.enchantingPower = level;
        }

        @Inject(method = "generateEnchantments", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("TAIL"))
        private static void fixEnchantmentListWithDemonicEnchantments(Random random, ItemStack stack, int level,
            boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir, List<EnchantmentLevelEntry> list)
        {
            // Demonic enchanting should be enabled for demonic enchantments to be added manually
            if(!ModConfigurations.isDemonicEnchantingEnabled())
                return;

            // Check if a demonic enchantment can be added to the list
            if(!MixinSharedData.isGeneratingFromEnchantingTable || MixinSharedData.skullsAroundEnchantingTable < 3)
                return;

            // There is a 1/10 chance of extracting a demonic enchantment
            float extracted = random.nextFloat();
            float additional = (MixinSharedData.skullsAroundEnchantingTable-3)*0.05F;
            if(extracted > 0.10F+additional)
                return;

            // Getting only the demonic enchantments
            int power = FixEnchantmentGenerationList.enchantingPower;
            FixEnchantmentGenerationList.enchantingPower = 0;

            // Adding only one demonic enchantment at the beginning of the list
            List<EnchantmentLevelEntry> demonics = getPossibleDemonicEntries(power, stack);
            Weighting.getRandom(random, demonics).ifPresent((entry -> list.add(0, entry)));
        }

        // Copied and modified from the EnchantmentHelper
        @Unique
        private static List<EnchantmentLevelEntry> getPossibleDemonicEntries(int power, ItemStack stack)
        {
            ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();
            Item item = stack.getItem();
            boolean isBook = stack.isOf(Items.BOOK);

            block0: for (Enchantment enchantment : Registries.ENCHANTMENT)
            {
                if(!(enchantment instanceof _IDemonicEnchantment))
                    continue ;
                if(!enchantment.target.isAcceptableItem(item) && !isBook)
                    continue;

                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i)
                {
                    if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i))
                        continue;

                    list.add(new EnchantmentLevelEntry(enchantment, i));
                    continue block0;
                }
            }
            return list;
        }
    }

    @Mixin(EnchantingTableBlock.class)
    public abstract static class ChangeEnchantingTableBehavior
    {
        @Inject(method = "canAccessPowerProvider", at = @At("HEAD"), cancellable = true)
        private static void fixBookshelvesAccessWithSkulls(World world, BlockPos tablePos, BlockPos bookshelfOffset, CallbackInfoReturnable<Boolean> cir)
        {
            // This is a fix which prevents using the @Redirect
            BlockPos vanillaAirPos = tablePos.add(bookshelfOffset.getX()/2, bookshelfOffset.getY(), bookshelfOffset.getZ()/2);
            if(_IDemonicEnchantmentScreenHandler.isValidSkull(world.getBlockState(vanillaAirPos)))
                cir.setReturnValue(world.getBlockState(tablePos.add(bookshelfOffset)).isOf(Blocks.BOOKSHELF));
        }

        @Inject(method = "randomDisplayTick", at = @At("TAIL"))
        private void addDemonicEnchantingGlyphParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
        {
            // Demonic enchanting should be enabled for particles to spawn
            if(!ModConfigurations.isDemonicEnchantingEnabled())
                return;

            for(BlockPos offset : _IDemonicEnchantmentScreenHandler.SKULLS_OFFSETS)
            {
                if(random.nextInt(8) != 0 || !_IDemonicEnchantmentScreenHandler.isValidSkull(world.getBlockState(pos.add(offset))))
                    continue;

                Vec3d pos0 = new Vec3d(pos.getX() + 0.5, (double)pos.getY() + 2.0, (double)pos.getZ() + 0.5);
                Vec3d vel = new Vec3d(offset.getX()+random.nextFloat()-0.5, offset.getY()-random.nextFloat()-1.0, offset.getZ()+random.nextFloat()-0.5);
                ParticleEffect effect = new ColoredGlyphParticleEffect(new Color(107, 15, 15));
                world.addParticle(effect, pos0.x, pos0.y, pos0.z, vel.x, vel.y, vel.z);
            }
        }
    }
}