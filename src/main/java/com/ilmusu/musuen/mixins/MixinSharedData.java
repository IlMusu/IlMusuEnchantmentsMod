package com.ilmusu.musuen.mixins;

public class MixinSharedData
{
    public static boolean isGeneratingFromEnchantingTable = false;
    public static int skullsAroundEnchantingTable = 0;

    public static boolean shouldOverrideEnchantmentTargetAcceptableCheck = false;
    public static boolean overrideEnchantmentTargetAcceptable = false;
}