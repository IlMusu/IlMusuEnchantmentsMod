package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Configuration;
import com.ilmusu.musuen.Resources;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

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
        public static final String VEIN_MINING_HAS_WHITE_LIST = "vein_mining_has_white_list";
        public static final String VEIN_MINING_WHITE_LIST = "vein_mining_white_list";
    }

    public static final Configuration MOD = new Configuration(Resources.MOD_ID, "mod");
    public static final Configuration ENCHANTMENTS = new Configuration(Resources.MOD_ID, "enchantments");


    public static void load()
    {
        // Loading the configuration for the mod from file
        MOD.load();
        MOD.setConfigIfAbsent(
            ModConfig.DEMONIC_ENCHANTING_ENABLED, true, """
            # The demonic enchanting is the mechanic that allows the enchanting table to take hearts from nearby
            # entities to provide demonic enchantments. Setting this flag as "false" disables this mechanic and
            # the demonic enchantments will be obtained thought the enchanting table as vanilla enchantments.""",
            Object::toString,
            Boolean::parseBoolean);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_ENABLED_WHILE_SNEAKING, true, """
            # Normally, the vein mining enchantment is always enabled. Setting this flag as "false" allows the vein
            # mining enchantment logic to be disabled when the player is sneaking and be active otherwise.""",
            Object::toString,
            Boolean::parseBoolean);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_HAS_WHITE_LIST, false,"""
            # Normally, the vein mining enchantment digs any block that suited for the enchanted tool. Setting this
            # flag as "false" makes the dig logic work only on the white list of blocks defined on the next config.""",
            Object::toString,
            Boolean::parseBoolean);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_WHITE_LIST, List.of(), """
            # This is the white list of items that is allowed for the vein mining enchantment when the white list
            # is enabled. The blocks must be written inside the square brackets, separated by commas and with no
            # blank spaces: the blocks ids can be found in game using the F3+H "Advanced Tooltips".
            # An example is the following: [minecraft:stone,minecraft:dirt,minecraft:coal_ore]""",
            ModConfigurations::listOfIdentifiesToString,
            ModConfigurations::stringToListOfIdentifiers
        );
        // Loading the configuration for the enchantments from file
        ENCHANTMENTS.load();
    }

    public static void write()
    {
        MOD.writeConfigurationFile();
        ENCHANTMENTS.writeConfigurationFile();
    }

    public static boolean isDemonicEnchantingEnabled()
    {
        return (boolean) MOD.getConfigValue(ModConfig.DEMONIC_ENCHANTING_ENABLED);
    }

    public static boolean shouldEnableVeinMiningWhileSneaking()
    {
        return (boolean) MOD.getConfigValue(ModConfig.VEIN_MINING_ENABLED_WHILE_SNEAKING);
    }

    public static boolean isVeinMiningEnchantmentWhiteListed()
    {
        return (boolean) MOD.getConfigValue(ModConfig.VEIN_MINING_HAS_WHITE_LIST);
    }

    @SuppressWarnings("unchecked")
    public static boolean isBlockVeinMiningWhiteListed(BlockState state)
    {
        if(!isVeinMiningEnchantmentWhiteListed())
            return true;

        List<Identifier> whiteList = (List<Identifier>) MOD.getConfigValue(ModConfig.VEIN_MINING_WHITE_LIST);
        return whiteList.contains(Registry.BLOCK.getId(state.getBlock()));
    }

    private static String getEnchantmentConfigKey(String name, String config)
    {
        return name+"."+config.toLowerCase();
    }

    public static boolean isEnchantmentEnabled(String name)
    {
        String enabledKey = getEnchantmentConfigKey(name, EnchantmentsConfig.ENABLED);
        Object configValue = ENCHANTMENTS.getConfigValue(enabledKey);
        if(configValue != null)
            return (boolean) configValue;

        ENCHANTMENTS.setConfigIfAbsent(enabledKey, true, Object::toString, Boolean::parseBoolean);
        return true;
    }

    public static int getEnchantmentMinLevel(Enchantment enchantment, int min)
    {
        String name = Registry.ENCHANTMENT.getId(enchantment).getPath();
        String key = getEnchantmentConfigKey(name, EnchantmentsConfig.MIN_LEVEL);
        Object configValue = ENCHANTMENTS.getConfigValue(key);
        if(configValue != null)
            return (int) configValue;

        try {
            ENCHANTMENTS.setConfigIfAbsent(key, min, Object::toString, Integer::parseInt);
            min = Math.max(1, (int) ENCHANTMENTS.getConfigValue(key));
        }
        catch (NumberFormatException exception) {
            Resources.LOGGER.error("Could not read minimum level for "+name+" enchantment! Defaulting to "+min+"!");
        }

        return min;
    }

    public static int getEnchantmentMaxLevel(Enchantment enchantment, int max)
    {
        String name = Registry.ENCHANTMENT.getId(enchantment).getPath();
        String key = getEnchantmentConfigKey(name, EnchantmentsConfig.MAX_LEVEL);
        Object configValue = ENCHANTMENTS.getConfigValue(key);
        if(configValue != null)
            return (int) configValue;

        try {
            ENCHANTMENTS.setConfigIfAbsent(key, max, Object::toString, Integer::parseInt);
            max = Math.min((int) ENCHANTMENTS.getConfigValue(key), 255);
        }
        catch (NumberFormatException exception) {
            Resources.LOGGER.error("Could not read maximum level for "+name+" enchantment! Defaulting to "+max+"!");
        }

        return max;
    }

    @SuppressWarnings("unchecked")
    public static String listOfIdentifiesToString(Object identifierList)
    {
        List<Identifier> identifiers = (List<Identifier>)identifierList;
        List<String> list = identifiers.stream().map(Identifier::toString).toList();
        return list.toString();
    }

    public static List<Identifier> stringToListOfIdentifiers(String string)
    {
        string = string.trim();
        string = string.substring(1, string.length()-1);
        List<String> list = List.of(string.split(","));
        return list.stream().map((str) -> new Identifier(str.strip())).toList();
    }
}
