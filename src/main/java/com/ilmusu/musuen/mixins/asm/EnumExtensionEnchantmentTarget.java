package com.ilmusu.musuen.mixins.asm;

import net.minecraft.item.*;

public class EnumExtensionEnchantmentTarget
{
    public static class Chargeable extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem;
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

    public static class Reacher extends EnchantmentTargetExtensible
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item instanceof MiningToolItem || item instanceof SwordItem || item instanceof TridentItem;
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
}