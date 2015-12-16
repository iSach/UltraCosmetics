package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sacha on 29/11/15.
 */
public class GadgetChristmasTree extends Gadget {

    private boolean active = false;
    private Location lastLocation;

    int[] logColor = {101, 67, 33};

    public GadgetChristmasTree(UUID owner) {
        super(owner, GadgetType.CHRISTMAS_TREE);
    }

    @Override
    void onInteractRightClick() {
        lastLocation = lastClickedBlock.getLocation().add(0.5d, 1.05d, 0.5d);
        active = true;
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                active = false;
            }
        }, 200);
    }

    @Override
    void onUpdate() {
        if (active) {
            drawLog();
            drawLeavesAndBalls();
            drawStar();
            drawSnow();
        }
    }

    private void drawSnow() {
        lastLocation.add(0, 3, 0);
        UtilParticles.display(Particles.FIREWORKS_SPARK, 4d, 3d, 4d, lastLocation, 10);
        lastLocation.subtract(0, 3, 0);
    }

    private void drawLog() {
        Location current = lastLocation.clone();
        Location to = lastLocation.clone().add(0, 2.5, 0);
        Vector link = to.toVector().subtract(current.toVector());
        float length = (float) link.length();
        link.normalize();
        float ratio = length / 10;
        Vector vector = link.multiply(ratio);
        for (int i = 0; i < 10; i++) {
            UtilParticles.display(logColor[0], logColor[1], logColor[2], current);
            current.add(vector);
        }
        current = null;
        to = null;
    }

    private void drawLeavesAndBalls() {
        float radius = 0.7f;
        for (float f = 0.8f; f <= 2.5f; f += 0.2f) {
            if (radius >= 0) {
                float d = 13f / f;
                float g = MathUtils.random(0, d);
                int e = MathUtils.random(0, 2);
                if (e == 1) {
                    double inc = (2 * Math.PI) / d;
                    float angle = (float) (g * inc);
                    float x = MathUtils.cos(angle) * (radius+0.05f);
                    float z = MathUtils.sin(angle) * (radius+0.05f);
                    lastLocation.add(x, f, z);
                    UtilParticles.display(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255), lastLocation);
                    lastLocation.subtract(x, f, z);
                }
                for (int i = 0; i < d; i++) {
                    double inc = (2 * Math.PI) / d;
                    float angle = (float) (i * inc);
                    float x = MathUtils.cos(angle) * radius;
                    float z = MathUtils.sin(angle) * radius;
                    lastLocation.add(x, f, z);
                    UtilParticles.display(0, 100, 0, lastLocation);
                    lastLocation.subtract(x, f, z);
                }
                radius = radius - (0.7f / 8.5f);
            }
        }
    }

    private void drawStar() {
        lastLocation.add(0, 2.6, 0);
        UtilParticles.display(255, 255, 0, lastLocation);
        lastLocation.subtract(0, 2.6, 0);
    }

    @Override
    public void onClear() {
        active = false;
        lastLocation = null;
    }

    @Override
    void onInteractLeftClick() {
    }
}
