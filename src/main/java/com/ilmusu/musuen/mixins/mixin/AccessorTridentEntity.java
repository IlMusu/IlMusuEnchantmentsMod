package com.ilmusu.musuen.mixins.mixin;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TridentEntity.class)
public interface AccessorTridentEntity
{
    @Accessor("tridentStack")
    ItemStack getTridentStack();
}
