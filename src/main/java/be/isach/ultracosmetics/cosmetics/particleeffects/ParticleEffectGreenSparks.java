package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 13/08/15.
 */
public class ParticleEffectGreenSparks extends ParticleEffect {

    boolean up;
    float height;
    int step;

    public ParticleEffectGreenSparks(UUID owner) {
        super(Effect.HAPPY_VILLAGER, Material.EMERALD, (byte) 0x0, "GreenSparks", "ultracosmetics.particleeffects.greensparks", owner, ParticleEffectType.GREENSPARKS, 1,
                "&7&oLittle green sparkly sparks!");
    }

    @Override
    void onUpdate() {
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
        UtilParticles.play(getPlayer().getLocation().clone().add(v).add(0, height, 0), Effect.HAPPY_VILLAGER);
        step += 4;
    }
}
