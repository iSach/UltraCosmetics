package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 15/12/15.
 */
public class GadgetFreezeCannon extends Gadget {

    private List<Item> items;
    private List<Item> queue;

    public GadgetFreezeCannon(UUID owner) {
        super(owner, GadgetType.FREEZECANNON);
        if (owner == null) return;
        items = new ArrayList<>();
        queue = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, UltraCosmetics.getInstance());
    }

    @Override
    void onRightClick() {
        Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), new ItemStack(Material.ICE));
        item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(0.9));
        queue.add(item);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if(items.contains(event.getItem())) event.setCancelled(true);
    }

    @Override
    void onUpdate() {
        for (Item item : queue)
            items.add(item);
        queue.clear();
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            Item i = itemIterator.next();
            if (i.isOnGround()) {
                for (Block b : BlockUtils.getBlocksInRadius(i.getLocation(), 4, false))
                    BlockUtils.setToRestore(b, Material.ICE, (byte) 0, 50);
                UtilParticles.display(Particles.FIREWORKS_SPARK, 4d, 3d, 4d, i.getLocation(), 80);
                i.remove();
                itemIterator.remove();
            }
        }
    }

    @Override
    public void onClear() {
        for (Item item : items)
            item.remove();
        for (Item item : queue)
            item.remove();
        queue.clear();
        items.clear();
        items = null;
        queue = null;
        unregisterListeners();
    }

    @Override
    void onLeftClick() {
    }

}
