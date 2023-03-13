package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModDamageTypeTags
{
    public static final TagKey<DamageType> BYPASSES_DEMONCTION = ModDamageTypeTags.of("bypasses_demonction");

    private static TagKey<DamageType> of(String identifier)
    {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, Resources.identifier(identifier));
    }
}
