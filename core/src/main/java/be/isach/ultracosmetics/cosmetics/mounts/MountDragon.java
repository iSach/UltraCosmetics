package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.FallDamageManager;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

/**
 * Represents an instance of a enderdragon mount.
 *
 * @author iSach
 * @since 08-17-2015
 */
public class MountDragon extends Mount<EnderDragon> {

    public MountDragon(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("dragon"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
        if (entity.getPassenger() == null) {
            clear();
            return;
        }

        Vector vector = getPlayer().getLocation().toVector();

        double rotX = getPlayer().getLocation().getYaw();
        double rotY = getPlayer().getLocation().getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double h = Math.cos(Math.toRadians(rotY));

        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));

        UltraCosmeticsData.get().getVersionManager().getEntityUtil().moveDragon(getPlayer(), vector, entity);
    }

    @Override
    public void onClear() {
        super.onClear();
        if (!getPlayer().isOnGround()) {
            FallDamageManager.addNoFall(getPlayer());
        }
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

    @Override
    protected void setup() {
    }
}
