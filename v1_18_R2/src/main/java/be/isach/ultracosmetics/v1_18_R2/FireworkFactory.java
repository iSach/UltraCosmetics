package be.isach.ultracosmetics.v1_18_R2;

import be.isach.ultracosmetics.v1_18_R2.customentities.CustomEntityFirework;
import be.isach.ultracosmetics.version.IFireworkFactory;
import net.minecraft.world.entity.Entity;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * @author RadBuilder
 */
public class FireworkFactory implements IFireworkFactory {
    @Override
    public void spawn(Location location, FireworkEffect effect, Player... players) {
        CustomEntityFirework firework = new CustomEntityFirework(((CraftWorld) location.getWorld()).getHandle(), players);
        FireworkMeta meta = ((Firework) firework.getBukkitEntity()).getFireworkMeta();
        meta.addEffect(effect);
        ((Firework) firework.getBukkitEntity()).setFireworkMeta(meta);
        ((Entity)firework).setPos(location.getX(), location.getY(), location.getZ());

        if ((((CraftWorld) location.getWorld()).getHandle()).addFreshEntity(firework)) {
            ((Entity)firework).setInvisible(true);
        }
    }
}
