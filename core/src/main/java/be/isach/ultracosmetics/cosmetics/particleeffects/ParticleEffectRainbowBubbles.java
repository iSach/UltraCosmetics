package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Location;

/**
 * Represents an instance of rainbow bubble particles summoned by a player.
 *
 * @author SinfulMentality
 * @since 04-19-20
 */
public class ParticleEffectRainbowBubbles extends ParticleEffect {

    private int step = 0;

    public ParticleEffectRainbowBubbles(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("rainbowbubbles"));
        this.ignoreMove = false; // ignoreMove is actually stopping the animation on moving if false... change naming
    }

    @Override
    public void onUpdate() {
        // Do nothing, this effect has no idle animation.
    }

}
