package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.util.*;

/**
 * Represents an instance of a paintball gun gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetPaintballGun extends Gadget implements Listener {

    private static final List<XMaterial> PAINT_BLOCKS = new ArrayList<>();

    static {
        String ending = SettingsManager.getConfig().getString("Gadgets." + GadgetType.valueOf("paintballgun").getConfigName() + ".Block-Type", "_TERRACOTTA").toUpperCase();
        for (XMaterial mat : XMaterial.VALUES) {
            if (mat.name().endsWith(ending)) {
                PAINT_BLOCKS.add(mat);
            }
        }
        if (PAINT_BLOCKS.isEmpty()) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.ERROR, "Paintball Gun setting 'Block-Type' does not match any known blocks.");
            PAINT_BLOCKS.add(XMaterial.BEDROCK);
        }
    }

    private final Set<Projectile> projectiles = new HashSet<>();
    private final int radius;

    public GadgetPaintballGun(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("paintballgun"), ultraCosmetics);
        radius = SettingsManager.getConfig().getInt("Gadgets." + getType().getConfigName() + ".Radius", 2);
        displayCooldownMessage = false;
    }

    @Override
    void onRightClick() {
        Projectile projectile = getPlayer().launchProjectile(EnderPearl.class, getPlayer().getLocation().getDirection().multiply(2));
        projectiles.add(projectile);
        SoundUtil.playSound(getPlayer(), Sounds.CHICKEN_EGG_POP, 1.5f, 1.2f);
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        for (Projectile proj : projectiles) {
            // equivalent to distance(vehicleLocation) < 10 but more performant
            if (proj.getLocation().distanceSquared(event.getVehicle().getLocation()) < 100) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Projectile) {
            if (projectiles.contains(event.getRemover())) {
                event.setCancelled(true);
            }
            // TODO: do we really want to prevent players from breaking hanging things while this gadget is equipped??
            // or is this required to prevent ender pearls from breaking things?
        } else if (event.getRemover() == getPlayer())
            event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        // if successfully removed (in other words, if it was there to begin with)
        if (projectiles.remove(event.getEntity())) {
            Location center = event.getEntity().getLocation().add(event.getEntity().getVelocity());
            for (Block block : BlockUtils.getBlocksInRadius(center.getBlock().getLocation(), radius, false)) {
                BlockUtils.setToRestore(block, PAINT_BLOCKS.get(RANDOM.nextInt(PAINT_BLOCKS.size())), 20 * 3);
            }
            if (SettingsManager.getConfig().getBoolean("Gadgets." + getType().getConfigName() + ".Particle.Enabled")) {
                Particles effect = Particles.valueOf((SettingsManager.getConfig().getString("Gadgets." + getType().getConfigName() + ".Particle.Effect")).replace("_", ""));
                UtilParticles.display(effect, 2.5, 0.2f, 2.5f, center.clone().add(0.5f, 1.2f, 0.5F), 50);
            }
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.ENDER_PEARL) return;
        if (projectiles.contains(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer().getUniqueId().equals(getOwnerUniqueId())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUpdate() {
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // TODO: can we check if the pearl that caused the spawn is the pearl thrown by this gadget?
        if (event.getSpawnReason() == SpawnReason.ENDER_PEARL)
            event.setCancelled(true);
    }

    @Override
    public void onClear() {
        for (Projectile projectile : projectiles) {
            projectile.remove();
        }
        projectiles.clear();
    }
}
