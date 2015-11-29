package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetFleshHook extends Gadget implements Listener {

    private ArrayList<Item> items = new ArrayList<>();

    public GadgetFleshHook(UUID owner) {
        super(Material.TRIPWIRE_HOOK, (byte) 0x0, "FleshHook", "ultracosmetics.gadgets.fleshhook", 2, owner, GadgetType.FLESH_HOOK, "&7&oMake new friends by throwing a hook\n&7&ointo their face and pulling them\n&7&otowards you!");
        Core.registerListener(this);
    }

    @org.bukkit.event.EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (items.contains(event.getItem())) {
            event.setCancelled(true);
            if (event.getPlayer().getName().equals(getPlayer().getName())) {
                return;
            }
            items.remove(event.getItem());
            event.getItem().remove();
            final Player HIT = event.getPlayer();
            HIT.playEffect(EntityEffect.HURT);
            Player hitter = getPlayer();
            double dX = HIT.getLocation().getX() - hitter.getLocation().getX();
            double dY = HIT.getLocation().getY() - hitter.getLocation().getY();
            double dZ = HIT.getLocation().getZ() - hitter.getLocation().getZ();
            double yaw = Math.atan2(dZ, dX);
            double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
            double X = Math.sin(pitch) * Math.cos(yaw);
            double Y = Math.sin(pitch) * Math.sin(yaw);
            double Z = Math.cos(pitch);

            Vector vector = new Vector(X, Z, Y);
            if (affectPlayers)
                MathUtils.applyVelocity(HIT, vector.multiply(2.5D).add(new Vector(0D, 1.45D, 0D)));
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Core.noFallDamageEntities.add(HIT);
                }
            }, 2);
        }
    }

    @Override
    void onInteractRightClick() {
        Item hook = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, UUID.randomUUID().toString()));
        hook.setPickupDelay(0);
        hook.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.5));
        items.add(hook);
    }

    @Override
    void onInteractLeftClick() {
    }

    @Override
    void onUpdate() {
        Iterator it = items.iterator();
        while (it.hasNext()) {
            Object pair = it.next();
            if (((Item) pair).isOnGround()) {
                ((Item) pair).remove();
                it.remove();
            }
        }
    }

    @Override
    public void onClear() {
        for (Item item : items)
            item.remove();
        HandlerList.unregisterAll(this);
    }

}
