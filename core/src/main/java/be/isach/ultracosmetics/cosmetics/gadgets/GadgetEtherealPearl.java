package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
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

/**
 * Represents an instance of a ethereal pearl gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetEtherealPearl extends Gadget implements Listener {

    private EnderPearl pearl;

    public GadgetEtherealPearl(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.ETHEREALPEARL, ultraCosmetics);
    }

    @Override
    public void onClear() {
        if(pearl != null) {
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
        }

        EnderPearl pearl = getPlayer().launchProjectile(EnderPearl.class);
        pearl.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.53d));
        pearl.setPassenger(getPlayer());
        getPlayer().teleport(getPlayer().getLocation().add(0, 5, 0));
        if (!getPlayer().getAllowFlight()) {
            getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity() == getPlayer()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if (pearl != null && event.getPlayer().getName().equals(getPlayer().getName())) {
            getPlayer().eject();

            if (getPlayer().getGameMode() != GameMode.CREATIVE) {
                getPlayer().setAllowFlight(false);
            }

            spawnRandomFirework(getPlayer().getLocation());
            pearl.remove();
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
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
            @Override
            public void run() {
                for (Firework f : fireworks)
                    f.detonate();
            }
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

            if(getPlayer().isOnGround()) {
                pearl.remove();
                pearl = null;
            }
        } else {
            pearl.remove();
            getPlayer().eject();

            if (getPlayer().getGameMode() != GameMode.CREATIVE) {
                getPlayer().setAllowFlight(false);
            }

            pearl = null;
            spawnRandomFirework(getPlayer().getLocation());
            cancel();
        }
    }

    @Override
    void onLeftClick() {
    }
}
