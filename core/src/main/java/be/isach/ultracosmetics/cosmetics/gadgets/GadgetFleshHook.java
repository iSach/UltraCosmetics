package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents an instance of a fleshhook gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetFleshHook extends Gadget implements Listener {

    private ArrayList<Item> items = new ArrayList<>();

    public GadgetFleshHook(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("fleshhook"), ultraCosmetics);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(event.getPlayer());
        if(ultraPlayer != null
         && !ultraPlayer.canBeHitByOtherGadgets()) {
            event.setCancelled(true);
            return;
        }

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
        }
    }

    @Override
    void onRightClick() {
        Item hook = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(XMaterial.TRIPWIRE_HOOK, UltraCosmeticsData.get().getItemNoPickupString()));
        hook.setPickupDelay(0);
        hook.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.5));
        items.add(hook);
    }

    @Override
    public void onUpdate() {
        Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
            Iterator<Item> it = items.iterator();
            while (it.hasNext()) {
                Object pair = it.next();
                if (((Item) pair).isOnGround()) {
                    ((Item) pair).remove();
                    it.remove();
                }
            }
        });
    }

    @Override
    public void onClear() {
        for (Item item : items)
            item.remove();
        HandlerList.unregisterAll(this);
    }
}
