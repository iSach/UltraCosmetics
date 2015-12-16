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

/**
 * Created by sacha on 03/08/15.
 */
public class MainMenuManager implements Listener {

    @EventHandler
    public void onInteract(final PlayerInteractEvent EVENT) {
        if (Core.getCustomPlayer(EVENT.getPlayer()).currentTreasureChest != null) {
            EVENT.setCancelled(true);
            return;
        }
        if (EVENT.getItem() != null
                && EVENT.getItem().hasItemMeta()
                && EVENT.getItem().getItemMeta().hasDisplayName()
                && EVENT.getItem().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            EVENT.setCancelled(true);
            Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    openMainMenu(EVENT.getPlayer());
                }
            });
        }
    }

    public static void openMainMenu(final Player PLAYER) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (!PLAYER.hasPermission("ultracosmetics.openmenu")) {
                    PLAYER.sendMessage(MessageManager.getMessage("No-Permission"));
                    PLAYER.closeInventory();
                    return;
                }
                boolean chests = Core.treasureChestsEnabled();
                int add = 0;
                if (chests)
                    add = 9;
                int slotAmount = 27;
                if (Core.enabledCategories.size() > 5)
                    slotAmount = 36;
                if (chests)
                    slotAmount = 45;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Main-Menu"));

                for (int i = 0; i < Core.enabledCategories.size(); i++) {
                    ItemStack is = Core.enabledCategories.get(i).getItemStack().clone();
                    inv.setItem(getMainMenuLayout()[i] + add, is);
                }

                if (chests) {
                    ItemStack chest;

                    if (Core.getCustomPlayer(PLAYER).getKeys() == 0)
                        chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Dont-Have-Key"), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
                    else
                        chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Click-Open-Chest"), "");

                    ItemStack keys = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, MessageManager.getMessage("Treasure-Keys"), "", MessageManager.getMessage("Your-Keys").replace("%keys%", Core.getCustomPlayer(PLAYER).getKeys() + ""), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
                    inv.setItem(5, keys);
                    inv.setItem(3, chest);
                }

                inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Clear-Cosmetics")));
                if (Core.getCustomPlayer(PLAYER).hasGadgetsEnabled())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.INK_SACK, (byte) 0xa, MessageManager.getMessage("Disable-Gadgets")));
                else
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.INK_SACK, (byte) 0x8, MessageManager.getMessage("Enable-Gadgets")));

                ItemFactory.fillInventory(inv);

                Bukkit.getScheduler().runTask(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        PLAYER.openInventory(inv);
                    }
                });

            }
        });
    }

    private static int[] getMainMenuLayout() {
        int[] layout = new int[]{0};
        switch (Core.enabledCategories.size()) {
            case 6:
                layout = new int[]{1, 7, 12, 14, 19, 25};
                break;
            case 5:
                layout = new int[]{9, 11, 13, 15, 17};
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
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Main-Menu"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu")))
                    return;
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))) {
                    GadgetManager.openGadgetsMenu((Player) event.getWhoClicked(), 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                    MountManager.openMountsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))) {
                    PetManager.openPetsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))) {
                    ParticleEffectManager.openParticlesMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Morphs"))) {
                    MorphManager.openMorphsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Cosmetics"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).clear();
                    event.getWhoClicked().closeInventory();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Hats"))) {
                    HatManager.openHatsMenu((Player) event.getWhoClicked(), 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Enable-Gadgets"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).setGadgetsEnabled(true);
                    event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.INK_SACK, (byte) 0xa, MessageManager.getMessage("Disable-Gadgets")));
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Disable-Gadgets"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).setGadgetsEnabled(false);
                    event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.INK_SACK, (byte) 0x8, MessageManager.getMessage("Enable-Gadgets")));
                    return;
                }
            }
        }
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
