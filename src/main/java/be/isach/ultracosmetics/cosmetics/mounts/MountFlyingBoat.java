package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityBoat;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftBoat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 17/08/15.
 */
public class MountFlyingBoat extends Mount {

    public MountFlyingBoat(UUID owner) {
        super(owner, MountType.FLYINGBOAT
        );
        if (owner != null)
            Core.registerListener(this);
    }

    @Override
    void onUpdate() {
        if (ent.getPassenger() == null)
            clear();
     
        EntityBoat ec = ((CraftBoat) ent).getHandle();


        Vector vector = getPlayer().getLocation().toVector();

        double rotX = getPlayer().getLocation().getYaw();
        double rotY = getPlayer().getLocation().getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double h = Math.cos(Math.toRadians(rotY));

        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = getPlayer().getLocation().getPitch();
        ec.yaw = getPlayer().getLocation().getYaw() - 180;        
    }

    @EventHandler
    public void stopDragonDamage(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EntityBoat && e == ent)
            event.setCancelled(true);

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        if (e instanceof EntityBoat && e == ent) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBoatBreak(VehicleDestroyEvent event){
    	Entity e = event.getVehicle();
    	if(e == ent){
    		event.setCancelled(true);
    	}
    }
}
