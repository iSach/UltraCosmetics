package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author iSach
 * @since 12-15-2015
 */
public class GadgetFreezeCannon extends Gadget {
	
	private List<Item> items;
	private List<Item> queue;
	
	public GadgetFreezeCannon(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.FREEZECANNON, ultraCosmetics);
		if (owner == null) return;
		items = new ArrayList<>();
		queue = new ArrayList<>();
	}
	
	@Override
	void onRightClick() {
		Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), new ItemStack(Material.ICE));
		item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(0.9));
		queue.add(item);
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (items.contains(event.getItem())) event.setCancelled(true);
	}
	
	@Override
	public void onUpdate() {
		items.addAll(queue);
		queue.clear();
		Iterator<Item> itemIterator = items.iterator();
		while (itemIterator.hasNext()) {
			Item i = itemIterator.next();
			if (i.isOnGround()) {
				for (Block b : BlockUtils.getBlocksInRadius(i.getLocation(), 4, false))
					BlockUtils.setToRestore(b, Material.PACKED_ICE, (byte) 0, 50);
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
	}
	
	@Override
	void onLeftClick() {
	}
}
