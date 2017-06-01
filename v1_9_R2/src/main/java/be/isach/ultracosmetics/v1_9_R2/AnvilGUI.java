package be.isach.ultracosmetics.v1_9_R2;

import be.isach.ultracosmetics.version.AAnvilGUI;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.ChatMessage;
import net.minecraft.server.v1_9_R2.ContainerAnvil;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by sacha on 23/08/15.
 */
public class AnvilGUI extends AAnvilGUI {
	public AnvilGUI(Player player, AnvilClickEventHandler handler) {
		super(player, handler);
	}
	
	private class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity) {
			super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
		}
		
		@Override
		public boolean a(EntityHuman entityhuman) {
			return true;
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
		p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
		p.activeContainer = container;
		p.activeContainer.windowId = c;
		p.activeContainer.addSlotListener(p);
	}
}