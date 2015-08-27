package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.BlockUtils;
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
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetPaintballGun extends Gadget implements Listener {

    Map<UUID, ArrayList<Projectile>> projectiles = new HashMap();

    public GadgetPaintballGun(UUID owner) {
        super(Material.DIAMOND_BARDING, (byte) 0x0, "PaintballGun", "ultracosmetics.gadgets.paintballgun", 0.2f, owner, GadgetType.PAINTBALLGUN);
        Core.registerListener(this);
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
            for (Block block : BlockUtils.getBlocksInRadius(center.getBlock().getLocation(), 2, false)) {
                BlockUtils.setToRestore(block, Material.STAINED_CLAY, b, 20 * 3);
            }
            event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation(), org.bukkit.Effect.STEP_SOUND, 49);
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
    public void clear() {
        if (projectiles.containsKey(getPlayer())) {
            for (Projectile projectile : projectiles.get(getPlayer())) {
                projectile.remove();
            }
            projectiles.remove(getPlayer());
        }
    }

    @Override
    void onInteractLeftClick() {
    }


}
