package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class ParticleEffectBloodHelix extends ParticleEffect {

    double i = 0;

    public ParticleEffectBloodHelix(UUID owner) {
        super(Effect.COLOURED_DUST, Material.REDSTONE, (byte) 0x0, "BloodHelix", "ultracosmetics.particleeffects.bloodhelix", owner, ParticleEffectType.BLOODHELIX, 1);
        repeatDelay = 20;
    }

    @Override
    void onUpdate() {
        Location location = getPlayer().getLocation();
        Location location2 = location.clone();
        double radius = 1.1d;
        double radius2 = 1.1d;
        double particles = 100;

        for (int step = 0; step < 100; step += 4) {
            double inc = (2 * Math.PI) / particles;
            double angle = step * inc + i;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * radius);
            v.setZ(Math.sin(angle) * radius);
            UtilParticles.play(location.add(v), Effect.COLOURED_DUST, 0f);
            location.subtract(v);
            location.add(0, 0.12d, 0);
            radius -= 0.044f;
        }
        for (int step = 0; step < 100; step += 4) {
            double inc = (2 * Math.PI) / particles;
            double angle = step * inc + i + 3.5;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * radius2);
            v.setZ(Math.sin(angle) * radius2);
            UtilParticles.play(location2.add(v), Effect.COLOURED_DUST, 0f);
            location2.subtract(v);
            location2.add(0, 0.12d, 0);
            radius2 -= 0.044f;
        }
        i += 0.05;
    }

}
