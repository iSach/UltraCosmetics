package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
public class GadgetEtherealPearl extends Gadget implements Updatable {

    private EnderPearl pearl;
    private boolean running;
    private boolean handledThisTick = false;
    private Location lastLoc = null;

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
    protected boolean checkRequirements(PlayerInteractEvent event) {
        // For some reason, the client sends two `use` packets for ender pearls
        // so we have to figure out how to ignore one of them.
        if (handledThisTick) return false;
        handledThisTick = true;
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> handledThisTick = false, 1);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onRightClick() {
        getOwner().removeCosmetic(Category.MOUNTS);

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
        if (pearl == null) return;
        if (event.getEntity() != getPlayer()) return;
        endRide();
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

    private void endRide() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE) {
            getPlayer().setAllowFlight(false);
        }

        // Don't get stuck in the ground or in a wall
        if (lastLoc != null) {
            getPlayer().teleport(lastLoc.add(0, 1, 0));
        }
        spawnRandomFirework(getPlayer().getLocation());
        if (pearl != null) {
            pearl.remove();
            pearl = null;
        }
        running = false;
    }

    @Override
    public void onUpdate() {
        if (running && (pearl == null || !pearl.isValid())) {
            endRide();
        } else {
            lastLoc = getPlayer().getLocation();
        }
    }
}
