package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetThorHammer extends Gadget implements Listener {

    ArrayList<Item> hammer = new ArrayList<>();
    Vector v;

    public GadgetThorHammer(UUID owner) {
        super(owner, GadgetType.THORHAMMER);
        if (owner != null)
            Bukkit.getPluginManager().registerEvents(this, UltraCosmetics.getInstance());
    }

    @Override
    void onRightClick() {
        final Item i = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.IRON_AXE, (byte) 0, MessageManager.getMessage("Gadgets.ThorHammer.name")));
        i.setPickupDelay(0);
        i.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.4));
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), null);
        hammer.add(i);
        v = getPlayer().getEyeLocation().getDirection().multiply(1.4).add(new Vector(0, 1, 0));
        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                i.setVelocity(getPlayer().getEyeLocation().toVector().subtract(i.getLocation().toVector()).multiply(0.2).add(new Vector(0, 0, 0)));
                v = null;
                Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (i.isValid()) {
                            if (UltraCosmetics.getInstance().isAmmoEnabled()) {
                                getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getMaterial(), getData(), "§f§l" + UltraCosmetics.getCustomPlayer(getPlayer()).getAmmo(getType().toString().toLowerCase()) + " " + getName(), "§9Gadget"));
                            } else {
                                getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getMaterial(), getData(), getName(), MessageManager.getMessage("Gadgets.Lore")));
                            }
                            i.remove();
                        }
                    }
                }, 40);
            }
        }, 20);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (hammer.contains(event.getItem())) {
            event.setCancelled(true);
            if (event.getPlayer() == getPlayer()) {
                if (event.getItem().getTicksLived() > 5) {
                    if (UltraCosmetics.getInstance().isAmmoEnabled()) {
                        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getMaterial(), getData(), "§f§l" + UltraCosmetics.getCustomPlayer(getPlayer()).getAmmo(getType().toString().toLowerCase()) + " " + getName(), "§9Gadget"));
                    } else {
                        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getMaterial(), getData(), getName(), MessageManager.getMessage("Gadgets.Lore")));
                    }
                    hammer.remove(event.getItem());
                    event.getItem().remove();
                }
            } else {
                if (v != null)
                    if (affectPlayers)
                        MathUtils.applyVelocity(event.getPlayer(), v);
            }
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
        for (Item i : hammer)
            i.remove();
        hammer.clear();
        v = null;
        HandlerList.unregisterAll(this);
    }
}
