package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class ParticleEffectFlameRings extends ParticleEffect {

    float step = 0;

    public ParticleEffectFlameRings(UUID owner) {
        super(owner,
                ParticleEffectType.FLAMERINGS
        );
        if (owner != null) {

        }
    }

    @Override
    void onUpdate() {
        for (int i = 0; i < 2; i++) {
            double inc = (2 * Math.PI) / 100;
            double toAdd = 0;
            if (i == 1)
                toAdd = 3.5;
            double angle = step * inc + toAdd;
            Vector v = new Vector();
            v.setX(Math.cos(angle));
            v.setZ(Math.sin(angle));
            if (i == 0) {
                MathUtils.rotateAroundAxisZ(v, 180);
            } else {
                MathUtils.rotateAroundAxisZ(v, 90);
            }
            UtilParticles.display(getType().getEffect(), getPlayer().getLocation().clone().add(0, 1, 0).add(v));
        }
        step += 3;
    }

}
