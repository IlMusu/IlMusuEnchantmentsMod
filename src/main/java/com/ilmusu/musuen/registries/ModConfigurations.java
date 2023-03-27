package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Configuration;
import com.ilmusu.musuen.Resources;

public class ModConfigurations
{
    public enum ENCHANTMENTS_CONFIGS {ENABLED, MIN_LEVEL, MAX_LEVEL}

    public static final Configuration ENCHANTMENTS = new Configuration(Resources.MOD_ID, "enchantments");

    public static void load()
    {
        // Loading the current configuration saved on file
        ENCHANTMENTS.load();
    }
}
