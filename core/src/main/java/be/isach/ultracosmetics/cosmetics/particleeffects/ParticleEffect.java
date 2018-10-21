package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * Represents an instance of a particle effect summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class ParticleEffect extends Cosmetic<ParticleEffectType> implements Updatable {
	
	/**
	 * If true, the effect will ignore moving.
	 */
	protected boolean ignoreMove = false;
	
	public ParticleEffect(UltraCosmetics ultraCosmetics, UltraPlayer ultraPlayer, final ParticleEffectType type) {
		super(ultraCosmetics, Category.EFFECTS, ultraPlayer, type);
	}
	
	@Override
	protected void onEquip() {
		if (getOwner().getCurrentParticleEffect() != null) {
			getOwner().removeParticleEffect();
		}
		getOwner().setCurrentParticleEffect(this);
		
		runTaskTimerAsynchronously(getUltraCosmetics(), 0, 1);
	}
	
	@Override
	public void run() {
		super.run();
		
		try {
			if (Bukkit.getPlayer(getOwnerUniqueId()) != null
			    && getOwner().getCurrentParticleEffect() != null
			    && getOwner().getCurrentParticleEffect().getType() == getType()) {
				if (getType() != ParticleEffectType.valueOf("frozenwalk")
				    && getType() != ParticleEffectType.valueOf("enchanted")
				    && getType() != ParticleEffectType.valueOf("music")
				    && getType() != ParticleEffectType.valueOf("santahat")
				    && getType() != ParticleEffectType.valueOf("flamefairy")
				    && getType() != ParticleEffectType.valueOf("enderaura")) {
					if (!isMoving() || ignoreMove)
						onUpdate();
					if (isMoving()) {
						boolean c = getType() == ParticleEffectType.valueOf("angelwings");
						if (getType().getEffect() == Particles.REDSTONE) {
							if (!ignoreMove) {
								for (int i = 0; i < 15; i++) {
									if (!c) {
										getType().getEffect().display(new Particles.OrdinaryColor(255, 0, 0), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
									} else {
										getType().getEffect().display(new Particles.OrdinaryColor(255, 255, 255), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
									}
								}
							}
						} else if (getType().getEffect() == Particles.ITEM_CRACK) {
							for (int i = 0; i < 15; i++)
								Particles.ITEM_CRACK.display(new Particles.ItemData(BlockUtils.getOldMaterial("INK_SACK"), ParticleEffectCrushedCandyCane.getRandomColor()), 0.2f, 0.2f, 0.2f, 0, 1, getPlayer().getLocation(), 128);
						} else
							UtilParticles.display(getType().getEffect(), .4f, .3f, .4f, getPlayer().getLocation().add(0, 1, 0), 3);
					}
				} else
					onUpdate();
			} else
				cancel();
		} catch (NullPointerException exc) {
			clear();
			cancel();
		}
	}
	
	protected boolean isMoving() {
		return getOwner().isMoving();
	}
	
	@Override
	protected void onClear() {
	}
}
