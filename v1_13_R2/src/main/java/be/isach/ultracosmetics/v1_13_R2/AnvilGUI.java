package be.isach.ultracosmetics.v1_13_R2;

import be.isach.ultracosmetics.version.AAnvilGUI;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
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
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
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
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
        p.activeContainer = container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);
    }
}