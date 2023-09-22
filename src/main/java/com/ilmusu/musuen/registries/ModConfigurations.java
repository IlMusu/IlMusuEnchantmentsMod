package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Configuration;
import com.ilmusu.musuen.Resources;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModConfigurations
{
    public static class EnchantmentsConfig
    {
        public static final String ENABLED = "enabled";
        public static final String RARITY = "rarity";
        public static final String MIN_LEVEL = "min_level";
        public static final String MAX_LEVEL = "max_level";
    }

    public static class ModConfig
    {
        public static final String DEMONIC_ENCHANTING_ENABLED = "demonic_enchanting_enabled";
        public static final String DEMONIC_DAMAGE_DEFAULT_CAMERA_TILT_DUMPER = "demonic_damage_default_camera_tilt_dumper";
        public static final String DEMONIC_DAMAGE_DEMONCTION_CAMERA_TILT_DUMPER = "demonic_damage_demonction_camera_tilt_dumper";
        public static final String VEIN_MINING_ENABLED_WHILE_SNEAKING = "vein_mining_enabled_while_sneaking";
        public static final String VEIN_MINING_ENABLED_WHILE_NOT_SNEAKING = "vein_mining_enabled_while_not_sneaking";
        public static final String VEIN_MINING_HAS_WHITE_LIST = "vein_mining_has_white_list";
        public static final String VEIN_MINING_WHITE_LIST = "vein_mining_white_list";
        public static final String VEIN_MINING_TOOLS = "vein_mining_tools";
    }

    public static final Configuration MOD = new Configuration(Resources.MOD_ID, "mod");
    public static final Configuration ENCHANTMENTS = new Configuration(Resources.MOD_ID, "enchantments");


    public static void load()
    {
        // Loading the existing mod configuration
        MOD.load();
        // Creating the configuration for the mod
        MOD.setConfigIfAbsent(
            ModConfig.DEMONIC_ENCHANTING_ENABLED, true, """
            # The demonic enchanting is the mechanic that allows the enchanting table to take hearts from nearby
            # entities to provide demonic enchantments. Setting this flag as "false" disables this mechanic and
            # the demonic enchantments will be obtained thought the enchanting table as vanilla enchantments.""",
            Object::toString,
            Boolean::parseBoolean);
        MOD.setConfigIfAbsent(
            ModConfig.DEMONIC_DAMAGE_DEFAULT_CAMERA_TILT_DUMPER, 0.5F, """
            # When using Demonic Enchantments, the player is inflicted Demonic Damage and can be distracting because
            # the camera tilts with great amplitude and frequency. This value can be used to soften that effect.
            # Making this value smaller means that the camera tilt is dumped more.""",
            Object::toString,
            Float::parseFloat);
        MOD.setConfigIfAbsent(
            ModConfig.DEMONIC_DAMAGE_DEMONCTION_CAMERA_TILT_DUMPER, 0.8F, """
            # Same concept of the previous config. This value is used to soften the camera tilt even only when the user
            # is equipped with an armor having the Demonction Enchantment and the damage is Demonic Damage.
            # Making this value smaller means that the camera tilt is dumped more.""",
            Object::toString,
            Float::parseFloat);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_ENABLED_WHILE_SNEAKING, true, """
            # The union of this config flag and the next config flag define when the vein mining enchantment is
            # enabled. Setting this flag as "false" makes the vein mining work also when the player is sneaking.""",
            Object::toString,
            Boolean::parseBoolean);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_ENABLED_WHILE_NOT_SNEAKING, true, """
            # The union of this config flag and the previous config flag define when the vein mining enchantment is
            # enabled. Setting this flag as "false" makes the vein mining work also when the player is not sneaking.""",
            Object::toString,
            Boolean::parseBoolean);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_HAS_WHITE_LIST, false,"""
            # Normally, the vein mining enchantment digs any block that suited for the enchanted tool. Setting this
            # flag as "true" makes the dig logic work only on the white list of blocks defined on the next config.""",
            Object::toString,
            Boolean::parseBoolean);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_WHITE_LIST, List.of(), """
            # This is the white list of items that is allowed for the vein mining enchantment when the white list
            # is enabled. The blocks must be written inside the square brackets, separated by commas: the blocks ids
            # can be found in game using the F3+H "Advanced Tooltips".
            # An example is the following: [minecraft:stone, minecraft:dirt, minecraft:coal_ore]""",
            ModConfigurations::listOfIdentifiersToString,
            ModConfigurations::stringToListOfIdentifiers);
        MOD.setConfigIfAbsent(
            ModConfig.VEIN_MINING_TOOLS, List.of("pickaxe", "axe", "shovel", "hoe"), """
            # This is the white list of tools that can be enchanted with the vein mining enchantment. The tools must
            # be written inside the square brackets, separated by commas.
            # An example is the following: [pickaxe, axe, shovel, hoe]""",
            ModConfigurations::listOfStringsToString,
            ModConfigurations::stringToListOfStrings);

        // Loading the configuration for the enchantments from file
        ENCHANTMENTS.load();
    }

    public static boolean registerEnchantmentConfig(String name, Enchantment.Rarity rarity, int minLevel, int maxLevel)
    {
        // Creating the keys in any case
        String enabledKey = getEnchantmentConfigKey(name, EnchantmentsConfig.ENABLED);
        String rarityKey = getEnchantmentConfigKey(name, EnchantmentsConfig.RARITY);
        String minLevelKey = getEnchantmentConfigKey(name, EnchantmentsConfig.MIN_LEVEL);
        String maxLevelKey = getEnchantmentConfigKey(name, EnchantmentsConfig.MAX_LEVEL);
        ENCHANTMENTS.createConfigGroup(enabledKey, List.of(rarityKey, minLevelKey, maxLevelKey));
        ENCHANTMENTS.setConfigIfAbsent(enabledKey, true, Object::toString, Boolean::parseBoolean);
        ENCHANTMENTS.setConfigIfAbsent(rarityKey, rarity, Object::toString, Enchantment.Rarity::valueOf);
        ENCHANTMENTS.setConfigIfAbsent(minLevelKey, minLevel, Object::toString, Integer::parseInt);
        ENCHANTMENTS.setConfigIfAbsent(maxLevelKey, maxLevel, Object::toString, Integer::parseInt);
        // Check if the enchantment is enabled
        return isEnchantmentEnabled(name);
    }

    public static void write()
    {
        MOD.writeConfigurationFile();
        ENCHANTMENTS.writeConfigurationFile();
    }

    public static boolean isDemonicEnchantingEnabled()
    {
        return (boolean) MOD.getConfigValue(ModConfig.DEMONIC_ENCHANTING_ENABLED, null);
    }

    public static float getDemonicDamageDefaultCameraDumping()
    {
        return (float) MOD.getConfigValue(ModConfig.DEMONIC_DAMAGE_DEFAULT_CAMERA_TILT_DUMPER, null);
    }

    public static float getDemonicDamageDemonctionCameraDumping()
    {
        return (float) MOD.getConfigValue(ModConfig.DEMONIC_DAMAGE_DEMONCTION_CAMERA_TILT_DUMPER, null);
    }

    public static boolean shouldEnableVeinMining(PlayerEntity player)
    {
        boolean isEnabledWhileSneaking = (boolean) MOD.getConfigValue(ModConfig.VEIN_MINING_ENABLED_WHILE_SNEAKING, null);
        boolean isEnabledWhileNotSneaking = (boolean) MOD.getConfigValue(ModConfig.VEIN_MINING_ENABLED_WHILE_NOT_SNEAKING, null);
        boolean isSneaking = player.isSneaking();
        return (isEnabledWhileSneaking && isSneaking) || (isEnabledWhileNotSneaking && !isSneaking);
    }

    public static boolean isVeinMiningEnchantmentWhiteListed()
    {
        return (boolean) MOD.getConfigValue(ModConfig.VEIN_MINING_HAS_WHITE_LIST, null);
    }

    @SuppressWarnings("unchecked")
    public static boolean isBlockVeinMiningWhiteListed(BlockState state)
    {
        if(!isVeinMiningEnchantmentWhiteListed())
            return true;

        List<Identifier> whiteList = (List<Identifier>) MOD.getConfigValue(ModConfig.VEIN_MINING_WHITE_LIST, null);
        return whiteList.contains(Registries.BLOCK.getId(state.getBlock()));
    }

    @SuppressWarnings("unchecked")
    public static boolean canToolVeinMine(Item item)
    {
        List<String> tools = (List<String>) MOD.getConfigValue(ModConfig.VEIN_MINING_TOOLS, null);
        if(item instanceof PickaxeItem)
            return tools.contains("pickaxe");
        if(item instanceof AxeItem)
            return tools.contains("axe");
        if(item instanceof ShovelItem)
            return tools.contains("shovel");
        if(item instanceof HoeItem)
            return tools.contains("hoe");
        return false;
    }

    private static String getEnchantmentConfigKey(String enchantment, String config)
    {
        return enchantment+"."+config.toLowerCase();
    }

    private static boolean isEnchantmentEnabled(String enchantment)
    {
        String enabledKey = getEnchantmentConfigKey(enchantment, EnchantmentsConfig.ENABLED);
        return (boolean) ENCHANTMENTS.getConfigValue(enabledKey, Boolean::parseBoolean);
    }

    public static int getEnchantmentMinLevel(String enchantment)
    {
        String key = getEnchantmentConfigKey(enchantment, EnchantmentsConfig.MIN_LEVEL);
        return (int) ENCHANTMENTS.getConfigValue(key, Integer::parseInt);
    }

    public static int getEnchantmentMaxLevel(String enchantment)
    {
        String key = getEnchantmentConfigKey(enchantment, EnchantmentsConfig.MAX_LEVEL);
        return (int) ENCHANTMENTS.getConfigValue(key, Integer::parseInt);
    }

    public static Enchantment.Rarity getEnchantmentRarity(String enchantment)
    {
        String key = getEnchantmentConfigKey(enchantment, EnchantmentsConfig.RARITY);
        return (Enchantment.Rarity) ENCHANTMENTS.getConfigValue(key, null);
    }

    @SuppressWarnings("unchecked")
    protected static String listOfIdentifiersToString(Object identifierList)
    {
        List<Identifier> identifiers = (List<Identifier>)identifierList;
        List<String> list = identifiers.stream().map(Identifier::toString).toList();
        return list.toString();
    }

    protected static List<Identifier> stringToListOfIdentifiers(String string)
    {
        string = string.trim();
        string = string.substring(1, string.length()-1);
        List<String> list = List.of(string.split(","));
        return list.stream().map((str) -> new Identifier(str.strip())).toList();
    }

    protected static String listOfStringsToString(Object strings)
    {
        return strings.toString();
    }

    protected static List<String> stringToListOfStrings(String string)
    {
        string = string.trim();
        string = string.substring(1, string.length()-1);
        List<String> list = List.of(string.split(","));
        return list.stream().map(String::strip).toList();
    }
}
