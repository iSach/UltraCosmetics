package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents an instance of a particle effect summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-03-2015
 */
public abstract class ParticleEffect extends Cosmetic<ParticleEffectType> implements Updatable {

    /**
     * If true, the effect will ignore moving.
     */
    protected boolean ignoreMove = false;

    public ParticleEffect(UltraCosmetics ultraCosmetics, UltraPlayer ultraPlayer, final ParticleEffectType type) {
        super(ultraCosmetics, Category.EFFECTS, ultraPlayer, type);

        if (!getPlayer().hasPermission(type.getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }

        if (getOwner().getCurrentParticleEffect() != null) {
            getOwner().removeParticleEffect();
        }

        // TODO
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (Bukkit.getPlayer(getOwnerUniqueId()) != null
                            && getOwner().getCurrentParticleEffect() != null
                            && getOwner().getCurrentParticleEffect().getType() == type) {
                        if (getType() != ParticleEffectType.FROZENWALK
                                && getType() != ParticleEffectType.ENCHANTED
                                && getType() != ParticleEffectType.MUSIC
                                && getType() != ParticleEffectType.SANTAHAT
                                && getType() != ParticleEffectType.FLAMEFAIRY
                                && getType() != ParticleEffectType.ENDERAURA) {
                            if (!isMoving() || ignoreMove)
                                onUpdate();
                            if (isMoving()) {
                                boolean c = type == ParticleEffectType.ANGELWINGS;
                                if (getType().getEffect() == Particles.REDSTONE) {
                                    if (!ignoreMove) {
                                        for (int i = 0; i < 15; i++) {
                                            if (!c) {
                                                type.getEffect().display(new Particles.OrdinaryColor(255, 0, 0), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                            } else {
                                                type.getEffect().display(new Particles.OrdinaryColor(255, 255, 255), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                            }
                                        }
                                    }
                                } else if (getType().getEffect() == Particles.ITEM_CRACK) {
                                    for (int i = 0; i < 15; i++)
                                        Particles.ITEM_CRACK.display(new Particles.ItemData(Material.INK_SACK, ParticleEffectCrushedCandyCane.getRandomColor()), 0.2f, 0.2f, 0.2f, 0, 1, getPlayer().getLocation(), 128);
                                } else
                                    UtilParticles.display(type.getEffect(), .4f, .3f, .4f, getPlayer().getLocation().add(0, 1, 0), 3);
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
        };
        runnable.runTaskTimerAsynchronously(UltraCosmeticsData.get().getPlugin(), 0, type.getRepeatDelay());

        getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).setCurrentParticleEffect(this);
    }

    protected boolean isMoving() {
        return getOwner().isMoving();
    }

    @Override
    protected void onClear() {
    }
}
