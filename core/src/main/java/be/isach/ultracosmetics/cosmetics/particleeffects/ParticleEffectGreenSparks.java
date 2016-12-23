package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.util.Vector;

/**
 * Created by sacha on 13/08/15.
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

    @Override
    protected void onEquip() {

    }
}
