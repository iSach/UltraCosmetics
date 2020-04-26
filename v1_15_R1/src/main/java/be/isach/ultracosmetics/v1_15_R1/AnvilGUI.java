package be.isach.ultracosmetics.v1_15_R1;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.util.UCMaterial;
import be.isach.ultracosmetics.version.AAnvilGUI;
import be.isach.ultracosmetics.version.IAnvilGUI;
import net.minecraft.server.v1_15_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author wesjd
 */
public class AnvilGUI implements IAnvilGUI {

    /**
     * A state that decides where the anvil GUI is able to be closed by the user
     */
    private final boolean preventClose;
    /**
     * An {@link Consumer} that is called when the anvil GUI is closed
     */
    private final Consumer<Player> closeListener;
    /**
     * An {@link BiFunction} that is called when the {@link Slot#OUTPUT} slot has been clicked
     */
    private final BiFunction<Player, String, AAnvilGUI.Response> completeFunction;
    /**
     * The listener holder class
     */
    private final ListenUp listener = new ListenUp();
    /**
     * The text that will be displayed to the user
     */
    private String text = "";
    /**
     * The ItemStack that is in the {@link Slot#INPUT_LEFT} slot.
     */
    private ItemStack insert;
    /**
     * The container id of the inventory, used for NMS methods
     */
    private int containerId;
    /**
     * The inventory that is used on the Bukkit side of things
     */
    private Inventory inventory;
    /**
     * Represents the state of the inventory being open
     */
    private boolean open;
    private Player player;

    /**
     * Create an AnvilGUI and open it for the player.
     *
     * @param player           The {@link Player} to open the inventory for
     * @param text             What to have the text already set to
     * @param preventClose     Whether to prevent the inventory from closing
     * @param closeListener    A {@link Consumer} when the inventory closes
     * @param completeFunction A {@link BiFunction} that is called when the player clicks the {@link Slot#OUTPUT} slot
     */
    public AnvilGUI(Player player, String text, Boolean preventClose, Consumer<Player> closeListener, BiFunction<Player, String, AAnvilGUI.Response> completeFunction) {
        this.player = player;
        this.text = text;
        this.preventClose = preventClose;
        this.closeListener = closeListener;
        this.completeFunction = completeFunction;
        openInventory();
    }

    private void openInventory() {
        final ItemStack paper = UCMaterial.PAPER.parseItem();
        final ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(" ");
        paper.setItemMeta(paperMeta);
        this.insert = paper;

        final ItemStack paperOut = UCMaterial.PAPER.parseItem();
        final ItemMeta paperOutMeta = paperOut.getItemMeta();
        paperOutMeta.setDisplayName(" ");
        paperOut.setItemMeta(paperOutMeta);

        CraftEventFactory.handleInventoryCloseEvent(toNMS(player));
        toNMS(player).activeContainer = toNMS(player).defaultContainer;

        Bukkit.getPluginManager().registerEvents(listener, UltraCosmeticsData.get().getPlugin());

        final Object container = new AnvilContainer(player, getNextContainerId(player));

        inventory = toBukkitInventory(container);
        inventory.setItem(Slot.INPUT_LEFT, this.insert);

        containerId = getNextContainerId(player);
        toNMS(player).playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, Containers.ANVIL, new ChatMessage("Repair & Name")));
        toNMS(player).activeContainer = (Container) container;
        try { // Change a private field in net.minecraft.server using reflection
            Field field = (Container.class).getDeclaredField("windowId");
            field.setAccessible(true);
            field.set(container, containerId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        ((Container) container).addSlotListener(toNMS(player));
        open = true;
    }

    public void closeInventory() {
        Validate.isTrue(open, "You can't close an inventory that isn't open!");
        open = false;

        CraftEventFactory.handleInventoryCloseEvent(toNMS(player));
        toNMS(player).activeContainer = toNMS(player).defaultContainer;
        toNMS(player).playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));

        HandlerList.unregisterAll(listener);

        if (closeListener != null) {
            closeListener.accept(player);
        }
    }

    private EntityPlayer toNMS(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public int getNextContainerId(Player player) {
        return toNMS(player).nextContainerCounter();
    }

    public Inventory toBukkitInventory(Object container) {
        return ((Container) container).getBukkitView().getTopInventory();
    }

    public static class Slot {

        /**
         * The slot on the far left, where the first input is inserted. An {@link ItemStack} is always inserted
         * here to be renamed
         */
        public static final int INPUT_LEFT = 0;
        /**
         * Not used, but in a real anvil you are able to put the second item you want to combine here
         */
        public static final int INPUT_RIGHT = 1;
        /**
         * The output slot, where an item is put when two items are combined from {@link #INPUT_LEFT} and
         * {@link #INPUT_RIGHT} or {@link #INPUT_LEFT} is renamed
         */
        public static final int OUTPUT = 2;

    }

    private class ListenUp implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getInventory().equals(inventory) && event.getRawSlot() < 3) {
                event.setCancelled(true);
                final Player clicker = (Player) event.getWhoClicked();
                if (event.getRawSlot() == Slot.OUTPUT) {
                    final ItemStack clicked = inventory.getItem(Slot.OUTPUT);
                    if (clicked == null || (clicked.getType() == UCMaterial.AIR.parseMaterial())) return;

                    final AAnvilGUI.Response response = completeFunction.apply(clicker, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : "");
                    if (response.getText() != null) {
                        final ItemMeta meta = clicked.getItemMeta();
                        meta.setDisplayName(response.getText());
                        clicked.setItemMeta(meta);
                        inventory.setItem(Slot.INPUT_LEFT, clicked);
                    } else {
                        closeInventory();
                    }
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (open && event.getInventory().equals(inventory)) {
                closeInventory();
                if (preventClose) {
                    Bukkit.getScheduler().runTask(UltraCosmeticsData.get().getPlugin(), AnvilGUI.this::openInventory);
                }
            }
        }

    }

    private class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(Player player, int containerId) {
            super(containerId, ((CraftPlayer) player).getHandle().inventory,
                    ContainerAccess.at(((CraftWorld) player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
            this.checkReachable = false;
            setTitle(new ChatMessage("Repair & Name"));
        }

        @Override
        public void e() {
            super.e();
            this.levelCost.set(0);
        }
    }
}