package com.ilmusu.ilmusuenchantments.mixins.mixin;

import com.ilmusu.ilmusuenchantments.enchantments._IDemonicEnchantment;
import com.ilmusu.ilmusuenchantments.registries.ModDamageSources;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEnchantmentScreenHandlerDemonic;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

public abstract class DemonicEnchantingTableLogicMixin
{
    @Mixin(EnchantmentScreenHandler.class)
    public abstract static class DemonicEnchantmentScreenHandler implements _IEnchantmentScreenHandlerDemonic
    {
        @Shadow @Final private ScreenHandlerContext context;
        @Shadow @Final public int[] enchantmentId;

        private final int[] demonicEnchantments = new int[3];
        private static int slot;

        @Override
        public boolean hasDemonicEnchantment(int slot)
        {
            return demonicEnchantments[slot] == 1;
        }

        @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
        private void addDemonicEnchantmentProperty(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci)
        {
            AccessorScreenHandler self = (AccessorScreenHandler)this;
            self.addPropertyAccess(Property.create(this.demonicEnchantments, 0));
            self.addPropertyAccess(Property.create(this.demonicEnchantments, 1));
            self.addPropertyAccess(Property.create(this.demonicEnchantments, 2));
        }

        @Inject(method = "method_17411", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
        private void addDemonicEnchantmentPropertyValueHook(ItemStack itemStack, World world, BlockPos pos, CallbackInfo ci, int i, int j)
        {
            DemonicEnchantmentScreenHandler.slot = j;
        }

        @SuppressWarnings("unchecked")
        @Redirect(method = "method_17411", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
        private <E> E addDemonicEnchantmentPropertyValue(List<EnchantmentLevelEntry> list, int i)
        {
            // The demonic enchantment is placed always at the first position
            EnchantmentLevelEntry entry = list.get(0);

            // Overriding the first enchantment in case there is a demonic enchantment
            if(entry.enchantment instanceof _IDemonicEnchantment)
            {
                this.demonicEnchantments[DemonicEnchantmentScreenHandler.slot] = 1;
                return (E)entry;
            }

            this.demonicEnchantments[DemonicEnchantmentScreenHandler.slot] = 0;
            return (E) list.get(i);
        }

        @Inject(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"), cancellable = true)
        public void takeHealthFromNearbyEntities(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir)
        {
            this.context.run((world, pos) ->
            {
                Enchantment enchantment = Enchantment.byRawId(this.enchantmentId[id]);
                if(!(enchantment instanceof _IDemonicEnchantment))
                    return;

                float healthToConsume = (id == 0 ? 20 : id == 1 ? 40 : 80) * 2;

                // Consuming the health of non player entities
                List<LivingEntity> nearEntities = world.getNonSpectatingEntities(LivingEntity.class, new Box(pos.up()).expand(5, 2, 5));
                nearEntities.removeIf((entity) -> entity instanceof PlayerEntity);
                healthToConsume = takeHealthFromEntitiesRandomly(world.getRandom(), nearEntities, healthToConsume);

                if(healthToConsume <= 0)
                    return;

                // Consuming the health of player entities
                List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, new Box(pos.up()).expand(5, 2, 5));
                players.removeIf(PlayerEntity::isCreative);
                healthToConsume = takeHealthFromEntitiesRandomly(world.getRandom(), players, healthToConsume);

                if(healthToConsume <= 0 || player.isCreative())
                    return;

                cir.setReturnValue(false);

            });
        }

        private static float takeHealthFromEntitiesRandomly(Random rand, List<? extends LivingEntity> entities, float healthToConsume)
        {
            while(healthToConsume > 0 && entities.size() > 0)
            {
                int index = rand.nextInt(entities.size());
                LivingEntity entity = entities.get(index);
                // Computing the health do remove
                float damage = Math.min(entity.getHealth(), healthToConsume);
                // Prevent non-attackable entities from blocking loop
                if(!entity.damage(ModDamageSources.DEMONIC_ENCHANTING, damage) || entity.isDead())
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
        @Inject(method = "generateEnchantments", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
        private static void fixEnchantmentListWithDemonicEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir, List<EnchantmentLevelEntry> list)
        {
            // Getting the first extracted demonic enchantment
            EnchantmentLevelEntry demonic = list.stream().filter((entry) -> entry.enchantment instanceof _IDemonicEnchantment).findFirst().orElse(null);
            if(demonic == null)
                return;

            // Removing all the other and keeping only the first one, there can be only one,
            // and it is placed at the first position in the list
            list.removeIf((entry) -> entry.enchantment instanceof _IDemonicEnchantment);
            list.add(0, demonic);
        }
    }
}
