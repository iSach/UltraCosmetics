package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Location;

/**
 * Represents an instance of a royal crown summoned by a player.
 *
 * @author SinfulMentality
 * @since 04-19-20
 */
public class ParticleEffectRoyalCrown extends ParticleEffect {

    private final int numCrownSpokes = 5; // Number of spokes the crown has
    private final int numParticlesBase = 10; // Number of particles the base of the crown is composed of
    private final int numParticlesInnerPadding = 5; // Number of particles the red inner padding is made of
    private int step = 0;

    public ParticleEffectRoyalCrown(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("royalcrown"));
        this.ignoreMove = true; // ignoreMove is actually stopping the animation on moving if false... change naming
    }

    @Override
    public void onUpdate() {
        Location location = getPlayer().getEyeLocation().add(0, 0.8, 0);
        float radius = 0.35f; // refresh radius value

        // Rotation logic
        if(step == 720) step = 0; // at step = 720, (step * PI / 360.0f) = 2PI, equivalent to 0
        step += 4;

        drawRedCircle((radius-0.05f)/2, 0.2f, location);
        drawGoldTop(0f, 0.5f, location);
        drawGoldCircle(radius, 0, location);
        drawCrownSpokes(radius, 0, location);
        drawCrownPadding(radius, 0, location);
    }

    // Draw each spoke of the crown, builds the spokes layer by layer
    private void drawCrownSpokes(float radius, float y, Location location) {
        drawCrownJewels(radius, y, location); // lowest point of spoke has a jewel
        radius += 0.09f;
        y += 0.2f;
        drawCrownSpokesLayer(radius, y, location);
        y += 0.2f;
        drawCrownSpokesLayer(radius, y, location);
        radius -= 0.18f;
        y += 0.05f;
        drawCrownSpokesLayer(radius, y, location);
        radius -= 0.18f;
        y -= 0.05f;
        drawCrownSpokesLayer(radius, y, location);
        y -= 0.4f;
    }

    // Draws a single layer of all the crown spokes
    private void drawCrownSpokesLayer(float radius, float y, Location location) {
        for (int i = 0; i < numCrownSpokes; i++) {
            double inc = (2 * Math.PI) / numCrownSpokes;
            float angle = (float) ((i * inc) + (step * Math.PI / 360.0f));
            float x = MathUtils.cos(angle) * radius;
            float z = MathUtils.sin(angle) * radius;
            location.add(x, y, z);
            UtilParticles.display(255, 215, 0, location);
            location.subtract(x, y, z);
        }
    }

    // Draw the red padding of the crown, layer by layer
    private void drawCrownPadding(float radius, float y, Location location) {
        radius += 0.09f;
        y += 0.2f;
        drawCrownPads(radius, y, location);
        y += 0.2f;
        drawCrownPads(radius, y, location);
        y -= 0.4f;
    }

    private void drawCrownPads(float radius, float y, Location location) {
        for (int i = 0; i < numCrownSpokes; i++) {
            double inc = (2 * Math.PI) / numCrownSpokes;
            float angle = (float) (((i * inc) + (step * Math.PI / 360.0f)) + (Math.PI) / numCrownSpokes);
            float x = MathUtils.cos(angle) * radius;
            float z = MathUtils.sin(angle) * radius;
            location.add(x, y, z);
            UtilParticles.display(255, 0, 0, location);
            location.subtract(x, y, z);
        }
    }

    // Draw crown jewels at the base of each spoke
    private void drawCrownJewels(float radius, float y, Location location) {
        for (int i = 0; i < numCrownSpokes; i++) {
            double inc = (2 * Math.PI) / numCrownSpokes;
            float angle = (float) ((i * inc) + (step * Math.PI / 360.0f));
            float x = MathUtils.cos(angle) * radius;
            float z = MathUtils.sin(angle) * radius;
            location.add(x, y, z);
            UtilParticles.display(0, 215, 255, location);
            location.subtract(x, y, z);
        }
    }

    // Draw inner circle of the red padding
    private void drawRedCircle(float radius, float y, Location location) {
        for (int i = 0; i < numParticlesBase; i++) {
            double inc = (2 * Math.PI) / numParticlesInnerPadding;
            float angle = (float) (i * inc);
            float x = MathUtils.cos(angle) * radius;
            float z = MathUtils.sin(angle) * radius;
            location.add(x, y, z);
            UtilParticles.display(255, 0, 0, location);
            location.subtract(x, y, z);
        }
    }

    // Draw gold circle for the crown base
    private void drawGoldCircle(float radius, float y, Location location) {
        for (int i = 0; i < numParticlesBase; i++) {
            double inc = (2 * Math.PI) / numParticlesBase;
            float angle = (float) ((i * inc) + (step * Math.PI / 360.0f));
            float x = MathUtils.cos(angle) * radius;
            float z = MathUtils.sin(angle) * radius;
            location.add(x, y, z);
            UtilParticles.display(255, 215, 0, location);
            location.subtract(x, y, z);
        }
    }

    // Draw the crown topper
    private void drawGoldTop(float radius, float y, Location location) {
        location.add(0, y + 0.2, 0);
        UtilParticles.display(255, 215, 0, location);
        location.subtract(0, y + 0.2, 0);
    }



}
