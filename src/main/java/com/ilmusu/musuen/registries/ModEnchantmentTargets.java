package com.ilmusu.musuen.registries;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.enchantment.EnchantmentTarget;

public class ModEnchantmentTargets
{
    public static EnchantmentTarget ELYTRA;
    public static EnchantmentTarget SHIELD;
    public static EnchantmentTarget REACHER;
    public static EnchantmentTarget CHARGEABLE;

    public static void initialize()
    {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String enchantmentTarget = remapper.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget)
                .addEnumSubclass("MUSUEN$ELYTRA", "com.ilmusu.musuen.mixins.asm.EnumExtensionEnchantmentTarget$Elytra").build();
        ClassTinkerers.enumBuilder(enchantmentTarget)
                .addEnumSubclass("MUSUEN$SHIELD", "com.ilmusu.musuen.mixins.asm.EnumExtensionEnchantmentTarget$Shield").build();
        ClassTinkerers.enumBuilder(enchantmentTarget)
                .addEnumSubclass("MUSUEN$REACHER", "com.ilmusu.musuen.mixins.asm.EnumExtensionEnchantmentTarget$Reacher").build();
        ClassTinkerers.enumBuilder(enchantmentTarget)
                .addEnumSubclass("MUSUEN$CHARGEABLE", "com.ilmusu.musuen.mixins.asm.EnumExtensionEnchantmentTarget$Chargeable").build();
    }

    public static void register()
    {
        ELYTRA = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$ELYTRA");
        SHIELD = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$SHIELD");
        REACHER = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$REACHER");
        CHARGEABLE = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$CHARGEABLE");
    }
}
