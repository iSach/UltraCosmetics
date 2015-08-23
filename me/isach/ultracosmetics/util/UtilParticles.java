package me.isach.ultracosmetics.util;

import me.isach.ultracosmetics.Core;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by sacha on 07/08/15.
 */
public class UtilParticles {

    public static void drawParticleLine(Location from, Location to, Effect effect, int particles, int r, int g, int b) {
        Location location = from.clone();
        Location target = to.clone();
        double amount = particles;
        Vector link = target.toVector().subtract(location.toVector());
        float length = (float) link.length();
        link.normalize();

        float ratio = length / particles;
        Vector v = link.multiply(ratio);
        Location loc = location.clone().subtract(v);
        int step = 0;
        for (int i = 0; i < particles; i++) {
            if (step >= amount)
                step = 0;
            step++;
            loc.add(v);
            if (effect == Effect.COLOURED_DUST) {
                float finalR = r / 255;
                float finalG = g / 255;
                float finalB = b / 255;
                play(loc, Effect.COLOURED_DUST, 0, 0, finalR, finalG, finalB, 1f, 0);
            } else {
                play(loc, effect);
            }
        }
    }

    public static void playHelix(final Location loc, final float i, final Effect effect) {
        BukkitRunnable runnable = new BukkitRunnable() {
            double radius = 0;
            double step;
            double y = loc.getY();
            Location location = loc.clone().add(0, 3, 0);
            @Override
            public void run() {
                double inc = (2 * Math.PI) / 50;
                double angle = step * inc + i;
                Vector v = new Vector();
                v.setX(Math.cos(angle) * radius);
                v.setZ(Math.sin(angle) * radius);
                if(effect == Effect.COLOURED_DUST) {
                    play(location.add(v), Effect.COLOURED_DUST, 0, 0, -1, -1, 1f, 1, 0);
                } else {
                    play(location.add(v), effect, 0f);
                }
                location.subtract(v);
                location.subtract(0, 0.1d, 0);
                if(location.getY() <= y) {
                    cancel();
                }
                step += 4;
                radius += 1/30f;
            }
        };
        runnable.runTaskTimer(Core.getPlugin(), 0, 1);
    }


    public static void play(Location location, Effect effect) {
        play(location, effect, 0, 0, 0, 0, 0, 0, 1);
    }

    public static void play(Location location, Effect effect, int data) {
        play(location, effect, data, data, 0, 0, 0, 0, 1);
    }

    public static void play(Location location, Effect effect, float offsetX, float offsetY, float offsetZ) {
        play(location, effect, 0, 0, offsetX, offsetY, offsetZ, 0, 1);
    }

    public static void play(Location location, Effect effect, float speed) {
        play(location, effect, 0, 0, 0, 0, 0, speed, 1);
    }

    public static void play(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        location.getWorld().spigot().playEffect(location, effect, id, data, offsetX, offsetY, offsetZ, speed, amount, 128);
    }


}
