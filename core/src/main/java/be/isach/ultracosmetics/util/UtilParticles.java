package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by sacha on 07/08/15.
 */
public class UtilParticles {
	
	private final static int DEF_RADIUS = 128;
	
	public static void drawParticleLine(Location from, Location to, Particle effect, int particles, int r, int g, int b) {
		Location location = from.clone();
		Location target = to.clone();
		Vector link = target.toVector().subtract(location.toVector());
		float length = (float) link.length();
		link.normalize();
		
		float ratio = length / particles;
		Vector v = link.multiply(ratio);
		Location loc = location.clone().subtract(v);
		int step = 0;
		for (int i = 0; i < particles; i++) {
			if (step >= (double) particles)
				step = 0;
			step++;
			loc.add(v);
			if (effect == Particle.REDSTONE)
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 128, new Particle.DustOptions(Color.fromRGB(r, g, b), 1));
			else
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 128, new Particle.DustOptions(Color.fromRGB(0, 0, 0), 1));
		}
	}
	
	public static void playHelix(final Location loc, final float i, final Particle effect) {
		BukkitRunnable runnable = new BukkitRunnable() {
			double radius = 0;
			double step;
			double y = loc.getY();
			Location location = loc.clone().add(0, 3, 0);
			
			@Override
			public void run() {
				double inc = (2 * Math.PI) / 50;
				double angle = step * inc + i;
				Vector v = new Vector();
				v.setX(Math.cos(angle) * radius);
				v.setZ(Math.sin(angle) * radius);
				if (effect == Particle.REDSTONE)
					loc.getWorld().spawnParticle(Particle.REDSTONE, location, 255, new Particle.DustOptions(Color.fromRGB(0, 0, 255), 1));
				else
					loc.getWorld().spawnParticle(effect, location, 1);
				location.subtract(v);
				location.subtract(0, 0.1d, 0);
				if (location.getY() <= y) {
					cancel();
				}
				step += 4;
				radius += 1 / 50f;
			}
		};
		runnable.runTaskTimer(UltraCosmeticsData.get().getPlugin(), 0, 1);
	}
}
