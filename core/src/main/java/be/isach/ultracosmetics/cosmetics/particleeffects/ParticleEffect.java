package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;

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

        runTaskTimerAsynchronously(getUltraCosmetics(), 0, getType().getRepeatDelay());
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
                                for (int i = 0; i < getModifiedAmount(15); i++) {
                                    if (!c) {
                                        getType().getEffect().display(new Particles.OrdinaryColor(255, 0, 0), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                    } else {
                                        getType().getEffect().display(new Particles.OrdinaryColor(255, 255, 255), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                    }
                                }
                            }
                        } else if (getType().getEffect() == Particles.ITEM_CRACK) {
                            if (UltraCosmeticsData.get().getServerVersion().isAtLeast(ServerVersion.v1_14_R1)) {
                                for (int i = 0; i < getModifiedAmount(15); i++) {
                                    getPlayer().getLocation().getWorld().spawnParticle(Particle.ITEM_CRACK, getPlayer().getLocation(), 1, 0.2, 0.2, 0.2, 0, UCMaterial.DYES.get(MathUtils.random(0, 14)).parseItem());
                                }
                            } else {
                                for (int i = 0; i < getModifiedAmount(15); i++) {
                                    Particles.ITEM_CRACK.display(new Particles.ItemData(BlockUtils.getDyeByColor(ParticleEffectCrushedCandyCane.getRandomColor()), ParticleEffectCrushedCandyCane.getRandomColor()), 0.2f, 0.2f, 0.2f, 0, 1, getPlayer().getLocation(), 128);
                                }
                            }
                        } else
                            UtilParticles.display(getType().getEffect(), .4f, .3f, .4f, getPlayer().getLocation().add(0, 1, 0), getModifiedAmount(3));
                    }
                } else
                    onUpdate();
            } else
                cancel();
        } catch (NullPointerException exc) {
            exc.printStackTrace();
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
    
    protected int getModifiedAmount(int originalAmount) {
        // always return at least 1 so the particles work
        return Integer.max((int) (originalAmount * getType().getParticleMultiplier()), 1);
    }
}
