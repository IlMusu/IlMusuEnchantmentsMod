package com.ilmusu.musuen.mixins.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PersistentProjectileEntity.class)
public interface AccessorPersistentProjectileEntity
{
    @Accessor("inGround")
    boolean inGround();
}
