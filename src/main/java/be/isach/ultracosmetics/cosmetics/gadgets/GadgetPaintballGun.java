package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.util.*;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetPaintballGun extends Gadget implements Listener {

    Map<UUID, ArrayList<Projectile>> projectiles = new HashMap();

    int radius = 2;

    public GadgetPaintballGun(UUID owner) {
        super(Material.DIAMOND_BARDING, (byte) 0x0, "PaintballGun", "ultracosmetics.gadgets.paintballgun", 0.2f, owner, GadgetType.PAINTBALLGUN, "&7&oPEW PEW PEW PEW!!");
        if (owner != null) {
            Core.registerListener(this);
            radius = SettingsManager.getConfig().getInt("Gadgets." + getType().configName + ".Radius");
        }
        displayCountdownMessage = false;
    }

    @Override
    void onInteractRightClick() {
        Projectile projectile = getPlayer().launchProjectile(EnderPearl.class, getPlayer().getLocation().getDirection().multiply(2));
        if (projectiles.containsKey(getOwner()))
            projectiles.get(getOwner()).add(projectile);
        else {
            ArrayList<Projectile> projectilesList = new ArrayList<>();
            projectilesList.add(projectile);
            projectiles.put(getOwner(), projectilesList);
        }
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 1.5F, 1.2F);
    }

    public boolean mapContainsProjectile(Projectile projectile) {
        for (ArrayList<Projectile> plist : projectiles.values()) {
            if (plist.contains(projectile)) return true;
        }
        return false;
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        for (ArrayList<Projectile> projectile : projectiles.values()) {
            for (Projectile proj : projectile) {
                if (proj.getLocation().distance(event.getVehicle().getLocation()) < 10) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Projectile) {
            if (mapContainsProjectile((Projectile) event.getRemover())) {
                event.setCancelled(true);
            }
        } else if (event.getRemover() == getPlayer())
            event.setCancelled(true);
    }

    public void removeProjectile(Projectile projectile) {
        for (UUID uuid : projectiles.keySet()) {
            ArrayList<Projectile> plist = projectiles.get(uuid);
            if (plist.contains(projectile)) {
                plist.remove(projectile);
                if (plist.isEmpty())
                    projectiles.remove(uuid);
                return;
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if (mapContainsProjectile(event.getEntity())) {
            removeProjectile(event.getEntity());
            Random r = new Random();
            byte b = (byte) r.nextInt(15);
            Location center = event.getEntity().getLocation().add(event.getEntity().getVelocity());
            for (Block block : BlockUtils.getBlocksInRadius(center.getBlock().getLocation(), radius, false)) {
                BlockUtils.setToRestore(block, Material.getMaterial((String) SettingsManager.getConfig().get("Gadgets." + getType().configName + ".Block-Type")), b, 20 * 3);
            }
            if (SettingsManager.getConfig().getBoolean("Gadgets." + getType().configName + ".Particle.Enabled")) {
                Particles effect = Particles.valueOf((SettingsManager.getConfig().getString("Gadgets." + getType().configName + ".Particle.Effect")).replace("_", ""));
                UtilParticles.play(effect, 2.5, 0.2f, 2.5f, center.clone().add(0.5f, 1.2f, 0.5F), 50);
            }
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.ENDER_PEARL) return;
        if (mapContainsProjectile((Projectile) event.getDamager()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTeleport(PlayerTeleportEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (projectiles.containsKey(uuid)) {
            event.setCancelled(true);
        }

    }

    @Override
    void onUpdate() {
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.ENDERMITE)
            event.setCancelled(true);
    }

    @Override
    public void onClear() {
        for (ArrayList<Projectile> list : projectiles.values()) {
            for (Projectile projectile : list) {
                projectile.remove();
            }
        }
    }

    @Override
    void onInteractLeftClick() {
    }


}
