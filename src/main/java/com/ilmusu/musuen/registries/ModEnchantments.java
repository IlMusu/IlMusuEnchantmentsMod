package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.apache.commons.lang3.function.TriFunction;

public class ModEnchantments
{
    public static Enchantment LACERATION;
    public static Enchantment SKEWERING;
    public static Enchantment BERSERKER;
    public static Enchantment UNEARTHING;
    public static Enchantment OVERCHARGED;
    public static Enchantment PHASING;
    public static Enchantment GUILLOTINING;
    public static Enchantment DEMONCTION;
    public static Enchantment REACHING;
    public static Enchantment POCKETED;
    public static Enchantment VEIN_MINER;
    public static Enchantment ATTRACTION;
    public static Enchantment METEORITY;
    public static Enchantment WINGSPAN;
    public static Enchantment ALIGHTING;
    public static Enchantment TELEKINESIS;
    public static Enchantment SKY_JUMP;
    public static Enchantment LONG_JUMP;
    public static Enchantment SKYHOOK;
    public static Enchantment REFLECTION;
    public static Enchantment SHOCKWAVE;
    public static Enchantment COVERAGE;
    public static Enchantment EVERLASTING;
    public static Enchantment ZERO_GRAVITY;
    public static Enchantment DREAMLIKE;
    public static Enchantment GLUTTONY;
    public static Enchantment MULTI_ARROW;
    public static Enchantment SCYTHING;
    public static Enchantment EVOLVING;
    public static Enchantment EXPERIENCING;
    public static Enchantment CRITTING;

    public static void register()
    {
        LACERATION = registerEnchantmentIfEnabled("laceration", LacerationEnchantment::new, Enchantment.Rarity.RARE, 1, 5);
        SKEWERING = registerEnchantmentIfEnabled("skewering", SkeweringEnchantment::new, Enchantment.Rarity.RARE, 1, 5);
        BERSERKER = registerEnchantmentIfEnabled("berserker", BerserkerEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 5);
        UNEARTHING = registerEnchantmentIfEnabled("unearthing", UnearthingEnchantment::new, Enchantment.Rarity.RARE, 1, 5);
        OVERCHARGED = registerEnchantmentIfEnabled("overcharged", OverchargeEnchantment::new, Enchantment.Rarity.RARE, 1, 5);
        PHASING = registerEnchantmentIfEnabled("phasing", PhasingEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 5);
        GUILLOTINING = registerEnchantmentIfEnabled("guillotining", GuillotiningEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 3);
        LONG_JUMP = registerEnchantmentIfEnabled("long_jump", LongJumpEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 5);
        DEMONCTION = registerEnchantmentIfEnabled("demonction", DemonctionEnchantment::new, Enchantment.Rarity.RARE, 1, 4);
        REACHING = registerEnchantmentIfEnabled("reaching", ReachingEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 5);
        POCKETED = registerEnchantmentIfEnabled("pocketed", PocketedEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 5);
        VEIN_MINER = registerEnchantmentIfEnabled("vein_miner", VeinMinerEnchantment::new, Enchantment.Rarity.RARE, 1, 3);
        ATTRACTION = registerEnchantmentIfEnabled("attraction", AttractionEnchantment::new, Enchantment.Rarity.RARE, 1, 3);
        METEORITY = registerEnchantmentIfEnabled("meteority", MeteorityEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 3);
        WINGSPAN = registerEnchantmentIfEnabled("wingspan", WingspanEnchantment::new, Enchantment.Rarity.COMMON, 1, 5);
        ALIGHTING = registerEnchantmentIfEnabled("alighting", AlightingEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 1);
        TELEKINESIS = registerEnchantmentIfEnabled("telekinesis", TelekinesisEnchantment::new, Enchantment.Rarity.RARE, 1, 5);
        SKY_JUMP = registerEnchantmentIfEnabled("sky_jump", SkyJumpEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 5);
        SKYHOOK = registerEnchantmentIfEnabled("skyhook", SkyhookEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 4);
        REFLECTION = registerEnchantmentIfEnabled("reflection", ReflectionEnchantment::new, Enchantment.Rarity.COMMON, 1, 3);
        SHOCKWAVE = registerEnchantmentIfEnabled("shockwave", ShockwaveEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 3);
        COVERAGE = registerEnchantmentIfEnabled("coverage", CoverageEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 5);
        EVERLASTING = registerEnchantmentIfEnabled("everlasting", EverlastingEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 1);
        ZERO_GRAVITY = registerEnchantmentIfEnabled("zero_gravity", ZeroGravityEnchantment::new, Enchantment.Rarity.UNCOMMON, 1, 1);
        DREAMLIKE = registerEnchantmentIfEnabled("dreamlike", DreamlikeEnchantment::new, Enchantment.Rarity.RARE, 1, 3);
        GLUTTONY = registerEnchantmentIfEnabled("gluttony", GluttonyEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 3);
        MULTI_ARROW = registerEnchantmentIfEnabled("multi_arrow", MultiArrowEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 5);
        SCYTHING = registerEnchantmentIfEnabled("scything", ScythingEnchantment::new, Enchantment.Rarity.RARE, 1, 3);
        EVOLVING = registerEnchantmentIfEnabled("evolving", EvolvingEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 5);
        EXPERIENCING = registerEnchantmentIfEnabled("experiencing", ExperiencingEnchantment::new, Enchantment.Rarity.VERY_RARE, 1, 3);
        //CRITTING = registerEnchantmentIfEnabled("critting", CritterEnchantment::new, Enchantment.Rarity.RARE, 1, 5);
    }

    public static Enchantment registerEnchantmentIfEnabled(String name, TriFunction<Enchantment.Rarity, Integer, Integer, Enchantment> constructor, Enchantment.Rarity rarity, int minLevel, int maxLevel)
    {
        // Registering the configs and checking if enabled
        if(!ModConfigurations.registerEnchantmentConfig(name, rarity, minLevel, maxLevel))
            return null;
        // Registering the enchantment and forcing the registration of the levels
        rarity = ModConfigurations.getEnchantmentRarity(name);
        minLevel = ModConfigurations.getEnchantmentMinLevel(name);
        maxLevel = ModConfigurations.getEnchantmentMaxLevel(name);
        Enchantment enchantment = constructor.apply(rarity, minLevel, maxLevel);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier(name), enchantment);
        return enchantment;
    }
}