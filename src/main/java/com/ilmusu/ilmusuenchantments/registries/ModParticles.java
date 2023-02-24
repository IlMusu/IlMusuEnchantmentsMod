package com.ilmusu.ilmusuenchantments.registries;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.client.particles.colored.ColoredParticle;
import com.ilmusu.ilmusuenchantments.client.particles.colored.ColoredParticleType;
import com.ilmusu.ilmusuenchantments.client.particles.eblock.BlockParticle;
import com.ilmusu.ilmusuenchantments.client.particles.eblock.BlockParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModParticles
{
    public static final ColoredParticleType COLORED = new ColoredParticleType();
    public static final BlockParticleType BLOCK = new BlockParticleType();

    public static void register()
    {
        Registry.register(Registries.PARTICLE_TYPE, Resources.identifier("colored"), COLORED);
        Registry.register(Registries.PARTICLE_TYPE, Resources.identifier("block"), BLOCK);
    }

    @Environment(EnvType.CLIENT)
    public static void registerFactories()
    {
        ParticleFactoryRegistry.getInstance().register(COLORED, ColoredParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(BLOCK, BlockParticle.Factory::new);
    }
}
