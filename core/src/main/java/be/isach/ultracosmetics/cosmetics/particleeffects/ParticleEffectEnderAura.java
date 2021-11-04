package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;

/**
 * Represents an instance of ender aura particles summoned by a player.
 *
 * @author iSach
 * @since 12-23-2015
 */
public class ParticleEffectEnderAura extends ParticleEffect {

    public ParticleEffectEnderAura(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("enderaura"));
    }

    @Override
    public void onUpdate() {
        Particles.PORTAL.display(0.35F, 0.05F, 0.35F, 0.1f, getModifiedAmount(5), getPlayer().getLocation().add(0, 1.2d, 0), 128);
        Particles.PORTAL.display(0.35F, 0.05F, 0.35F, 0.1f, getModifiedAmount(5), getPlayer().getLocation().add(0, 0.2d, 0), 128);
    }
}
