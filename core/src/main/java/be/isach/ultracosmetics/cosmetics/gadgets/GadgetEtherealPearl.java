package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashSet;
import java.util.Set;

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

        pearl = getPlayer().launchProjectile(EnderPearl.class);
        pearl.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.53d));
        getPlayer().teleport(getPlayer().getLocation().add(0, 5, 0));
        if (!getPlayer().getAllowFlight()) {
            getPlayer().setAllowFlight(true);
        }
        pearl.setPassenger(getPlayer());
        running = true;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (running && event.getCause() == DamageCause.FALL && event.getEntity() == getPlayer()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (event.getEntity() != getPlayer()) return;
        if (pearl == null) return;
        Player player = getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setAllowFlight(false);
        }

        spawnRandomFirework(player.getLocation());
        pearl.remove();
        running = false;
    }

    public FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.fromRGB(100, 0, 100)).withFade(Color.fromRGB(30, 0, 30)).build();
    }

    public void spawnRandomFirework(Location location) {
        Set<Firework> fireworks = new HashSet<>();
        Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
            for (int i = 0; i < 4; i++) {
                Firework f = location.getWorld().spawn(location, Firework.class);
                FireworkMeta fm = f.getFireworkMeta();
                fm.addEffect(getRandomFireworkEffect());
                f.setFireworkMeta(fm);
                fireworks.add(f);
                f.setMetadata("UCFirework", new FixedMetadataValue(getUltraCosmetics(), 1));
            }
        });
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            fireworks.forEach(Firework::detonate);
            fireworks.clear();
        }, 2);
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
        if (pearl == event.getEntity()) {
            event.getEntity().remove();
            pearl = null;
        }
    }

    @Override
    public void onUpdate() {
        if (running && (pearl == null || !pearl.isValid())) {
            running = false;
            pearl = null;
            spawnRandomFirework(getPlayer().getLocation());

            Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                getPlayer().eject();
                if (getPlayer().getGameMode() != GameMode.CREATIVE) {
                    getPlayer().setAllowFlight(false);
                }
            });
        }
    }
}
