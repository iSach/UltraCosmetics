package be.isach.ultracosmetics.cosmetics.emotes;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics.emotes
 * Created by: Sacha
 * Created on: 17th June, 2016
 * at 02:48
 */
public class EmoteAnimation implements Runnable {

    private int ticks, ticksPerFrame;
    private Emote emote;

    public EmoteAnimation(int ticksPerFrame, Emote emote) {
        this.ticksPerFrame = ticksPerFrame;
        this.ticks = 0;
    }

    @Override
    public void run() {
        if (ticks < ticksPerFrame) {
            ticks++;
        } else {
            ticks = 0;
            // Change Texture.
        }
    }
}
