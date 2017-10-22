package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Represents an instance of an antigravity gadget summoned by a player.
 *
 * @author iSach
 * @since 08-10-2015
 */
public class GadgetAntiGravity extends Gadget {
	
	private ArmorStand as;
	private boolean running;
	
	public GadgetAntiGravity(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.valueOf("antigravity"), ultraCosmetics);
	}
	
	@Override
	void onRightClick() {
		as = getPlayer().getWorld().spawn(getPlayer().getLocation(), ArmorStand.class);
		as.setMetadata("NO_INTER", new FixedMetadataValue(getUltraCosmetics(), ""));
		as.setGravity(false);
		as.setSmall(true);
		running = true;
		as.setVisible(false);
		as.setHelmet(new ItemStack(Material.SEA_LANTERN));
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> running = false, 240);
	}
	
	@Override
	void onLeftClick() {
	}
	
	@Override
	public void onUpdate() {
		if (as != null && as.isValid()) {
			if (running) {
				as.setHeadPose(as.getHeadPose().add(0, 0.1, 0));
				UtilParticles.display(Particles.PORTAL, 3f, 3f, 3f, as.getLocation(), 150);
				UtilParticles.display(Particles.SPELL_WITCH, .3f, .3f, .3f, as.getEyeLocation(), 5);
				for (Entity ent : as.getNearbyEntities(3, 2, 3)) {
					if (ent instanceof LivingEntity && !(ent instanceof ArmorStand)) {
						MathUtils.applyVelocity(ent, new Vector(0, 0.05, 0));
					}
				}
			} else {
				as.remove();
				as = null;
			}
		}
	}
	
	// Find a fkn alternative to this shit :^)
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		try {
			if (running) {
				if (as != null && as.isValid() && event.getReason().contains("Fly")) {
					if (as.getLocation().distance(event.getPlayer().getLocation()) < 8) {
						event.setCancelled(true);
						System.out.println("UltraCosmetics >> Cancelling invalid Flight KicK.");
						return;
					}
				}
				event.setCancelled(true);
				System.out.println("UltraCosmetics >> Cancelling invalid Flight KicK.");
			}
		} catch (Exception exc) {
		}
	}
	
	@Override
	public void onClear() {
		if (as != null) {
			as.remove();
		}
		running = false;
	}
}
