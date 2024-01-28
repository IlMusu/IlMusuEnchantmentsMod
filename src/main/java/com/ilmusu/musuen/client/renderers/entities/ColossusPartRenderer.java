package com.ilmusu.musuen.client.renderers.entities;

import com.ilmusu.musuen.entities.ColossusPartEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class ColossusPartRenderer extends EntityRenderer<ColossusPartEntity>
{
    public ColossusPartRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public Identifier getTexture(ColossusPartEntity entity)
    {
        return null;
    }
}
