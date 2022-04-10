package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Represents an instance of frost lord particles summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class ParticleEffectFrostLord extends ParticleEffect {

    private int step = 0;
    private float stepY = 0;
    private float radius = 1.5f;

    public ParticleEffectFrostLord(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("frostlord"));
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < 6; i++) {
            Location location = getPlayer().getLocation();
            double inc = (2 * Math.PI) / 100;
            double angle = step * inc + stepY + i;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * radius);
            v.setZ(Math.sin(angle) * radius);
            getType().getEffect().display(location.add(v).add(0, stepY, 0));
            location.subtract(v).subtract(0, stepY, 0);
            if (stepY < 3) {
                radius -= 0.022f;
                stepY += 0.045f;
            } else {
                stepY = 0;
                step = 0;
                radius = 1.5f;
                SoundUtil.playSound(getPlayer(), Sounds.DIG_SNOW, .5f, 1.5f);
                getType().getEffect().display(location.clone().add(0, 3, 0), getModifiedAmount(48), 0.3f);
            }
        }
    }
}
