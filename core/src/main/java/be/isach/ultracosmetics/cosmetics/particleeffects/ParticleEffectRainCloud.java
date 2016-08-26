package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;

/**
 * z
 * Created by sacha on 12/08/15.
 */
public class ParticleEffectRainCloud extends ParticleEffect {

    public ParticleEffectRainCloud(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.RAINCLOUD);
    }

    @Override
    public void onUpdate() {
        UtilParticles.display(Particles.CLOUD, 0.5F, 0.1f, 0.5f, getPlayer().getLocation().add(0, 3, 0), 10);
        UtilParticles.display(getType().getEffect(), 0.25F, 0.05f, 0.25f, getPlayer().getLocation().add(0, 3, 0), 1);

    }

    @Override
    protected void onEquip() {

    }
}
