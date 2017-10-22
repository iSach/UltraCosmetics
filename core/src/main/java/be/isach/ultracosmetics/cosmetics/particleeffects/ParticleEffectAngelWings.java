package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Represents an instance of angel wing particles summoned by a player.
 *
 * @author iSach
 * @since 11-11-2015
 */
public class ParticleEffectAngelWings extends ParticleEffect {

	boolean x = true;
	boolean o = false;

	public ParticleEffectAngelWings(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(ultraCosmetics, owner, ParticleEffectType.valueOf("angelwings"));
	}

	private boolean[][] shape = {
			{ o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o },
			{ o, x, x, x, x, o, o, o, o, o, o, o, x, x, x, x, o, o },
			{ o, o, x, x, x, x, x, o, o, o, x, x, x, x, x, o, o, o },
			{ o, o, o, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o },
			{ o, o, o, o, x, x, x, x, x, x, x, x, x, o, o, o, o, o },
			{ o, o, o, o, x, x, x, x, o, x, x, x, x, o, o, o, o, o },
			{ o, o, o, o, o, x, x, x, o, x, x, x, o, o, o, o, o, o },
			{ o, o, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o, o },
			{ o, o, o, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o }
	};

	@Override
	public void onUpdate() {
		drawParticles(getPlayer().getLocation());
	}

	private void drawParticles(Location location) {
		double space = 0.2;
		double defX = location.getX() - (space * shape[0].length / 2) + space;
		double x = defX;
		double y = location.clone().getY() + 2;
		double angle = -((location.getYaw() + 180) / 60);
		angle += (location.getYaw() < -180 ? 3.25 : 2.985);

		for (boolean[] aShape : shape) {
			for (boolean anAShape : aShape) {
				if (anAShape) {

					Location target = location.clone();
					target.setX(x);
					target.setY(y);

					Vector v = target.toVector().subtract(location.toVector());
					Vector v2 = getBackVector(location);
					v = rotateAroundAxisY(v, angle);
					v2.setY(0).multiply(-0.2);

					location.add(v);
					location.add(v2);
					for (int k = 0; k < 3; k++)
						UtilParticles.display(255, 255, 255, location);
					location.subtract(v2);
					location.subtract(v);
				}
				x += space;
			}
			y -= space;
			x = defX;
		}
	}

	public static Vector rotateAroundAxisY(Vector v, double angle) {
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static Vector getBackVector(Location loc) {
		final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 90))));
		final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 90))));
		return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
	}
}
