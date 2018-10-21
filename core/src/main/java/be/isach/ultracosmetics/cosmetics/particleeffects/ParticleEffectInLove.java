package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;

/**
 * Represents an instance of in love particles summoned by a player.
 *
 * @author iSach
 * @since 08-13-2015
 */
public class ParticleEffectInLove extends ParticleEffect {

	public ParticleEffectInLove(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(ultraCosmetics, owner, ParticleEffectType.valueOf("inlove"));
	}

	@Override
	public void onUpdate() {
		getPlayer().getWorld().spawnParticle(getType().getEffect(), getPlayer().getLocation().add(0, 1, 0), 2, 0.5f, 0.5f, 0.5f);
	}
}
