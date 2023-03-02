package com.ilmusu.ilmusuenchantments.enchantments;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.callbacks.PlayerFovMultiplierCallback;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IEntityPersistentNbt;
import com.ilmusu.ilmusuenchantments.mixins.interfaces._IPlayerTickers;
import com.ilmusu.ilmusuenchantments.networking.messages.PhasingSwitchMessage;
import com.ilmusu.ilmusuenchantments.registries.ModEnchantments;
import com.ilmusu.ilmusuenchantments.utils.ModUtils;
import com.ilmusu.ilmusuenchantments.utils.raycasting.ModRaycast;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class PhasingEnchantment extends Enchantment implements _IDemonicEnchantment
{
    /*
    public static List<Vec3d> poses = new ArrayList<>();

    public static void debugPositions()
    {
        poses.clear();

        PlayerEntity player = MinecraftClient.getInstance().player;
        // If there was no block on the path of the ray, simply teleport the player there
        BlockHitResult hitResult = ModRaycast.raycastFullBlock(player, 0.0F, 25);
        if(hitResult.getType() == HitResult.Type.MISS)
            return;

        Box box = player.getBoundingBox(player.getPose()).expand(0.3F, 0.0F, 0.3F);
        box = box.withMinY(box.minY+0.6F);
        float travelled = ModRaycast.computeTravelDistance(player, hitResult);
        HitResult phaseResult = raycastBox(player, box, travelled+0.5F, 25);
    }

    public static class RayStepper
    {
        protected Vec3d start;
        protected Vec3d direction;
        protected float step;
        protected float range;

        protected  Vec3d position;
        protected float current;

        public RayStepper(Vec3d start, Vec3d direction, float step, float range)
        {
            this.start = start;
            this.direction = direction.normalize();
            this.step = step;
            this.range = range;

            this.current = 0;
            this.updatePosition();
        }

        public boolean step()
        {
            // Computing the new position of the ray
            this.current += this.step;
            this.updatePosition();
            // Return true if this is complete
            return this.current < this.range;
        }

        protected void updatePosition()
        {
            this.position = this.start.add(this.direction.multiply(this.current));
        }

        public HitResult intersectsEmpty(World world, Box box)
        {
            // DEBUG IS DONE HERE
            poses.add(this.position);

            // The box must be centered at its center position!
            box = box.offset(this.position);
            // Trying to find and empty position by moving the box up
            if(!world.getBlockCollisions(null, box).iterator().hasNext())
            {
                Vec3d center = box.getCenter();
                return BlockHitResult.createMissed(center, Direction.UP, new BlockPos(center));
            }

            return null;
        }
    }

    public static HitResult raycastBox(World word, Vec3d start, Vec3d direction, float range, Box box)
    {
        RayStepper stepper = new RayStepper(start, direction, 0.1F, range);
        while(stepper.step())
        {
            HitResult result = stepper.intersectsEmpty(word, box);
            if(result != null)
                return result;
        }

        return null;
    }

    public static HitResult raycastBox(LivingEntity entity, Box box, float from, float range)
    {
        // Adjusting the start pos with the value of "from"
        Vec3d direction = entity.getRotationVector();
        Vec3d start = computeStart(entity).add(direction.multiply(from));
        // The selector for the block
        box = box.offset(0, -entity.getEyeHeight(entity.getPose()), 0);
        return raycastBox(entity.world, start, direction, range, box);
    }

    public static Vec3d computeStart(LivingEntity entity)
    {
        return entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
    }


    static
    {
        WorldRendererCallback.AFTER.register((matrices, tickDelta, provider) ->
        {
            for(Vec3d pos : poses)
            {
                matrices.push();

                ItemStack block = new ItemStack(Blocks.DIAMOND_BLOCK);

                MinecraftClient mc = MinecraftClient.getInstance();
                Camera camera = mc.gameRenderer.getCamera();
                int blockLight = mc.world.getLightLevel(LightType.BLOCK, new BlockPos(pos));
                int skyLight = mc.world.getLightLevel(LightType.SKY, new BlockPos(pos));
                int light = LightmapTextureManager.pack(blockLight, skyLight);

                // Translating to the origin
                matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

                Box box = mc.player.getBoundingBox(mc.player.getPose()).expand(0.3F, 0.0F, 0.3F);
                box = box.withMinY(box.minY+0.6F);
                box = box.offset(0, -mc.player.getEyeHeight(mc.player.getPose()), 0);
                box = box.offset(pos);

                matrices.translate(box.getCenter().x, box.getCenter().y, box.getCenter().z);

                // Scaling with the player bounding box
                matrices.scale((float)box.getXLength(), (float)box.getYLength(), (float)box.getZLength());
                // Scaling correctly the block
                matrices.scale(2.0F, 2.0F, 2.0F);
                MinecraftClient.getInstance().getItemRenderer().renderItem(block, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, provider, 0);

                matrices.pop();
            }
        });
    }
    */

    private static final String PHASING_TAG = Resources.MOD_ID+".is_phasing";
    private static final int TICK_DELAY_BETWEEN_PHASING = 10;

    public static float clientTargetFov = 1.0F;

    public PhasingEnchantment(Rarity weight)
    {
        super(weight, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public Text getName(int level)
    {
        return _IDemonicEnchantment.super.getName(this.getTranslationKey(), level, this.getMaxLevel());
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    public static boolean onPhasingKeyBindingPress(PlayerEntity player, int modifiers)
    {
        long lastPhaseTick = ((_IEntityPersistentNbt)player).get().getLong(PHASING_TAG);
        if(player.world.getTime() - lastPhaseTick <= TICK_DELAY_BETWEEN_PHASING)
            return false;

        // The phasing enchantment must be present on the legs
        int level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.PHASING, player);
        if(level == 0)
            return false;

        // The maximum distance the player can phase to
        float distance = level * 5.0F;

        // If there was no block on the path of the ray, simply teleport the player there
        BlockHitResult hitResult = ModRaycast.raycastFullBlock(player, 0.0F, distance);
        if(hitResult.getType() == HitResult.Type.MISS)
            return performPlayerPhasing(player, ModEnchantments.PHASING, hitResult, level);

        Box box = player.getBoundingBox(player.getPose()).expand(0.3F, 0.0F, 0.3F);
        box = box.withMinY(box.minY+0.6F);
        float travelled = ModRaycast.computeTravelDistance(player, hitResult);
        HitResult phaseResult = ModRaycast.raycastBox(player, box, travelled+0.5F, distance);

        // If it is not possible to phase, teleport to the looked block if the distance is not minimal
        if(phaseResult == null)
        {
            // The phasing has failed
            if(travelled <= 2.0F)
            {
                float pitch = ModUtils.range(player.world.getRandom(), 0.5F, 0.7F);
                player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_BLAZE_DEATH, SoundCategory.PLAYERS, 1.0F, pitch);
                return false;
            }

            return performPlayerPhasing(player, ModEnchantments.PHASING, offsetFromDir(hitResult), level);
        }

        // This is the actual phasing
        return performPlayerPhasing(player, ModEnchantments.PHASING, phaseResult, level);
    }

    private static boolean performPlayerPhasing(PlayerEntity player, Enchantment ench, HitResult result, int level)
    {
        Vec3d target = result.getPos();
        if(player.world.getBlockState(new BlockPos(target)).getMaterial().blocksMovement())
            target = new Vec3d(target.getX(), ((int)target.getY())+1, target.getZ());
        
        Vec3d finalTarget = target;
        int fovEffectTime = 13;
        ((_IPlayerTickers)player).addTicker(new _IPlayerTickers.Ticker(fovEffectTime)
            .onEntering((ticker) ->
            {
                // The player starts to phase, registering in the nbt
                ((_IEntityPersistentNbt)player).get().putLong(PHASING_TAG, player.world.getTime()+fovEffectTime);
                // Sending the message for updating the player fov
                new PhasingSwitchMessage(true).sendToClient((ServerPlayerEntity) player);

                Vec3d start = player.getEyePos();
                Vec3d direction = finalTarget.subtract(start).normalize();
                float distance = Math.min((float)finalTarget.subtract(start).length(), 3.0F);
                for(float i=0; i<distance; i+=ModUtils.range(player.getRandom(), 0.7F, 1.1F))
                {
                    Vec3d pos = start.add(direction.multiply(i));
                    ((ServerWorld)player.world).spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            })
            .onExiting((ticker) ->
            {
                // Teleporting player
                player.requestTeleport(finalTarget.x, finalTarget.getY(), finalTarget.getZ());
                float pitch = ModUtils.range(player.world.getRandom(), 0.8F, 1.2F);
                player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.0F, pitch);

                // Sending the message for updating the player fov
                new PhasingSwitchMessage(false).sendToClient((ServerPlayerEntity) player);

                // Consuming health
                float percentage = new ModUtils.Linear(ench.getMinLevel(), 0.2F, ench.getMaxLevel(), 0.3F).of(level);
                _IDemonicEnchantment.consumeHealthValue(player, percentage, false);
            })
        );

        return true;
    }

    private static BlockHitResult offsetFromDir(BlockHitResult result)
    {
        if(result.getSide() == Direction.UP)
        {
            Vec3d pos = new Vec3d(result.getPos().x, result.getBlockPos().getY()+1, result.getPos().z);
            return new BlockHitResult(pos, result.getSide(), new BlockPos(pos), true);
        }
        if(result.getSide() == Direction.DOWN)
        {
            Vec3d pos = new Vec3d(result.getPos().x, result.getBlockPos().getY()-2.5F, result.getPos().z);
            return new BlockHitResult(pos, result.getSide(), new BlockPos(pos), true);
        }

        Vector3f offset = result.getSide().getUnitVector();
        Vec3d pos = result.getPos().add(0, -1, 0).add(new Vec3d(offset));
        return new BlockHitResult(pos, result.getSide(), new BlockPos(pos), true);
    }

    public static void onClientPlayerPhasing(boolean enter)
    {
        PhasingEnchantment.clientTargetFov = enter ? 1.8F : 1.0F;
    }

    static
    {
        PlayerFovMultiplierCallback.AFTER.register(((player) ->
        {
            if(PhasingEnchantment.clientTargetFov == 1.0F)
                return PlayerFovMultiplierCallback.FovParams.UNCHANGED;

            return new PlayerFovMultiplierCallback.FovParams(PhasingEnchantment.clientTargetFov)
                    .unclamped().velocity(0.35F);
        }));
    }
}
