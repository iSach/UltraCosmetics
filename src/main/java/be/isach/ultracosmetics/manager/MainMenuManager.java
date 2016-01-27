package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sacha on 03/08/15.
 */
public class MainMenuManager implements Listener {

    /**
     * Runs the task for pets following players
     */
    private ExecutorService asyncMenuSelectionService;

    public MainMenuManager() {
        asyncMenuSelectionService = Executors.newSingleThreadExecutor();
    }

    public void dispose() {
        asyncMenuSelectionService.shutdown();
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (Core.getCustomPlayer(event.getPlayer()).currentTreasureChest != null) {
            event.setCancelled(true);
            return;
        }
        if (event.getItem() != null
                && event.getItem().hasItemMeta()
                && event.getItem().getItemMeta().hasDisplayName()
                && event.getItem().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    openMenu(event.getPlayer());
                }
            });
        }
    }

    public static void openMenu(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (!player.hasPermission("ultracosmetics.openmenu")) {
                    player.sendMessage(MessageManager.getMessage("No-Permission"));
                    player.closeInventory();
                    return;
                }
                boolean chests = Core.treasureChestsEnabled();
                int add = 9;
                int slotAmount = 45;
                int d = Core.enabledCategories.size();
                if (d < 5) {
                    slotAmount -= 18;
                    add -= 9;
                }

                if (!chests && Core.enabledCategories.size() == 1) {
                    switch (Core.enabledCategories.get(0)) {
                        case GADGETS:
                            GadgetManager.openMenu(player, 1);
                            break;
                        case MORPHS:
                            MorphManager.openMenu(player, 1);
                            break;
                        case HATS:
                            HatManager.openMenu(player, 1);
                            break;
                        case PETS:
                            PetManager.openMenu(player, 1);
                            break;
                        case EFFECTS:
                            ParticleEffectManager.openMenu(player, 1);
                            break;
                        case MOUNTS:
                            MountManager.openMenu(player, 1);
                            break;
                        case SUITS:
                            SuitManager.openMenu(player, 1);
                            break;
                    }
                    return;
                }

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Main-Menu"));

                for (int i = 0; i < Core.enabledCategories.size(); i++) {
                    ItemStack is = Core.enabledCategories.get(i).getItemStack().clone();
                    inv.setItem(getMainMenuLayout()[i] + add, is);
                }

                if (chests) {
                    ItemStack chest;

                    if (Core.getCustomPlayer(player).getKeys() == 0)
                        chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Dont-Have-Key"), Core.vaultLoaded ?
                                "" : null, Core.vaultLoaded ? MessageManager.getMessage("Click-Buy-Key") : null, Core.vaultLoaded ? "" : null);
                    else
                        chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Click-Open-Chest"), "");

                    ItemStack keys = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, MessageManager.getMessage("Treasure-Keys"), "",
                            MessageManager.getMessage("Your-Keys").replace("%keys%", Core.getCustomPlayer(player).getKeys() + ""), Core.vaultLoaded ?
                                    "" : null, Core.vaultLoaded ? MessageManager.getMessage("Click-Buy-Key") : null, Core.vaultLoaded ? "" : null);
                    inv.setItem(5, keys);
                    inv.setItem(3, chest);
                }

                inv.setItem(inv.getSize() - 5, ItemFactory.create(ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getItemType(), ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getData(), MessageManager.getMessage("Clear-Cosmetics")));

                ItemFactory.fillInventory(inv);

                Bukkit.getScheduler().runTask(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        player.openInventory(inv);
                    }
                });

            }
        });
    }

    private static int[] getMainMenuLayout() {
        int[] layout = new int[]{0};
        switch (Core.enabledCategories.size()) {
            case 7:
                layout = new int[]{1, 4, 7, 19, 21, 23, 25};
                break;
            case 6:
                layout = new int[]{1, 4, 7, 19, 22, 25};
                break;
            case 5:
                layout = new int[]{1, 7, 13, 20, 24};
                break;
            case 4:
                layout = new int[]{10, 12, 14, 16};
                break;
            case 3:
                layout = new int[]{11, 13, 15};
                break;
            case 2:
                layout = new int[]{12, 14};
                break;
            case 1:
                layout = new int[]{13};
                break;
        }
        return layout;
    }

    @EventHandler
    public void mainMenuSelection(final InventoryClickEvent event) {
        asyncMenuSelectionService.submit(new Runnable() {
            @Override
            public void run() {
                if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Main-Menu"))) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                            || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
                    if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu")))
                            return;
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))) {
                            GadgetManager.openMenu((Player) event.getWhoClicked(), 1);
                            return;
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                            MountManager.openMenu((Player) event.getWhoClicked(), 1);
                            return;
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))) {
                            PetManager.openMenu((Player) event.getWhoClicked(), 1);
                            return;
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))) {
                            ParticleEffectManager.openMenu((Player) event.getWhoClicked(), 1);
                            return;
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Morphs"))) {
                            MorphManager.openMenu((Player) event.getWhoClicked(), 1);
                            return;
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Suits"))) {
                            SuitManager.openMenu((Player) event.getWhoClicked(), 1);
                            return;
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Cosmetics"))) {
                            Core.getCustomPlayer((Player) event.getWhoClicked()).clear();
                            event.getWhoClicked().closeInventory();
                            return;
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Hats"))) {
                            HatManager.openMenu((Player) event.getWhoClicked(), 1);
                            return;
                        }
                    }
                }
            }
        });
    }

    @EventHandler
    public void onInventoryMoveItem(PlayerPickupItemEvent event) {
        try {
            if (event.getItem().getItemStack().hasItemMeta()
                    && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                    && UUID.fromString(event.getItem().getItemStack().getItemMeta().getDisplayName()) != null) {
                event.setCancelled(true);
            }
        } catch (Exception exception) {
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryPickupItemEvent event) {
        try {
            if (event.getInventory() != null
                    && event.getInventory().getType() == InventoryType.HOPPER) {
                if (event.getItem().getItemStack().hasItemMeta()
                        && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                        && UUID.fromString(event.getItem().getItemStack().getItemMeta().getDisplayName()) != null) {
                    event.setCancelled(true);
                }
            }
        } catch (Exception exception) {
        }
    }


}
