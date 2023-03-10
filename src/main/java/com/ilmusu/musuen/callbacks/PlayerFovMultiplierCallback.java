package com.ilmusu.musuen.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerFovMultiplierCallback
{
    Event<PlayerFovMultiplierCallback> AFTER = EventFactory.createArrayBacked(PlayerFovMultiplierCallback.class,
            (listeners) -> (player) ->
            {
                for (PlayerFovMultiplierCallback listener : listeners)
                {
                    FovParams params = listener.handler(player);
                    if(!params.shouldNotChange())
                        return params;
                }

                return FovParams.UNCHANGED;
            });

    FovParams handler(PlayerEntity player);

    class FovParams
    {
        public static final FovParams UNCHANGED = new FovParams();

        // If the FOV should not be changed
        protected final boolean unchanged;
        // The multiplier for the current FOV
        protected float additionalMultiplier = 1.0F;
        protected boolean unclamped = false;
        // The update speed for moving toward the target FOV
        protected float updateVelocity = 0.5F;

        protected FovParams()
        {
            this.unchanged = true;
        }

        public FovParams(float additionalMultiplier)
        {
            this.unchanged = false;
            this.additionalMultiplier = additionalMultiplier;
        }

        public FovParams unclamped()
        {
            this.unclamped = !this.unchanged;
            return this;
        }

        public FovParams velocity(float velocity)
        {
            if(!this.unchanged)
                this.updateVelocity = Math.max(0, velocity);
            return this;
        }

        public boolean shouldNotChange()
        {
            return this.unchanged;
        }

        public boolean isUnclamped()
        {
            return this.unclamped;
        }

        public float getAdditionalMultiplier()
        {
            return this.additionalMultiplier;
        }

        public float getUpdateVelocity()
        {
            return updateVelocity;
        }
    }
}
