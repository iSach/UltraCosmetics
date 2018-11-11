package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Effect;

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
                getPlayer().getWorld().spawnParticle(Particle.PORTAL, getPlayer().getLocation().add(0, 1, 0), 3, 0.2D, 0.01D, 0.2D);
	}
}
