package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.mixins.interfaces._IEntityTrackableDrops;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityWithTrackableDrops implements _IEntityTrackableDrops
{
    private DamageSource deathDamageSource;

    @Override
    public void setDeathDamageSource(DamageSource source)
    {
        this.deathDamageSource = source;
    }

    @Override
    public DamageSource getDeathDamageSource()
    {
        return this.deathDamageSource;
    }

    @Mixin(LivingEntity.class)
    public abstract static class LivingEntityTrackableDrops
    {
        @Inject(method = "drop", at = @At("HEAD"))
        private void beforeDroppingDrops(DamageSource source, CallbackInfo ci)
        {
            ((_IEntityTrackableDrops)this).setDeathDamageSource(source);
        }

        @Inject(method = "drop", at = @At("TAIL"))
        private void afterDroppingDrops(DamageSource source, CallbackInfo ci)
        {
            ((_IEntityTrackableDrops)this).setDeathDamageSource(null);
        }
    }
}
