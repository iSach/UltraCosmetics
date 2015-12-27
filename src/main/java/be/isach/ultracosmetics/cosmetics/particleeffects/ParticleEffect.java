package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class ParticleEffect implements Listener {

    /**
     * Current moving status of the player.
     */
    boolean moving;

    /**
     * Type of the Effect.
     */
    private ParticleEffectType type;

    /**
     * Owner of the Effect.
     */
    private UUID owner;

    /**
     * Event Listener, listens to MoveEvent.
     */
    private Listener listener;

    /**
     * If true, the effect will ignore moving.
     */
    protected boolean ignoreMove = false;

    public ParticleEffect(final UUID owner, final ParticleEffectType type) {
        this.type = type;
        if (!type.isEnabled())
            return;
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(type.getPermission())) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
            if (Core.getCustomPlayer(getPlayer()).currentParticleEffect != null)
                Core.getCustomPlayer(getPlayer()).removeParticleEffect();
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if (Bukkit.getPlayer(owner) != null
                                && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentParticleEffect != null
                                && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentParticleEffect.getType() == type) {
                            if (getType() != ParticleEffectType.FROZENWALK
                                    && getType() != ParticleEffectType.ENCHANTED
                                    && getType() != ParticleEffectType.MUSIC
                                    && getType() != ParticleEffectType.SANTAHAT
                                    && getType() != ParticleEffectType.FLAMEFAIRY
                                    && getType() != ParticleEffectType.ENDERAURA) {
                                if (!moving || ignoreMove)
                                    onUpdate();
                                if (moving) {
                                    boolean c = type == ParticleEffectType.ANGELWINGS;
                                    if (getType().getEffect() == Particles.REDSTONE) {
                                        if (!ignoreMove)
                                            for (int i = 0; i < 15; i++)
                                                if (!c)
                                                    type.getEffect().display(new Particles.OrdinaryColor(255, 0, 0), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                                else
                                                    type.getEffect().display(new Particles.OrdinaryColor(255, 255, 255), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                    } else if (getType().getEffect() == Particles.ITEM_CRACK) {
                                        for (int i = 0; i < 15; i++)
                                            Particles.ITEM_CRACK.display(new Particles.ItemData(Material.INK_SACK, ParticleEffectCrushedCandyCane.getRandomColor()), 0.2f, 0.2f, 0.2f, 0, 1, getPlayer().getLocation(), 128);
                                    } else
                                        UtilParticles.display(type.getEffect(), .4f, .3f, .4f, getPlayer().getLocation().add(0, 1, 0), 3);
                                    moving = false;
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
            runnable.runTaskTimerAsynchronously(Core.getPlugin(), 0, type.getRepeatDelay());
            listener = new ParticleEffectListener(this);
            getPlayer().sendMessage(MessageManager.getMessage("Particle-Effects.Summon").replace("%effectname%", (Core.placeHolderColor) ? type.getName() : Core.filterColor(type.getName())));
            Core.getCustomPlayer(getPlayer()).currentParticleEffect = this;
        }
    }

    /**
     * Gets Effect Type.
     *
     * @return The Type of the Effect.
     */
    public ParticleEffectType getType() {
        return this.type;
    }

    /**
     * Called each tick (not called if player isn't moving
     * and that the effect doesn't ignore moving)
     */
    abstract void onUpdate();

    /**
     * Gets Effect Owner.
     */
    protected UUID getOwner() {
        return owner;
    }

    /**
     * Gets Effect Owner as Player.
     *
     * @return Effect Owner as Player.
     */
    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    /**
     * Clears the effect.
     */
    public void clear() {
        Core.getCustomPlayer(getPlayer()).currentParticleEffect = null;
        try {
            HandlerList.unregisterAll(this);
            HandlerList.unregisterAll(listener);
        } catch (Exception exc) {
        }
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Particle-Effects.Unsummon").replace("%mountname%", (Core.placeHolderColor) ? type.getName() : Core.filterColor(type.getName())));
        owner = null;
    }

    /**
     * Effect Listener, listens to MoveEvent.
     */
    public class ParticleEffectListener implements Listener {
        private ParticleEffect particleEffect;

        public ParticleEffectListener(ParticleEffect particleEffect) {
            this.particleEffect = particleEffect;
            Core.registerListener(this);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (getPlayer() == event.getPlayer()
                    && (event.getFrom().getX() != event.getTo().getX()
                    || event.getFrom().getY() != event.getTo().getY()
                    || event.getFrom().getZ() != event.getTo().getZ()))
                particleEffect.moving = true;
        }
    }
}
