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
    public static EnchantmentTarget ARROW_SHOOTER;
    public static EnchantmentTarget HOE;

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
        ClassTinkerers.enumBuilder(enchantmentTarget)
                .addEnumSubclass("MUSUEN$ARROW_SHOOTER", "com.ilmusu.musuen.mixins.asm.EnumExtensionEnchantmentTarget$ArrowShooter").build();
        ClassTinkerers.enumBuilder(enchantmentTarget)
                .addEnumSubclass("MUSUEN$HOE", "com.ilmusu.musuen.mixins.asm.EnumExtensionEnchantmentTarget$Hoe").build();
    }

    public static void register()
    {
        ELYTRA = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$ELYTRA");
        SHIELD = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$SHIELD");
        REACHER = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$REACHER");
        CHARGEABLE = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$CHARGEABLE");
        ARROW_SHOOTER = ClassTinkerers.getEnum(EnchantmentTarget.class,"MUSUEN$ARROW_SHOOTER");
        HOE = ClassTinkerers.getEnum(EnchantmentTarget.class, "MUSUEN$HOE");
    }
}
