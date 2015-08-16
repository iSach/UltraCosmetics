package me.isach.ultracosmetics.util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

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
            if(effect == Effect.COLOURED_DUST) {
                float finalR = r / 255;
                float finalG = g / 255;
                float finalB = b / 255;
                play(loc, Effect.COLOURED_DUST, 0, 0, finalR, finalG, finalB, 1f, 0);
            } else {
                play(loc, effect);
            }
        }
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
