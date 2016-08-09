package be.isach.ultracosmetics.cosmetics.emotes;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics.emotes
 * Created by: Sacha
 * Created on: 17th June, 2016
 * at 02:48
 */
class EmoteAnimation extends BukkitRunnable {

    private static final int INTERVAL_BETWEEN_REPLAY = 20;

    private int ticks, ticksPerFrame, currentFrame, intervalTick;
    private Emote emote;
    private boolean running, up = true;

    EmoteAnimation(int ticksPerFrame, Emote emote) {
        this.ticksPerFrame = ticksPerFrame;
        this.emote = emote;
        this.ticks = 0;
        this.running = false;
    }

    @Override
    public void run() {
        if (ticks < ticksPerFrame) {
            ticks++;
        } else {
            ticks = 0;
            updateTexture();
        }
    }

    void start() {
        this.running = true;
        runTaskTimer(emote.getUcInstance(), 0, ticksPerFrame);
    }

    void stop() {
        this.running = false;
        cancel();
    }

    private void updateTexture() {
        if (!running) return;

        emote.getPlayer().getInventory().setHelmet(emote.getCosmeticType().getFrames().get(currentFrame));
        emote.setItemStack(emote.getCosmeticType().getFrames().get(currentFrame));

        if (up) {
            if (currentFrame >= emote.getCosmeticType().getMaxFrames() - 1) {
                up = false;
            } else {
                currentFrame++;
            }
        } else {
            if (currentFrame <= 0) {
                if (intervalTick >= INTERVAL_BETWEEN_REPLAY / ticksPerFrame) {
                    up = true;
                    intervalTick = 0;
                } else {
                    intervalTick++;
                }
            } else {
                currentFrame--;
            }
        }
    }
}
