package com.ilmusu.musuen.mixins.asm;

import com.ilmusu.musuen.registries.ModConfigurations;
import net.minecraft.item.*;

public class EnumExtensionEnchantmentTarget
{
    public static class Tool extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof MiningToolItem || item instanceof SwordItem || item instanceof TridentItem
                    || item instanceof BowItem || item instanceof CrossbowItem;
        }
    }

    public static class Reacher extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof MiningToolItem || item instanceof SwordItem || item instanceof TridentItem;
        }
    }

    public static class Chargeable extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem;
        }
    }

    public static class ArrowShooter extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof BowItem || item instanceof CrossbowItem;
        }
    }

    public static class VeinMiners extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return ModConfigurations.canToolVeinMine(item);
        }
    }

    public static class Elytra extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof ElytraItem;
        }
    }

    public static class Shield extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof ShieldItem;
        }
    }

    public static class Hoe extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof HoeItem;
        }
    }
}