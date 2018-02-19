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
	
	public GadgetEtherealPearl(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.valueOf("EtherealPearl"), ultraCosmetics);
	}
	
	@Override
	public void onClear() {
		if (pearl != null) {
			pearl.remove();
		}
	}
	
	@Override
	public void onRightClick() {
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
		if (pearl != null && event.getPlayer() == getPlayer()) {
			event.getPlayer().eject();
			
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.getPlayer().setAllowFlight(false);
			}
			
			spawnRandomFirework(event.getPlayer().getLocation());
			pearl.remove();
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
					Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
					FireworkMeta fwm = fw.getFireworkMeta();
					Random r = new Random();
					int rt = r.nextInt(4) + 1;
        				Type type = Type.BALL;
        				if (rt == 1) type = Type.BALL;
        				if (rt == 2) type = Type.BALL_LARGE;
        				if (rt == 3) type = Type.BURST;
        				if (rt == 4) type = Type.CREEPER;
        				if (rt == 5) type = Type.STAR;
        				int r1i = r.nextInt(17) + 1;
        				int r2i = r.nextInt(17) + 1;
        				Color c1 = plugin.getColor(r1i);
        				Color c2 = plugin.getColor(r2i);
        				FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        				fwm.addEffect(effect);
        				fw.setFireworkMeta(fwm);
					/**fm.addEffect(getRandomFireworkEffect());
					f.setFireworkMeta(fm);*/
					fireworks.add(fw);
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
				pearl.remove();
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
			getPlayer().eject();
			
			if (getPlayer().getGameMode() != GameMode.CREATIVE) {
				getPlayer().setAllowFlight(false);
			}
			
			pearl.remove();
			pearl = null;
			spawnRandomFirework(getPlayer().getLocation());
		}
	}
	
	@Override
	public void onLeftClick() {
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
}
