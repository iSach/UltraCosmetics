package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.UltraCosmetics;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public abstract class AAnvilGUI {
    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(1),
        OUTPUT(2);

        private int slot;

        private AnvilSlot(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }

            return null;
        }
    }

    public class AnvilClickEvent {
        private AnvilSlot slot;

        private String name;

        private boolean close = true;
        private boolean destroy = true;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;
        }

        public AnvilSlot getSlot() {
            return slot;
        }

        public String getName() {
            return name;
        }

        public boolean getWillClose() {
            return close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }

    public interface AnvilClickEventHandler {
        public void onAnvilClick(AnvilClickEvent event);
    }

    protected Player player;

    protected AnvilClickEventHandler handler;

    protected Map<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();

    protected Inventory inv;

    protected Listener listener;

    public AAnvilGUI(final Player player, final AnvilClickEventHandler handler) {
        this.player = player;
        this.handler = handler;

        this.listener = new Listener() {

            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {

                    if (event.getInventory().equals(inv)) {
                        event.setCancelled(true);

                        ItemStack item = event.getCurrentItem();
                        int slot = event.getRawSlot();
                        String name = "";

                        if (item != null) {
                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();

                                if (meta.hasDisplayName()) {
                                    name = meta.getDisplayName();
                                }
                            }
                        }

                        if (player.getGameMode() == GameMode.ADVENTURE
                                || player.getGameMode() == GameMode.SURVIVAL
                                && player.getLevel() > 0)
                            Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    player.setLevel(player.getLevel());
                                }
                            }, 5);


                        AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);

                        handler.onAnvilClick(clickEvent);

                        if (clickEvent.getWillClose()) {
                            event.getWhoClicked().closeInventory();
                        }


                        if (clickEvent.getWillDestroy()) {
                            destroy();
                        }
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inv = event.getInventory();
                    if (inv.equals(AAnvilGUI.this.inv)) {
                        inv.clear();
                        destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(getPlayer())) {
                    destroy();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, UltraCosmetics.getInstance());
    }

    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    public abstract void open();

    public void destroy() {
        player = null;
        handler = null;
        items = null;

        HandlerList.unregisterAll(listener);

        listener = null;
    }
}
