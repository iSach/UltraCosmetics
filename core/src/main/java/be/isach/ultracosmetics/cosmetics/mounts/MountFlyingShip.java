package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Matthew on 23/01/16.
 * Copy from iSach's Dragon Mount
 */
public class MountFlyingShip extends Mount {

    long nextAllowTime = 0;
    Entity currentboom = null;
    //ArmorStand nameTag = null;

    public MountFlyingShip(UUID owner) {
        super(owner, MountType.FLYINGSHIP);
        if (owner != null)
            UltraCosmetics.getInstance().registerListener(this);
        //  spawnNameTag();
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

        UltraCosmetics.getInstance().getEntityUtil().moveShip(getPlayer(), entity, vector);

        if (currentboom != null) {
            if (currentboom.isDead()) {
                currentboom = null;
                return;
            }
            SoundUtil.playSound(getPlayer(), Sounds.NOTE_STICKS, 1.0f, 1.0f);
            if (currentboom.isOnGround()) {
                Location l = currentboom.getLocation().clone();
                for (Entity i : currentboom.getNearbyEntities(3, 3, 3)) {
                    double dX = i.getLocation().getX() - currentboom.getLocation().getX();
                    double dY = i.getLocation().getY() - currentboom.getLocation().getY();
                    double dZ = i.getLocation().getZ() - currentboom.getLocation().getZ();
                    double yaw = Math.atan2(dZ, dX);
                    double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
                    double X = Math.sin(pitch) * Math.cos(yaw);
                    double Y = Math.sin(pitch) * Math.sin(yaw);
                    double Z = Math.cos(pitch);
                    MathUtils.applyVelocity(i, new Vector(X, Z, Y).multiply(1.3D).add(new Vector(0, 1.4D, 0)));
                }
                UtilParticles.display(Particles.EXPLOSION_HUGE, l);
                SoundUtil.playSound(l, Sounds.EXPLODE);
                currentboom.remove();
                currentboom = null;
            }
        }
    }

    /*
        private void spawnNameTag(){
             nameTag = (ArmorStand) ent.getWorld().spawnEntity(ent.getLocation(), EntityType.ARMOR_STAND);
             nameTag.setVisible(false);
             nameTag.setSmall(true);
             nameTag.setCustomName(getType().getName(getPlayer()));
             nameTag.setCustomNameVisible(true);
             //hide name of ent
             ent.setCustomNameVisible(false);
             nameTag.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(Core.getInstance(),"C_AD_ArmorStand"));
             //getPlayer().setPassenger(nameTag);
        }
        */
    @EventHandler
    public void stopBoatDamage(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (e == entity)
            event.setCancelled(true);

    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        if (event.getPlayer() != getPlayer()) {
            return;
        }
        if (event.getPlayer().getVehicle() == null || event.getPlayer().getVehicle() != entity) {
            return;
        }

        if (System.currentTimeMillis() < nextAllowTime) {
            SoundUtil.playSound(getPlayer().getLocation(), Sounds.ITEM_PICKUP, 1.0f, 1.0f);
            return;
        }

        SoundUtil.playSound(getPlayer(), Sounds.NOTE_STICKS, 1.0f, 1.0f);
        nextAllowTime = System.currentTimeMillis() + 10000;
        currentboom = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), EntityType.PRIMED_TNT);
        currentboom.setCustomName(ChatColor.RED + ChatColor.BOLD.toString() + "!!!!!!!");
        currentboom.setCustomNameVisible(true);

        if (currentboom instanceof LivingEntity) {
            ((LivingEntity) currentboom).setNoDamageTicks(-1);
            if (currentboom instanceof Animals) {
                ((Animals) currentboom).setBreed(false);
            }
        }


    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        if (e == entity) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBoatBreak(VehicleDestroyEvent event) {
        Entity e = event.getVehicle();
        if (e == entity) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onClear() {
        if (currentboom != null) {
            currentboom.remove();
        }
        /*
        if(owner != null){
    		nameTag.getVehicle().eject();
    	}
    	try{
    		nameTag.remove();
    	}catch(Exception e){
    		
    	}
    	*/
    }

}
