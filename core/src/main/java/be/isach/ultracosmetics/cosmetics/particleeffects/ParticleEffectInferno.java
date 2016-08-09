package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class ParticleEffectInferno extends ParticleEffect {

    float[] height = {0, 0, 2, 2};
    boolean[] up = {true, false, true, false};
    int[] steps = {0, 0, 0, 0};

    public ParticleEffectInferno(UUID owner) {
        super(owner, ParticleEffectType.INFERNO
        );
    }

    @Override
    void onUpdate() {
        for (int i = 0; i < 4; i++) {
            if (up[i]) {
                if (height[i] < 2)
                    height[i] += 0.05;
                else
                    up[i] = false;
            } else {
                if (height[i] > 0)
                    height[i] -= 0.05;
                else
                    up[i] = true;
            }
            double inc = (2 * Math.PI) / 100;
            double angle = steps[i] * inc + ((i + 1) % 2 == 0 ? 45 : 0);
            Vector v = new Vector();
            v.setX(Math.cos(angle) * 1.1);
            v.setZ(Math.sin(angle) * 1.1);
            try {
                UtilParticles.display(getType().getEffect(), 0.15f, 0.15f, 0.15f, getPlayer().getLocation().clone().add(v).add(0, height[i], 0), 4);
            } catch (Exception exc) {

            }
            if (i == 0 || i == 3)
                steps[i] -= 4;
            else
                steps[i] += 4;
        }
    }
}
