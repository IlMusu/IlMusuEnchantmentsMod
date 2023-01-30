package com.ilmusu.ilmusuenchantments.mixins.interfaces;

public interface _IPlayerTickers
{
    void addTicker(Ticker ticker);

    class Ticker
    {
        protected final int duration;
        protected int remainingTime;

        protected Runnable onEnter = () -> {};
        protected Runnable onTick  = () -> {};
        protected Runnable onExit = () -> {};

        public Ticker(int duration)
        {
            this.duration = duration;
            this.remainingTime = duration;
        }

        public Ticker onEntering(Runnable action)
        {
            this.onEnter = action;
            return this;
        }

        public Ticker onTicking(Runnable action)
        {
            this.onTick = action;
            return this;
        }

        public Ticker onExiting(Runnable action)
        {
            this.onExit = action;
            return this;
        }

        public void start()
        {
            // The ticker started, execute the runnable
            this.onEnter.run();
        }

        public void tick()
        {
            // Decreasing the remaining time
            if(this.hasFinished())
                return;
            --this.remainingTime;

            // The ticker is ticking, execute the runnable
            this.onTick.run();

            // The ticker finished executing, execute the runnable
            if(this.remainingTime == 0)
                this.onExit.run();
        }

        public boolean hasFinished()
        {
            return this.remainingTime <= 0;
        }
    }
}
