package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;

/**
 * Represents an instance of a ethereal pearl gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetEtherealPearl extends Gadget implements Listener {

    private EnderPearl pearl;
    private boolean running;

    public GadgetEtherealPearl(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("etherealpearl"), ultraCosmetics);
        running = false;
    }

    @Override
    public void onClear() {
        if (pearl != null) {
            pearl.remove();
        }
    }

    @Override
    void onRightClick() {
        if (getOwner().getCurrentMount() != null) {
            getOwner().removeMount();
        }

        if (getPlayer().getVehicle() instanceof EnderPearl) {
            getPlayer().getVehicle().remove();
            getPlayer().eject();
        }

        EnderPearl pearl = getPlayer().launchProjectile(EnderPearl.class);
        pearl.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.53d));
        pearl.setPassenger(getPlayer());
        getPlayer().teleport(getPlayer().getLocation().add(0, 5, 0));
        if (!getPlayer().getAllowFlight()) {
            getPlayer().setAllowFlight(true);
        }
        pearl.setPassenger(getPlayer());
        running = true;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity() == getPlayer()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if (pearl != null && event.getPlayer() == getPlayer()) {
            event.getPlayer().eject();

            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.getPlayer().setAllowFlight(false);
            }

            spawnRandomFirework(event.getPlayer().getLocation());
            pearl.remove();
            running = false;
        }
    }

    public FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.fromRGB(100, 0, 100)).withFade(Color.fromRGB(30, 0, 30)).build();
    }

    public void spawnRandomFirework(Location location) {
        // Temporary try/catch to avoid errors. //TODO fix NPE here (unknown reason yet)
        try {
            ArrayList<Firework> fireworks = new ArrayList<>();
            Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                for (int i = 0; i < 4; i++) {
                    Firework f = location.getWorld().spawn(location, Firework.class);
                    FireworkMeta fm = f.getFireworkMeta();
                    fm.addEffect(getRandomFireworkEffect());
                    f.setFireworkMeta(fm);
                    fireworks.add(f);
                }
            });
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                fireworks.forEach(Firework::detonate);
                fireworks.clear();
            }, 2);
        } catch (Exception exc) {
        }
    }


    @EventHandler
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        if (pearl == event.getRemover()
                || event.getRemover() == getPlayer()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (pearl == event.getEntity()) {
                event.getEntity().remove();
                pearl = null;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (pearl != null && pearl.isValid()) {
            getPlayer().eject();
            pearl.setPassenger(getPlayer());

            if (getPlayer().isOnGround()) {
                pearl.remove();
                pearl = null;
            }
        } else {
            if (running) {
                running = false;

                Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                    getPlayer().eject();

                    if (getPlayer().getGameMode() != GameMode.CREATIVE) {
                        getPlayer().setAllowFlight(false);
                    }
                });

                pearl = null;
                spawnRandomFirework(getPlayer().getLocation());
            }
        }
    }

    @Override
    void onLeftClick() {
    }
}
