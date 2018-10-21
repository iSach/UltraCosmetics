package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Particle;

/**
 * Represents an instance of rain cloud particles summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class ParticleEffectRainCloud extends ParticleEffect {

	public ParticleEffectRainCloud(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(ultraCosmetics, owner, ParticleEffectType.valueOf("raincloud"));
	}

	@Override
	public void onUpdate() {
		getPlayer().getWorld().spawnParticle(Particle.CLOUD, getPlayer().getLocation().add(0, 3, 0), 10, 0.5F, 0.1f, 0.5f);
		getPlayer().getWorld().spawnParticle(getType().getEffect(), getPlayer().getLocation().add(0, 3, 0), 1, 0.25F, 0.05f, 0.25f);

	}
}
