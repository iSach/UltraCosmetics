package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import com.cryptomorin.xseries.XMaterial;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author iSach
 * @since 12-15-2015
 */
public class GadgetFreezeCannon extends Gadget implements Updatable {

    private Set<Item> items = new HashSet<>();

    public GadgetFreezeCannon(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("freezecannon"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), new ItemStack(Material.ICE));
        item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(0.9));
        items.add(item);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPickup(org.bukkit.event.player.PlayerPickupItemEvent event) {
        if (items.contains(event.getItem())) event.setCancelled(true);
    }

    @Override
    public void onUpdate() {
        Iterator<Item> iter = items.iterator();
        Map<Block,XMaterial> updates = new HashMap<>();
        while (iter.hasNext()) {
            Item item = iter.next();
            if (item.isOnGround()) {
                for (Block b : BlockUtils.getBlocksInRadius(item.getLocation(), 4, false)) {
                    updates.put(b, XMaterial.PACKED_ICE);
                }
                Particles.FIREWORKS_SPARK.display(4d, 3d, 4d, item.getLocation(), 80);
                item.remove();
                iter.remove();
            }
        }
        BlockUtils.setToRestore(updates, 50);
    }

    @Override
    public void onClear() {
        for (Item item : items) {
            item.remove();
        }
        items.clear();
    }
}
