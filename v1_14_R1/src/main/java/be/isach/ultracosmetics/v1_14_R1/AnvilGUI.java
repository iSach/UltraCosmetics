package be.isach.ultracosmetics.v1_14_R1;

import be.isach.ultracosmetics.version.AAnvilGUI;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author RadBuilder
 */
public class AnvilGUI extends AAnvilGUI {
	public AnvilGUI(Player player, AnvilClickEventHandler handler) {
		super(player, handler);
	}
	
	private class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity) {
			// super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
			super(3, entity.inventory);
			this.checkReachable = false;
		}
	}
	
	@Override
	public void open() {
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		AnvilContainer container = new AnvilContainer(p);
		inv = container.getBukkitView().getTopInventory();
		for (AnvilSlot slot : items.keySet())
			inv.setItem(slot.getSlot(), items.get(slot));
		int c = p.nextContainerCounter();
		p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, Containers.ANVIL, new ChatMessage("Repairing")));
		p.activeContainer = container;
		// p.activeContainer.windowId = c;
		p.activeContainer.addSlotListener(p);
	}
}