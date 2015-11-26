package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 13/08/15.
 */
public class ParticleEffectInLove extends ParticleEffect {

    public ParticleEffectInLove(UUID owner) {
        super(Particles.HEART, Material.RED_ROSE, (byte) 0x0, "InLove", "ultracosmetics.particleeffects.inlove", owner, ParticleEffectType.INLOVE, 6,
                "&7&oOMG, I am in love!");
    }

    @Override
    void onUpdate() {
        UtilParticles.play(getEffect(), 0.5f, 0.5f, 0.5f, getPlayer().getLocation().add(0, 1, 0), 2);
    }
}
