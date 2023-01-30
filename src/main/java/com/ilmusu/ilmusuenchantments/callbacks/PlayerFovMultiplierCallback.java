package com.ilmusu.ilmusuenchantments.callbacks;

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
        protected float multiplier = 1.0F;
        protected boolean unclamped = false;
        // The update speed for moving toward the target FOV
        protected float updateVelocity = -1.0F;

        protected FovParams()
        {
            this.unchanged = true;
        }

        public FovParams(float multiplier)
        {
            this.unchanged = false;
            this.multiplier = multiplier;
        }

        public FovParams unclamped()
        {
            this.unclamped = !this.unchanged;
            return this;
        }

        public FovParams velocity(float velocity)
        {
            if(!this.unchanged)
                this.updateVelocity = velocity;
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

        public float getMultiplier()
        {
            return this.multiplier;
        }

        public float getUpdateVelocityOr(float velocity)
        {
            if( this.updateVelocity < 0)
                return velocity;
            return this.updateVelocity;
        }
    }
}
