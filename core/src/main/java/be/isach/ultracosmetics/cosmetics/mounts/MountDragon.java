package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 17/08/15.
 */
public class MountDragon extends Mount {

    public MountDragon(UUID owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.DRAGON, ultraCosmetics);
        if (owner != null)
            UltraCosmetics.getInstance().registerListener(this);
    }

    @Override
    protected void onUpdate() {
        if (entity.getPassenger() == null)
            clear();

        Vector vector = getPlayer().getLocation().toVector();

        double rotX = getPlayer().getLocation().getYaw();
        double rotY = getPlayer().getLocation().getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double h = Math.cos(Math.toRadians(rotY));

        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));

        UltraCosmetics.getInstance().getEntityUtil().moveDragon(getPlayer(), vector, entity);
    }

    @EventHandler
    public void stopDragonDamage(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EnderDragonPart)
            e = ((EnderDragonPart) e).getParent();
        if (e instanceof EnderDragon && e == entity)
            event.setCancelled(true);

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        if (e instanceof EnderDragonPart) {
            e = ((EnderDragonPart) e).getParent();
        }
        if (e instanceof EnderDragon && e == entity) {
            event.setCancelled(true);

        }
    }
}
