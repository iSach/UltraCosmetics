package be.isach.ultracosmetics.cosmetics.particleeffects;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class ParticleEffectFrozenWalk extends ParticleEffect {

    public ParticleEffectFrozenWalk(UUID owner) {
        super(Effect.ITEM_BREAK, Material.SNOW_BALL, (byte) 0, "FrozenWalk", "ultracosmetics.particleeffects.frozenwalk", owner, ParticleEffectType.FROZENWALK, 1,
                "&7&oMy feet are so cold!");
    }

    @Override
    void onUpdate() {
        Vector vectorLeft = getLeftVector(getPlayer().getLocation()).normalize().multiply(0.15);
        Vector vectorRight = getRightVector(getPlayer().getLocation()).normalize().multiply(0.15);
        Location locationLeft = getPlayer().getLocation().add(vectorLeft);
        Location locationRight = getPlayer().getLocation().add(vectorRight);
        locationLeft.setY(getPlayer().getLocation().getY());
        locationRight.setY(getPlayer().getLocation().getY());

        be.isach.ultracosmetics.util.ParticleEffect.ITEM_CRACK.display(new be.isach.ultracosmetics.util.ParticleEffect.ItemData(Material.SNOW, (byte) 0), 0, 0, 0, 0f, 0, locationLeft, 32);
        be.isach.ultracosmetics.util.ParticleEffect.ITEM_CRACK.display(new be.isach.ultracosmetics.util.ParticleEffect.ItemData(Material.SNOW, (byte)0), 0, 0, 0, 0f, 0, locationRight, 32);
    }

    public static Vector getLeftVector(Location loc) {
        final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));

        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }

    public static Vector getRightVector(Location loc) {
        final float newX = (float) (loc.getX() + (-1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (-1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));

        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }
}
