package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.callbacks.PlayerReachDistanceCallback;
import com.ilmusu.musuen.mixins.interfaces._IPlayerExtendedReach;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerWithExtendedReach implements _IPlayerExtendedReach
{
    private double currentReach = -1.0F;
    private double lastUpdateTime = -1.0F;

    @Override
    public double getUpdatedReach(double vanilla)
    {
        PlayerEntity player = (PlayerEntity)(Object)this;

        // Return the already updated reach
        if(this.lastUpdateTime >= player.world.getTime())
            return this.currentReach;

        // A reach == -1 means that the reach should not be overridden
        this.lastUpdateTime = player.world.getTime();
        this.currentReach = PlayerReachDistanceCallback.BEFORE.invoker().handler(player, vanilla);
        return this.currentReach;
    }

    @Mixin(ClientPlayerInteractionManager.class)
    public abstract static class ModifyClientReachDistance
    {
        @Shadow private GameMode gameMode;

        @SuppressWarnings("DataFlowIssue")
        @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
        public void modifyPlayerReach(CallbackInfoReturnable<Float> cir)
        {
            double vanilla = this.gameMode.isCreative() ? 5.0F : 4.5F;
            double reach = ((_IPlayerExtendedReach)MinecraftClient.getInstance().player).getUpdatedReach(vanilla);
            if(reach >= 0.0)
                cir.setReturnValue((float)reach);
        }
    }

    @Mixin(GameRenderer.class)
    public abstract static class FixClientReachDistance
    {
        @Shadow @Final MinecraftClient client;

        @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
        public double increaseMaximumPossibleEntityInteraction(double constant)
        {
            return 150.0F;
        }

        @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 6.0))
        public double increaseCreativeRangeOnlyIfTheCurrentRangeIsLess(double range)
        {
            return Math.max(range, this.client.interactionManager.getReachDistance());
        }
    }

    @Mixin(ServerPlayerInteractionManager.class)
    public abstract static class ModifyServerBlockBreakingDistance
    {
        @Shadow @Final protected ServerPlayerEntity player;

        @Redirect(method = "processBlockBreakingAction", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
        public double modifyBlockBreakingReach()
        {
            double vanilla = ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE;
            double reach = ((_IPlayerExtendedReach)this.player).getUpdatedReach(vanilla);
            if(reach >= 0.0)
                return MathHelper.square(reach+1);
            return vanilla;
        }
    }

    @Mixin(ServerPlayNetworkHandler.class)
    public abstract static class ModifyServerInteractionDistance
    {
        @Shadow public ServerPlayerEntity player;

        @Redirect(method = "onPlayerInteractBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
        public double modifyBlockInteractReach()
        {
            double vanilla = ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE;
            double reach = ((_IPlayerExtendedReach)this.player).getUpdatedReach(vanilla);
            if(reach >= 0.0)
                return MathHelper.square(reach+1);
            return vanilla;
        }

        @Redirect(method = "onPlayerInteractEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
        public double modifyEntityInteractReach()
        {
            double vanilla = ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE;
            double reach = ((_IPlayerExtendedReach)this.player).getUpdatedReach(vanilla);
            if(reach >= 0.0)
                return MathHelper.square(reach+1);
            return vanilla;
        }
    }
}
