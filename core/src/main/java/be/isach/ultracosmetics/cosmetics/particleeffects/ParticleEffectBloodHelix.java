package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Represents an instance of blood helix particles summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-12-2015
 */
public class ParticleEffectBloodHelix extends ParticleEffect {

    double i = 0;

    public ParticleEffectBloodHelix(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.BLOODHELIX);
    }

    @Override
    public void onUpdate() {
        Location location = getPlayer().getLocation();
        Location location2 = location.clone();
        double radius = 1.1d;
        double radius2 = 1.1d;
        double particles = 100;

        for (int step = 0; step < 100; step += 4) {
            double interval = (2 * Math.PI) / particles;
            double angle = step * interval + i;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * radius);
            v.setZ(Math.sin(angle) * radius);
            UtilParticles.display(Particles.REDSTONE, location.add(v));
            location.subtract(v);
            location.add(0, 0.12d, 0);
            radius -= 0.044f;
        }
        for (int step = 0; step < 100; step += 4) {
            double interval = (2 * Math.PI) / particles;
            double angle = step * interval + i + 3.5;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * radius2);
            v.setZ(Math.sin(angle) * radius2);
            UtilParticles.display(Particles.REDSTONE, location2.add(v));
            location2.subtract(v);
            location2.add(0, 0.12d, 0);
            radius2 -= 0.044f;
        }
        i += 0.05;
    }
}
