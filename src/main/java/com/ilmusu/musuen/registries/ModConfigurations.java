package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Configuration;
import com.ilmusu.musuen.Resources;

public class ModConfigurations
{
    public static final Configuration CONFIG = new Configuration(Resources.MOD_ID, "enchantments");

    public static void load()
    {
        // Loading the current configuration saved on file
        CONFIG.load();
    }
}
