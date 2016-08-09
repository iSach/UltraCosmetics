package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;

import java.util.UUID;

/**
 * Created by sacha on 13/08/15.
 */
public class ParticleEffectInLove extends ParticleEffect {

    public ParticleEffectInLove(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.INLOVE);
    }

    @Override
    public void onUpdate() {
        UtilParticles.display(getCosmeticType().getEffect(), 0.5f, 0.5f, 0.5f, getPlayer().getLocation().add(0, 1, 0), 2);
    }

    @Override
    protected void onEquip() {

    }
}
