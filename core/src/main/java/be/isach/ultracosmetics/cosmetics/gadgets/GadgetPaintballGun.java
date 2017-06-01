package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Represents an instance of a paintball gun gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetPaintballGun extends Gadget implements Listener {
	
	Map<UUID, ArrayList<Projectile>> projectiles = new HashMap<>();
	
	int radius = 2;
	
	public GadgetPaintballGun(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.PAINTBALLGUN, ultraCosmetics);
		if (owner != null) {
			radius = SettingsManager.getConfig().getInt("Gadgets." + getType().getConfigName() + ".Radius");
		}
		displayCooldownMessage = false;
	}
	
	@Override
	void onRightClick() {
		Projectile projectile = getPlayer().launchProjectile(EnderPearl.class, getPlayer().getLocation().getDirection().multiply(2));
		if (projectiles.containsKey(getOwnerUniqueId()))
			projectiles.get(getOwnerUniqueId()).add(projectile);
		else {
			ArrayList<Projectile> projectilesList = new ArrayList<>();
			projectilesList.add(projectile);
			projectiles.put(getOwnerUniqueId(), projectilesList);
		}
		SoundUtil.playSound(getPlayer(), Sounds.CHICKEN_EGG_POP, 1.5f, 1.2f);
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
				BlockUtils.setToRestore(block, Material.getMaterial((String) SettingsManager.getConfig().get("Gadgets." + getType().getConfigName() + ".Block-Type")), b, 20 * 3);
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
	public void onUpdate() {
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
	void onLeftClick() {
	}
}
