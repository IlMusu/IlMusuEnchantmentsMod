package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

public class ModEnchantments
{
    public static final Enchantment LACERATION = new LacerationEnchantment(Enchantment.Rarity.RARE, 0);
    public static final Enchantment SKEWERING = new SkeweringEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment UNEARTHING = new UnearthingEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment PHASING = new PhasingEnchantment(Enchantment.Rarity.VERY_RARE);
    public static final Enchantment OVERCHARGED = new OverchargeEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment REACHING = new ReachingEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment POCKETED = new PocketedEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment VEIN_MINER = new VeinMinerEnchantment(Enchantment.Rarity.RARE);
    public static final Enchantment ATTRACTION = new AttractionEnchantment(Enchantment.Rarity.COMMON);
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

    public static void register()
    {
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("laceration"), LACERATION);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("skewering"), SKEWERING);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("unearthing"), UNEARTHING);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("phasing"), PHASING);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("overcharged"), OVERCHARGED);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("reaching"), REACHING);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("pocketed"), POCKETED);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("vein_miner"), VEIN_MINER);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("attraction"), ATTRACTION);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("meteority"), METEORITY);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("wingspan"), WINGSPAN);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("alighting"), ALIGHTING);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("telekinesis"), TELEKINESIS);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("sky_jump"), SKY_JUMP);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("long_jump"), LONG_JUMP);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("demonction"), DEMONCTION);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("skyhook"), SKYHOOK);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("reflection"), REFLECTION);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("shockwave"), SHOCKWAVE);
        Registry.register(Registry.ENCHANTMENT, Resources.identifier("coverage"), COVERAGE);
    }
}