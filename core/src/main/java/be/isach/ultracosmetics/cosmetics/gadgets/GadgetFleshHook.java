package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents an instance of a fleshhook gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetFleshHook extends Gadget implements PlayerAffectingCosmetic, Updatable {

    private Set<Item> items = new HashSet<>();

    public GadgetFleshHook(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("fleshhook"), ultraCosmetics);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onItemPickup(org.bukkit.event.player.PlayerPickupItemEvent event) {
        UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(event.getPlayer());
        if(ultraPlayer != null && !ultraPlayer.canBeHitByOtherGadgets()) {
            event.setCancelled(true);
            return;
        }

        if (items.contains(event.getItem())) {
            event.setCancelled(true);
            if (event.getPlayer() == getPlayer() || !canAffect(event.getPlayer())) {
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
            MathUtils.applyVelocity(HIT, vector.multiply(2.5D).add(new Vector(0D, 1.45D, 0D)));
        }
    }

    @Override
    void onRightClick() {
        items.add(ItemFactory.createUnpickableItemDirectional(XMaterial.TRIPWIRE_HOOK, getPlayer(), 1.5));
    }

    @Override
    public void onUpdate() {
        Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
            Iterator<Item> it = items.iterator();
            while (it.hasNext()) {
                Item pair = it.next();
                if (pair.isOnGround()) {
                    pair.remove();
                    it.remove();
                }
            }
        });
    }

    @Override
    public void onClear() {
        for (Item item : items) {
            item.remove();
        }
        items.clear();
    }
}
