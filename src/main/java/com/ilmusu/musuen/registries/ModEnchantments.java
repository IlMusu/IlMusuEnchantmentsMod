package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ModEnchantments
{
    private static final Map<Enchantment, Integer> MIN_LEVELS = new HashMap<>();
    private static final Map<Enchantment, Integer> MAX_LEVELS = new HashMap<>();

    public static final Enchantment LACERATION = new LacerationEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment SKEWERING = new SkeweringEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment UNEARTHING = new UnearthingEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment PHASING = new PhasingEnchantment(Enchantment.Rarity.VERY_RARE);
    public static final Enchantment OVERCHARGED = new OverchargeEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment BERSERKER = new BerserkerEnchantment(Enchantment.Rarity.VERY_RARE);
    public static final Enchantment REACHING = new ReachingEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment POCKETED = new PocketedEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment VEIN_MINER = new VeinMinerEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment ATTRACTION = new AttractionEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment METEORITY = new MeteorityEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment WINGSPAN = new WingspanEnchantment(Enchantment.Rarity.COMMON);
    public static final Enchantment ALIGHTING = new AlightingEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment TELEKINESIS = new TelekinesisEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment SKY_JUMP = new SkyJumpEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment LONG_JUMP = new LongJumpEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment DEMONCTION = new DemonctionEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment SKYHOOK = new SkyhookEnchantment(Enchantment.Rarity.VERY_RARE);
    public static final Enchantment REFLECTION = new ReflectionEnchantment(Enchantment.Rarity.COMMON);
    public static final Enchantment SHOCKWAVE = new ShockwaveEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment COVERAGE = new CoverageEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment EVERLASTING = new EverlastingEnchantment(Enchantment.Rarity.UNCOMMON);

    public static void register()
    {
        registerEnchantmentIfEnabled("laceration", LACERATION);
        registerEnchantmentIfEnabled("skewering", SKEWERING);
        registerEnchantmentIfEnabled("unearthing", UNEARTHING);
        registerEnchantmentIfEnabled("phasing", PHASING);
        registerEnchantmentIfEnabled("overcharged", OVERCHARGED);
        registerEnchantmentIfEnabled("berserker", BERSERKER);
        registerEnchantmentIfEnabled("reaching", REACHING);
        registerEnchantmentIfEnabled("pocketed", POCKETED);
        registerEnchantmentIfEnabled("vein_miner", VEIN_MINER);
        registerEnchantmentIfEnabled("attraction", ATTRACTION);
        registerEnchantmentIfEnabled("meteority", METEORITY);
        registerEnchantmentIfEnabled("wingspan", WINGSPAN);
        registerEnchantmentIfEnabled("alighting", ALIGHTING);
        registerEnchantmentIfEnabled("telekinesis", TELEKINESIS);
        registerEnchantmentIfEnabled("sky_jump", SKY_JUMP);
        registerEnchantmentIfEnabled("long_jump", LONG_JUMP);
        registerEnchantmentIfEnabled("demonction", DEMONCTION);
        registerEnchantmentIfEnabled("skyhook", SKYHOOK);
        registerEnchantmentIfEnabled("reflection", REFLECTION);
        registerEnchantmentIfEnabled("shockwave", SHOCKWAVE);
        registerEnchantmentIfEnabled("coverage", COVERAGE);
        registerEnchantmentIfEnabled("everlasting", EVERLASTING);
    }

    public static void registerEnchantmentIfEnabled(String name, Enchantment enchantment)
    {
        String enabledKey = getKey(name, ModConfigurations.ENCHANTMENTS_CONFIGS.ENABLED);
        if(Boolean.parseBoolean(ModConfigurations.ENCHANTMENTS.getOrSet(enabledKey, true)))
        {
            Registry.register(Registries.ENCHANTMENT, Resources.identifier(name), enchantment);
            enchantment.getMinLevel();
            enchantment.getMaxLevel();
        }
    }

    public static int getMinLevel(Enchantment enchantment, int min)
    {
        if(MIN_LEVELS.containsKey(enchantment))
            return MIN_LEVELS.get(enchantment);

        String name = Registries.ENCHANTMENT.getId(enchantment).getPath();
        String key = getKey(name, ModConfigurations.ENCHANTMENTS_CONFIGS.MIN_LEVEL);
        String level = ModConfigurations.ENCHANTMENTS.getOrSet(key, min);

        try {
            min = Math.max(1, Integer.parseInt(level));
        }
        catch (NumberFormatException exception) {
            Resources.LOGGER.error("Could not read minimum level for "+name+" enchantment! Defaulting to "+min+"!");
        }

        MIN_LEVELS.put(enchantment, min);
        ModConfigurations.ENCHANTMENTS.set(key, min);
        return min;
    }

    public static int getMaxLevel(Enchantment enchantment, int max)
    {
        if(MAX_LEVELS.containsKey(enchantment))
            return MAX_LEVELS.get(enchantment);

        String name = Registries.ENCHANTMENT.getId(enchantment).getPath();
        String key = getKey(name, ModConfigurations.ENCHANTMENTS_CONFIGS.MAX_LEVEL);
        String level = ModConfigurations.ENCHANTMENTS.getOrSet(key, max);

        try {
            max = Math.min(Integer.parseInt(level), 255);
        }
        catch (NumberFormatException exception) {
            Resources.LOGGER.error("Could not read maximum level for "+name+" enchantment! Defaulting to "+max+"!");
        }

        MAX_LEVELS.put(enchantment, max);
        ModConfigurations.ENCHANTMENTS.set(key, max);
        return max;
    }

    private static String getKey(String name, ModConfigurations.ENCHANTMENTS_CONFIGS config)
    {
        return name+"."+config.name().toLowerCase();
    }
}