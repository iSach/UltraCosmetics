package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Represents an instance of green spark particles summoned by a player.
 *
 * @author iSach
 * @since 08-13-2015
 */
public class ParticleEffectMagicalRods extends ParticleEffect {

    private final double RADIUS = 1.1;
    private final double ROD_HEIGHT = 1;
    boolean up;
    float height;
    int step;


    public ParticleEffectMagicalRods(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("magicalrods"));
    }

    @Override
    public void onUpdate() {
        Bukkit.broadcastMessage(up + "");
        if (up) {
            if (height < 1)
                height += 0.05;
            else
                up = false;
        } else {
            if (height > -1)
                height -= 0.05;
            else
                up = true;
        }
        double inc = (2 * Math.PI) / 100;
        double angle = step * inc;
        Vector v = new Vector();
        // Draw color 1
        v.setX(Math.cos(angle) * RADIUS);
        v.setZ(Math.sin(angle) * RADIUS);
        Location loc = getPlayer().getLocation().clone().add(v).add(0, height, 0);
        Location to = loc.clone().add(0, ROD_HEIGHT, 0);
        // UtilParticles.display(Particles.REDSTONE, 255, 255, 255, loc);
        //UtilParticles.display(Particles.REDSTONE, 0, 0, 0, to);
        UtilParticles.drawParticleLine(loc, to, Particles.REDSTONE, ((int) ROD_HEIGHT) * 10, 255, 0, 0);

        angle += 2 * Math.PI / 3;

        // Draw color 2
        v.setX(Math.cos(angle) * RADIUS);
        v.setZ(Math.sin(angle) * RADIUS);
        loc = getPlayer().getLocation().clone().add(v).add(0, height + 0.3, 0);
        to = loc.clone().add(0, ROD_HEIGHT, 0);
        UtilParticles.drawParticleLine(loc, to, Particles.REDSTONE, ((int) ROD_HEIGHT) * 10, 0, 255, 0);

        angle += 2 * Math.PI / 3;

        // Draw color 3
        v.setX(Math.cos(angle + Math.PI) * RADIUS);
        v.setZ(Math.sin(angle + Math.PI) * RADIUS);
        loc = getPlayer().getLocation().clone().add(v).add(0, height + 0.6, 0);
        to = loc.clone().add(0, ROD_HEIGHT, 0);
        UtilParticles.drawParticleLine(loc, to, Particles.REDSTONE, ((int) ROD_HEIGHT) * 10, 0, 0, 255);

        step += 4;
    }

    private void drawColumn() {

    }
}
