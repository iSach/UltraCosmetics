package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.util.Vector;

/**
 * Represents an instance of green spark particles summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-13-2015
 */
public class ParticleEffectGreenSparks extends ParticleEffect {

    boolean up;
    float height;
    int step;

    public ParticleEffectGreenSparks(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.GREENSPARKS);
    }

    @Override
    public void onUpdate() {
        if (up) {
            if (height < 2)
                height += 0.05;
            else
                up = false;
        } else {
            if (height > 0)
                height -= 0.05;
            else
                up = true;
        }
        double inc = (2 * Math.PI) / 100;
        double angle = step * inc;
        Vector v = new Vector();
        v.setX(Math.cos(angle) * 1.1);
        v.setZ(Math.sin(angle) * 1.1);
        UtilParticles.display(getType().getEffect(), getPlayer().getLocation().clone().add(v).add(0, height, 0));
        step += 4;
    }
}
