package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Location;

/**
 * Represents an instance of garden nymph halo particles summoned by a player.
 *
 * @author SinfulMentality
 * @since 04-19-20
 */
public class ParticleEffectGardenNymph extends ParticleEffect {

    private final int numParticles = 1; // Number of particles the garden halo is composed of
    private int step = 0;

    public ParticleEffectGardenNymph(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("gardennymph"));
        this.ignoreMove = false; // ignoreMove is actually stopping the animation on moving if false... change naming
    }

    @Override
    public void onUpdate() {
        Location location = getPlayer().getEyeLocation().add(0, 0.5, 0);
        float radius = 0.35f; // refresh radius value

        // Rotation logic
        if(step == 720) step = 0; // at step = 720, (step * PI / 360.0f) = 2PI, equivalent to 0
        step += 128;

        drawCircle(radius, 0, location);
    }

    private void drawCircle(float radius, float y, Location location) {
        for (int i = 0; i < numParticles; i++) {
            double inc = (2 * Math.PI) / numParticles;
            float angle = (float) ((i * inc) + (step * Math.PI / 360.0f));
            float x = MathUtils.cos(angle) * radius;
            float z = MathUtils.sin(angle) * radius;
            location.add(x, y, z);
            UtilParticles.display(Particles.VILLAGER_HAPPY, location);
            location.subtract(x, y, z);
        }
    }



}
