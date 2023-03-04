package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.mixins.interfaces._IPlayerTickers;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerWithTickers implements _IPlayerTickers
{
    private final List<Ticker> tickers = new ArrayList<>();

    @Override
    public void addTicker(Ticker ticker)
    {
        this.tickers.add(ticker);
        ticker.start();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void updateTickersOnPlayerTick(CallbackInfo ci)
    {
        for(int i=this.tickers.size()-1; i>=0; --i)
        {
            Ticker ticker = this.tickers.get(i);
            ticker.tick();

            if(ticker.hasFinished())
                this.tickers.remove(i);
        }
    }
}
