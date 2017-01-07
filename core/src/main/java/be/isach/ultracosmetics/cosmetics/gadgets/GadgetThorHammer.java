package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetThorHammer extends Gadget implements Listener {

    ArrayList<Item> hammer = new ArrayList<>();
    Vector v;

    public GadgetThorHammer(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.THORHAMMER, ultraCosmetics);
        if (owner != null)
            Bukkit.getPluginManager().registerEvents(this, getUltraCosmetics());
    }

    @Override
    void onRightClick() {
        final Item i = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.IRON_AXE, (byte) 0, MessageManager.getMessage("Gadgets.ThorHammer.name")));
        i.setPickupDelay(0);
        i.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.4));
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), null);
        hammer.add(i);
        v = getPlayer().getEyeLocation().getDirection().multiply(1.4).add(new Vector(0, 1, 0));
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
            @Override
            public void run() {
                i.setVelocity(getPlayer().getEyeLocation().toVector().subtract(i.getLocation().toVector()).multiply(0.2).add(new Vector(0, 0, 0)));
                v = null;
                Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
                    @Override
                    public void run() {
                        if (i.isValid()) {
                            if (UltraCosmeticsData.get().isAmmoEnabled()) {
                                getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getType().getMaterial(), getType().getData(), ChatColor.WHITE + "" + ChatColor.BOLD + getOwner().getAmmo(getType().toString().toLowerCase()) + " " + getType().getName(), ChatColor.BLUE + "Gadget"));
                            } else {
                                getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getType().getMaterial(), getType().getData(), getType().getName(), MessageManager.getMessage("Gadgets.Lore")));
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
                    if (UltraCosmeticsData.get().isAmmoEnabled()) {
                        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getType().getMaterial(), getType().getData(), ChatColor.WHITE + "" + ChatColor.BOLD + getOwner().getAmmo(getType().toString().toLowerCase()) + " " + getType().getName(), ChatColor.BLUE + "Gadget"));
                    } else {
                        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), ItemFactory.create(getType().getMaterial(), getType().getData(), getType().getName(), MessageManager.getMessage("Gadgets.Lore")));
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
    public void onUpdate() {

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
