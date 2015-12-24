package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.UtilParticles;

import java.util.UUID;

/**
 * Created by sacha on 13/08/15.
 */
public class ParticleEffectInLove extends ParticleEffect {

    public ParticleEffectInLove(UUID owner) {
        super(owner, ParticleEffectType.INLOVE
        );
    }

    @Override
    void onUpdate() {
        UtilParticles.display(getType().getEffect(), 0.5f, 0.5f, 0.5f, getPlayer().getLocation().add(0, 1, 0), 2);
    }
}
