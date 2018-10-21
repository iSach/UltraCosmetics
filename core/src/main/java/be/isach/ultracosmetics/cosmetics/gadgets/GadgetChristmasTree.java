package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

/**
 * Represents an instance of a Christmas Tree gadget summoned by a player.
 *
 * @author iSach
 * @since 11-29-2015
 */
public class GadgetChristmasTree extends Gadget {
	
	private boolean active = false;
	private Location lastLocation;
	
	private static final Color LOG_COLOR = Color.fromRGB(101, 67, 33);
	
	public GadgetChristmasTree(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.valueOf("christmastree"), ultraCosmetics);
	}
	
	@Override
	public void onRightClick() {
		lastLocation = lastClickedBlock.getLocation().add(0.5d, 1.05d, 0.5d);
		active = true;
		Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> active = false, 200);
	}
	
	@Override
	public void onUpdate() {
		if (active) {
			drawLog();
			drawLeavesAndBalls();
			drawStar();
			drawSnow();
		}
	}
	
	private void drawSnow() {
		lastLocation.add(0, 3, 0);
		lastLocation.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, lastLocation.clone().add(4d, 3d, 4d), 10);
		lastLocation.subtract(0, 3, 0);
	}
	
	@Override
	protected boolean checkRequirements(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null
		    || event.getClickedBlock().getType() == Material.AIR) {
			getPlayer().sendMessage(MessageManager.getMessage("Gadgets.ChristmasTree.Click-On-Block"));
			return false;
		}
		return true;
	}
	
	private void drawLog() {
		Location current = lastLocation.clone();
		Location to = lastLocation.clone().add(0, 2.5, 0);
		Vector link = to.toVector().subtract(current.toVector());
		float length = (float) link.length();
		link.normalize();
		float ratio = length / 10;
		Vector vector = link.multiply(ratio);
		for (int i = 0; i < 10; i++) {
			current.getWorld().spawnParticle(Particle.REDSTONE, current, 1, new Particle.DustOptions(LOG_COLOR, 1));
			current.add(vector);
		}
	}
	
	private void drawLeavesAndBalls() {
		float radius = 0.7f;
		for (float f = 0.8f; f <= 2.5f; f += 0.2f) {
			if (radius >= 0) {
				float d = 13f / f;
				float g = MathUtils.random(0, d);
				int e = MathUtils.random(0, 2);
				if (e == 1) {
					double inc = (2 * Math.PI) / d;
					float angle = (float) (g * inc);
					float x = MathUtils.cos(angle) * (radius + 0.05f);
					float z = MathUtils.sin(angle) * (radius + 0.05f);
					lastLocation.add(x, f, z);
					lastLocation.getWorld().spawnParticle(Particle.REDSTONE, lastLocation, 1, new Particle.DustOptions(Color.fromRGB(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255)), 1));
					lastLocation.subtract(x, f, z);
				}
				for (int i = 0; i < d; i++) {
					double inc = (2 * Math.PI) / d;
					float angle = (float) (i * inc);
					float x = MathUtils.cos(angle) * radius;
					float z = MathUtils.sin(angle) * radius;
					lastLocation.add(x, f, z);
					lastLocation.getWorld().spawnParticle(Particle.REDSTONE, lastLocation, 1, new Particle.DustOptions(Color.fromRGB(0, 100, 0), 1));
					lastLocation.subtract(x, f, z);
				}
				radius = radius - (0.7f / 8.5f);
			}
		}
	}
	
	private void drawStar() {
		lastLocation.add(0, 2.6, 0);
		lastLocation.getWorld().spawnParticle(Particle.REDSTONE, lastLocation, 1, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1));
		lastLocation.subtract(0, 2.6, 0);
	}
	
	@Override
	public void onClear() {
		active = false;
		lastLocation = null;
	}
	
	@Override
	public void onLeftClick() {
	}
}
