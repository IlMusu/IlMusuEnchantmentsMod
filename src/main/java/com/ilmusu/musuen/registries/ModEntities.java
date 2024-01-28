package com.ilmusu.musuen.registries;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.client.renderers.entities.ColossusPartRenderer;
import com.ilmusu.musuen.client.renderers.entities.ColossusRenderer;
import com.ilmusu.musuen.entities.ColossusEntity;
import com.ilmusu.musuen.entities.ColossusPartEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities
{
    public static final EntityType<ColossusEntity> COLOSSUS = Registry.register(Registries.ENTITY_TYPE,
        Resources.identifier("colossus"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, ColossusEntity::new)
            .dimensions(EntityDimensions.fixed(0.1F, 0.1F)).trackRangeChunks(6).trackedUpdateRate(20).build());

    public static final EntityType<ColossusPartEntity> COLOSSUS_PART = Registry.register(Registries.ENTITY_TYPE,
        Resources.identifier("colossus_part"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, ColossusPartEntity::new)
            .dimensions(EntityDimensions.changing(0.8F, 0.8F)).build());

    public static void registerRenders()
    {
        EntityRendererRegistry.register(COLOSSUS, ColossusRenderer::new);
        EntityRendererRegistry.register(COLOSSUS_PART, ColossusPartRenderer::new);
    }
}
