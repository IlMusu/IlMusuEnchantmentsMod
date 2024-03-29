package com.ilmusu.musuen.client.renderers.entities;

import com.ilmusu.musuen.entities.ColossusEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ColossusRenderer extends EntityRenderer<ColossusEntity>
{
    public ColossusRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public void render(ColossusEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers, int light)
    {
        ItemStack stack = entity.getColossusStack();
        float age = entity.age + tickDelta;

        if(age < ColossusEntity.DAMAGE_AFTER_TICKS)
            return;

        float scale = 2.3F+(entity.getLevel()-1)*1.2F;

        matrices.push();

        Vec3d tr = new Vec3d(entity.getFacing().getUnitVector()).multiply(0.3);
        matrices.scale(scale, scale, scale);
        matrices.translate(tr.x, tr.y, tr.z);

        // Adding a random rotation when up or down
        if(entity.getFacing() == Direction.UP || entity.getFacing() == Direction.DOWN)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(entity.getYaw()));
        // Rotating to face direction
        if(entity.getFacing() == Direction.UP)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));
        else if(entity.getFacing() == Direction.DOWN)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-225));
        else if(entity.getFacing() == Direction.NORTH)
        {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));
        }
        else if(entity.getFacing() == Direction.EAST)
        {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-135));
        }
        else if(entity.getFacing() == Direction.SOUTH)
        {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(+135));
        }
        else if(entity.getFacing() == Direction.WEST)
        {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(+45));
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, light,
                OverlayTexture.DEFAULT_UV, matrices, consumers, null, 0);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(ColossusEntity entity)
    {
        return null;
    }
}
