package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.mixins.interfaces._IEnchantmentLevels;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Enchantment.class)
public abstract class EnchantmentWithLevels implements _IEnchantmentLevels
{
    @Unique private int minLevel;
    @Unique private int maxLevel;

    @Override
    public void setConfigurationLevels(int minLevel, int maxLevel)
    {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public int getConfigurationMinLevel()
    {
        return this.minLevel;
    }

    @Override
    public int getConfigurationMaxLevel()
    {
        return this.maxLevel;
    }
}
