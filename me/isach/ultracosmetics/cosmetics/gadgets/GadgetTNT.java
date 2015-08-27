package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.MathUtils;
import me.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 17/08/15.
 */
public class GadgetTNT extends Gadget {

    List<Entity> entities = new ArrayList<>();

    public GadgetTNT(UUID owner) {
        super(Material.TNT, (byte) 0x0, "TNT", "ultracosmetics.gadgets.tnt", 1, owner, GadgetType.TNT);
        Core.registerListener(this);
    }

    @Override
    void onInteractRightClick() {

        TNTPrimed tnt = getPlayer().getWorld().spawn(getPlayer().getLocation().add(0, 2, 0), TNTPrimed.class);
        // vector stuff
        tnt.setFuseTicks(20);
        tnt.setVelocity(getPlayer().getLocation().getDirection().multiply(0.854321));
        entities.add(tnt);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (entities.contains(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (entities.contains(event.getEntity())) {
            event.setCancelled(true);
            UtilParticles.play(event.getEntity().getLocation(), Effect.EXPLOSION_HUGE);
            event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.EXPLODE, 1, 1);

            for (Entity ent : event.getEntity().getNearbyEntities(3, 3, 3)) {
                double dX = event.getEntity().getLocation().getX() - ent.getLocation().getX();
                double dY = event.getEntity().getLocation().getY() - ent.getLocation().getY();
                double dZ = event.getEntity().getLocation().getZ() - ent.getLocation().getZ();
                double yaw = Math.atan2(dZ, dX);
                double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
                double X = Math.sin(pitch) * Math.cos(yaw);
                double Y = Math.sin(pitch) * Math.sin(yaw);
                double Z = Math.cos(pitch);

                Vector vector = new Vector(X, Z, Y);
                MathUtils.applyVelocity(ent, vector.multiply(1.3D).add(new Vector(0, 1.4D, 0)));
            }
            entities.remove(event.getEntity());
        }
    }

    @Override
    void onInteractLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void clear() {

    }
}
