package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;

/**
 * Represents an instance of snow cloud particles summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class ParticleEffectSnowCloud extends ParticleEffect {

    public ParticleEffectSnowCloud(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("snowcloud"));
    }

    @Override
    public void onUpdate() {
        Particles.CLOUD.display(0.5F, 0.1f, 0.5f, getPlayer().getLocation().add(0, 3, 0), getModifiedAmount(10));
        getType().getEffect().display(0.25F, 0.05f, 0.25f, getPlayer().getLocation().add(0, 3, 0), 1);
    }

}
