package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetEtherealPearl extends Gadget implements Listener {

    Random r = new Random();

    HashMap<Player, BukkitRunnable> runnableHashMap = new HashMap<>();
    ArrayList<EnderPearl> pearls = new ArrayList<>();

    public GadgetEtherealPearl(UUID owner) {
        super(owner, GadgetType.ETHEREAL_PEARL);
        if (owner != null)
            Core.registerListener(this);
    }

    @Override
    public void onClear() {
        for (EnderPearl pearl : pearls)
            pearl.remove();
        HandlerList.unregisterAll(this);
    }

    @Override
    void onInteractRightClick() {
        if (Core.getCustomPlayer(getPlayer()).currentMount != null)
            Core.getCustomPlayer(getPlayer()).removeMount();
        if (getPlayer().getVehicle() instanceof EnderPearl) {
            getPlayer().getVehicle().remove();
        }
        if (runnableHashMap.containsKey(getPlayer())) {
            if (getPlayer().getVehicle() != null)
                getPlayer().getVehicle().remove();
            getPlayer().eject();
            if (getPlayer().getGameMode() != GameMode.CREATIVE)
                getPlayer().setAllowFlight(false);
            runnableHashMap.get(getPlayer()).cancel();
            runnableHashMap.remove(getPlayer());
            spawnRandomFirework(getPlayer().getLocation());
        }
        final EnderPearl pearl = getPlayer().launchProjectile(EnderPearl.class);
        pearl.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.53d));
        pearl.setPassenger(getPlayer());
        getPlayer().teleport(getPlayer().getLocation().add(0, 5, 0));
        pearls.add(pearl);
        if (!getPlayer().getAllowFlight()) {
            getPlayer().setAllowFlight(true);
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (pearl.isValid()) {
                    getPlayer().eject();
                    pearl.setPassenger(getPlayer());
                } else {
                    pearl.remove();
                    getPlayer().eject();
                    if (getPlayer().getGameMode() != GameMode.CREATIVE)
                        getPlayer().setAllowFlight(false);
                    runnableHashMap.remove(getPlayer());
                    spawnRandomFirework(getPlayer().getLocation());
                    cancel();
                }
            }
        };
        runnableHashMap.put(getPlayer(), runnable);
        runnable.runTaskTimer(Core.getPlugin(), 0, 10);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && runnableHashMap.containsKey((Player) event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if (runnableHashMap.containsKey(event.getPlayer()) && event.getPlayer().getName().equals(getPlayer().getName())) {
            getPlayer().eject();
            if (getPlayer().getGameMode() != GameMode.CREATIVE)
                getPlayer().setAllowFlight(false);
            runnableHashMap.get(getPlayer()).cancel();
            runnableHashMap.remove(getPlayer());
            spawnRandomFirework(getPlayer().getLocation());
        }
    }

    public FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.fromRGB(100, 0, 100)).withFade(Color.fromRGB(30, 0, 30)).build();
        return effect;
    }

    public void spawnRandomFirework(Location location) {
        final ArrayList<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Firework f = getPlayer().getWorld().spawn(location, Firework.class);

            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            f.setFireworkMeta(fm);
            fireworks.add(f);
        }
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Firework f : fireworks)
                    f.detonate();
            }
        }, 2);
    }


    @EventHandler
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        if (pearls.contains(event.getRemover())
                || event.getRemover() == getPlayer()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (pearls.contains(event.getEntity())) {
                pearls.remove(event.getEntity());
                event.getEntity().remove();
            }
        }
    }

    @Override
    void onUpdate() {
        if (runnableHashMap.containsKey(getPlayer())) {
            if (getPlayer().isOnGround()) {
                //getPlayer().getVehicle().remove();
                getPlayer().eject();
            }
        }
    }

    @Override
    void onInteractLeftClick() {
    }
}
