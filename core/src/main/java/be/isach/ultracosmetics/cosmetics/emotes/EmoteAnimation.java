package be.isach.ultracosmetics.cosmetics.emotes;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics.emotes
 * Created by: Sacha
 * Created on: 17th June, 2016
 * at 02:48
 */
public class EmoteAnimation extends BukkitRunnable {

    private static final int INTERVAL_BETWEEN_REPLAY = 20;

    private int ticks, ticksPerFrame, currentFrame, intervalTick;
    private Emote emote;
    private boolean up = true;

    public EmoteAnimation(int ticksPerFrame, Emote emote) {
        this.ticksPerFrame = ticksPerFrame;
        this.emote = emote;
        this.ticks = 0;
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

    public void start() {
        runTaskTimer(UltraCosmetics.getInstance(), 0, ticksPerFrame);
    }

    private void updateTexture() {
        emote.getPlayer().getInventory().setHelmet(getType().getFrames().get(currentFrame));
//        emote.getPlayer().updateInventory();

        if(up) {
            if(currentFrame >= getType().getMaxFrames() - 1) {
                up = false;
            } else {
                currentFrame++;
            }
        } else {
            if(currentFrame <= 0) {
                if(intervalTick >= INTERVAL_BETWEEN_REPLAY) {
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

    private EmoteType getType() {
        return emote.getEmoteType();
    }
}
