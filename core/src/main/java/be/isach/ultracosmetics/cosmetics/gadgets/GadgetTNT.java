package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
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
        super(owner, GadgetType.TNT);
        UltraCosmetics.getInstance().registerListener(this);
    }

    @Override
    void onRightClick() {

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
    public void onItemFrameBreak(HangingBreakEvent event) {
        for (Entity ent : entities) {
            if(ent.getWorld() != event.getEntity().getWorld()) continue;
            if (ent.getLocation().distance(event.getEntity().getLocation()) < 15)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        for (Entity tnt : entities) {
            if(tnt.getWorld() != event.getVehicle().getWorld()) continue;
            if (tnt.getLocation().distance(event.getVehicle().getLocation()) < 10) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (entities.contains(event.getEntity())) {
            event.setCancelled(true);
            UtilParticles.display(Particles.EXPLOSION_HUGE, event.getEntity().getLocation());
            SoundUtil.playSound(getPlayer(), Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 1.5f);

            for (Entity ent : event.getEntity().getNearbyEntities(3, 3, 3)) {
                if (ent instanceof Creature || ent instanceof Player) {
                    double dX = event.getEntity().getLocation().getX() - ent.getLocation().getX();
                    double dY = event.getEntity().getLocation().getY() - ent.getLocation().getY();
                    double dZ = event.getEntity().getLocation().getZ() - ent.getLocation().getZ();
                    double yaw = Math.atan2(dZ, dX);
                    double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
                    double X = Math.sin(pitch) * Math.cos(yaw);
                    double Y = Math.sin(pitch) * Math.sin(yaw);
                    double Z = Math.cos(pitch);

                    Vector vector = new Vector(X, Z, Y);
                    if (affectPlayers)
                        MathUtils.applyVelocity(ent, vector.multiply(1.3D).add(new Vector(0, 1.4D, 0)));
                }
            }
            entities.remove(event.getEntity());
        }
    }

    @Override
    void onLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {
        for (Entity ent : entities)
            ent.remove();
        HandlerList.unregisterAll(this);
    }
}
