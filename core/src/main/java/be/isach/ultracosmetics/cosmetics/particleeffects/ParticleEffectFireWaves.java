package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.util.Vector;

/**
 * Represents an instance of green spark particles summoned by a player.
 *
 * @author iSach
 * @since 08-13-2015
 */
public class ParticleEffectFireWaves extends ParticleEffect {

    private final double RADIUS = 1.1; // radius between player and rods
    private final int U_PER_WAVE = 4; // Amount of "U's" per wave.
    private final double MAX_HEIGHT_DIFF = 0.5; // Max height diff between columns
    private final double HEIGHT_DIFF_STEP = 0.05; // Height diff step...

    private boolean heightFactorDir; // Indicates whether the height diff between columns is going up or down (gives dynamism)
    private double heightFactor = MAX_HEIGHT_DIFF; // Height diff between columns. Variates over time with hoveringDirectionUp.

    public ParticleEffectFireWaves(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("firewaves"));
    }

    @Override
    public void onUpdate() {
        if (heightFactorDir) {
            if (heightFactor < MAX_HEIGHT_DIFF) heightFactor += HEIGHT_DIFF_STEP;
            else heightFactorDir = false;
        } else {
            if (heightFactor > -MAX_HEIGHT_DIFF) heightFactor -= HEIGHT_DIFF_STEP;
            else heightFactorDir = true;
        }

        drawWave();
    }

    /**
     * Draws a wave
     */
    private void drawWave() {
        Vector v = new Vector(0, 0, 0);

        for (double angle = 0; angle <= 2 * Math.PI; angle += (2 * Math.PI / 45) / getType().getParticleMultiplier()) {
            v.setX(Math.cos(angle) * RADIUS);
            v.setZ(Math.sin(angle) * RADIUS);
            v.setY(0.5 + Math.sin(angle * U_PER_WAVE) * heightFactor);

            UtilParticles.display(getType().getEffect(), getPlayer().getLocation().add(v));
            UtilParticles.display(getType().getEffect(), getPlayer().getLocation().add(v).add(0, 1, 0));
        }
    }
}
