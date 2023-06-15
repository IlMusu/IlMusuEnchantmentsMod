package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Configuration;
import com.ilmusu.musuen.Resources;

public class ModConfigurations
{
    public static class EnchantmentsConfig
    {
        public static final String ENABLED = "enabled";
        public static final String MIN_LEVEL = "min_level";
        public static final String MAX_LEVEL = "max_level";
    }

    public static class ModConfig
    {
        public static final String DEMONIC_ENCHANTING_ENABLED = "demonic_enchanting_enabled";
        public static final String VEIN_MINING_ENABLED_WHILE_SNEAKING = "vein_mining_enabled_while_sneaking";
    }

    public static final Configuration MOD = new Configuration(Resources.MOD_ID, "mod");
    public static final Configuration ENCHANTMENTS = new Configuration(Resources.MOD_ID, "enchantments");

    public static void load()
    {
        // Loading the configuration for the mod from file
        MOD.load();
        MOD.setIfAbsent(ModConfig.DEMONIC_ENCHANTING_ENABLED, true);
        MOD.setIfAbsent(ModConfig.VEIN_MINING_ENABLED_WHILE_SNEAKING, true);
        // Loading the configuration for the enchantments from file
        ENCHANTMENTS.load();
    }


    public static boolean isDemonicEnchantingEnabled()
    {
        return Boolean.parseBoolean(MOD.getOrSet(ModConfig.DEMONIC_ENCHANTING_ENABLED, true));
    }

    public static boolean shouldDisableVeinMiningWhileSneaking()
    {
        return !Boolean.parseBoolean(MOD.getOrSet(ModConfig.VEIN_MINING_ENABLED_WHILE_SNEAKING, true));
    }
}
