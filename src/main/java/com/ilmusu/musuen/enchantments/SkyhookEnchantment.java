package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.EntityRendererCallback;
import com.ilmusu.musuen.callbacks.ProjectileShotCallback;
import com.ilmusu.musuen.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.musuen.mixins.interfaces._IPlayerTickers;
import com.ilmusu.musuen.networking.messages.SkyhookLeashMessage;
import com.ilmusu.musuen.registries.ModEnchantments;
import com.ilmusu.musuen.utils.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

public class SkyhookEnchantment extends Enchantment
{
    public static final String SKYHOOK_HOLDER = Resources.MOD_ID+".skyhook_holder";
    public static final String SKYHOOK_PROJECTILE = Resources.MOD_ID+".skyhook_projectile";

    public SkyhookEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.CROSSBOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel()
    {
        return 4;
    }

    static
    {
        ProjectileShotCallback.AFTER.register((shooter, container, projectile) ->
        {
            if(!(shooter instanceof PlayerEntity player) || shooter.world.isClient)
                return;

            int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.SKYHOOK, shooter);
            int slot = ModUtils.findInInventory(player, Items.LEAD);
            if(level == 0 || (slot == -1))
                return;

            // Starting the skyhook
            NbtCompound nbt = ((_IEntityPersistentNbt)player).getPNBT();
            new SkyhookLeashMessage(projectile, player, true).sendToClientsTrackingAndSelf(player);
            nbt.putUuid(SKYHOOK_PROJECTILE, projectile.getUuid());
            // Removing the leash from the player inventory
            ItemStack leadStack = player.isCreative() ? ItemStack.EMPTY : player.getInventory().removeStack(slot, 1);

            int duration = 20*(level*2);
            ((_IPlayerTickers)player).addTicker(new _IPlayerTickers.Ticker(duration)
                .onTicking((ticker) -> {
                    // Conditions for prematurely stopping the skyhook
                    boolean isProjectileInGround = (projectile instanceof PersistentProjectileEntity p && p.inGround);
                    boolean isHoldingContainer = player.getMainHandStack() == container;
                    boolean isHooked = nbt.getUuid(SKYHOOK_PROJECTILE).equals(projectile.getUuid());
                    if(isProjectileInGround || !isHoldingContainer || !isHooked || projectile.isRemoved())
                    {
                        ticker.setFinished();
                        return;
                    }

                    // Computing the velocity for the given projectile
                    Vec3d diff = projectile.getPos().subtract(player.getPos());
                    float magnitude = ModUtils.clamp(0.0F, (float)diff.length(), 2.0F);
                    Vec3d velocity = diff.normalize().multiply(magnitude);

                    player.setVelocity(velocity);
                    player.velocityModified = true;
                })
                .onExiting((ticker -> {
                    // Actually stopping the skyhook
                    new SkyhookLeashMessage(projectile, player, false).sendToClientsTrackingAndSelf(player);
                    if(nbt.getUuid(SKYHOOK_PROJECTILE).equals(projectile.getUuid()))
                        nbt.remove(SKYHOOK_PROJECTILE);
                    // Putting the leash back into the player inventory
                    player.getInventory().insertStack(leadStack);
                })));
        });
    }

    @Environment(EnvType.CLIENT)
    public static class SkyhookEnchantmentRendering
    {
        public static void register()
        {
            EntityRendererCallback.AFTER.register(((entity, matrices, tickDelta, provider, light) ->
            {
                NbtCompound nbt = ((_IEntityPersistentNbt)entity).getPNBT();
                if(!nbt.contains(SKYHOOK_HOLDER))
                    return;

                Entity holder = entity.world.getEntityById(nbt.getInt(SKYHOOK_HOLDER));
                renderLeash(entity, holder, tickDelta, matrices, provider);
            }));
        }

        private static void renderLeash(Entity leashed, Entity holder, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider)
        {
            matrices.push();
            Vec3d holderLeasPos = holder.getLeashPos(tickDelta);
            double g = MathHelper.lerp(tickDelta, leashed.prevX, leashed.getX());
            double h = MathHelper.lerp(tickDelta, leashed.prevY, leashed.getY());
            double i = MathHelper.lerp(tickDelta, leashed.prevZ, leashed.getZ());
            float j = (float)(holderLeasPos.x - g);
            float k = (float)(holderLeasPos.y - h);
            float l = (float)(holderLeasPos.z - i);

            VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float n = MathHelper.fastInverseSqrt(j * j + l * l) * 0.025f / 2.0f;
            float o = l * n;
            float p = j * n;

            BlockPos blockPos = new BlockPos(leashed.getCameraPosVec(tickDelta));
            BlockPos blockPos2 = new BlockPos(holder.getCameraPosVec(tickDelta));
            int q = leashed.world.getLightLevel(LightType.BLOCK, blockPos);
            int r = leashed.world.getLightLevel(LightType.BLOCK, blockPos2);
            int s = leashed.world.getLightLevel(LightType.SKY, blockPos);
            int t = leashed.world.getLightLevel(LightType.SKY, blockPos2);

            for (int u = 0; u <= 24; ++u)
                renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.025f, o, p, u, false);
            for (int u = 24; u >= 0; --u)
                renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.0f, o, p, u, true);

            matrices.pop();
        }

        private static void renderLeashPiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g, float h, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight, int holdingEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot)
        {
            float m = (float)pieceIndex / 24.0f;
            int n = (int)MathHelper.lerp(m, leashedEntityBlockLight, holdingEntityBlockLight);
            int o = (int)MathHelper.lerp(m, leashedEntitySkyLight, holdingEntitySkyLight);
            int p = LightmapTextureManager.pack(n, o);
            float q = pieceIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1.0f;
            float r = 0.5f * q;
            float s = 0.4f * q;
            float t = 0.3f * q;
            float u = f * m;
            float v = g > 0.0f ? g * m * m : g - g * (1.0f - m) * (1.0f - m);
            float w = h * m;
            vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(r, s, t, 1.0f).light(p).next();
            vertexConsumer.vertex(positionMatrix, u + k, v + i - j, w - l).color(r, s, t, 1.0f).light(p).next();
        }
    }
}
