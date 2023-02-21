package com.ilmusu.ilmusuenchantments.registries;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

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
    public static final Enchantment MOON_JUMP = new MoonJumpEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment LONG_JUMP = new LongJumpEnchantment(Enchantment.Rarity.UNCOMMON);
    public static final Enchantment DEMONCTION = new DemonctionEnchantment(Enchantment.Rarity.VERY_RARE);

    public static void register()
    {
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("laceration"), LACERATION);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("skewering"), SKEWERING);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("unearthing"), UNEARTHING);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("phasing"), PHASING);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("overcharged"), OVERCHARGED);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("reaching"), REACHING);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("pocketed"), POCKETED);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("vein_miner"), VEIN_MINER);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("attraction"), ATTRACTION);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("meteority"), METEORITY);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("wingspan"), WINGSPAN);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("alighting"), ALIGHTING);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("telekinesis"), TELEKINESIS);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("moon_jump"), MOON_JUMP);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("long_jump"), LONG_JUMP);
        Registry.register(Registries.ENCHANTMENT, Resources.identifier("demonction"), DEMONCTION);
    }
}