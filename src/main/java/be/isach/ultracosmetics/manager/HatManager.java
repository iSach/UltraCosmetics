package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.util.ItemFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 11/11/15.
 */
public class HatManager implements Listener {

    static List<Player> playerList = new ArrayList<>();

    public static void openHatsMenu(final Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                int listSize = 0;
                for (Hat hat : Core.getHats()) {
                    if (!hat.isEnabled()) continue;
                    listSize++;
                }
                int slotAmount = 54;
                if (listSize < 22)
                    slotAmount = 54;
                if (listSize < 15)
                    slotAmount = 45;
                if (listSize < 8)
                    slotAmount = 36;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Hats"));

                int i = 1;
                for (Hat hat : Core.getHats()) {
                    if (!hat.isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
                        Material material = Material.valueOf((String) SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Type"));
                        Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                        String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replace("&", "§");
                        inv.setItem(i, ItemFactory.create(material, data, name));
                        if (i == 25 || i == 34 || i == 43 || i == 16 || i == 5) {
                            i += 3;
                        } else {
                            i++;
                        }
                        continue;
                    }
                    if (!hat.isEnabled()) continue;
                    if (SettingsManager.getConfig().get("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(hat.getPermission()))
                            continue;
                    if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(hat.getPermission())) {
                        Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                        Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                        String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("&", "§");
                        inv.setItem(i, ItemFactory.create(material, data, name));
                        if (i == 25 || i == 34 || i == 43 || i == 16 || i == 5) {
                            i += 3;
                        } else {
                            i++;
                        }
                        continue;
                    }
                    String lore = null;
                    if (SettingsManager.getConfig().get("No-Permission.Show-In-Lore"))
                        lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(hat.getPermission()) ? "Yes" : "No")))));
                    String toggle = MessageManager.getMessage("Menu.Equip");
                    CustomPlayer cp = Core.getCustomPlayer(p);
                    if (cp.currentHat != null && cp.currentHat == hat)
                        toggle = MessageManager.getMessage("Menu.Unequip");
                    ItemStack is = hat.getItemStack().clone();
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(toggle + " " + hat.getName());
                    is.setItemMeta(im);
                    if (cp.currentHat != null && cp.currentHat == hat)
                        is = ItemFactory.addGlow(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> loreList = new ArrayList<>();
                    if (hat.showsDescription()) {
                        loreList.add("");
                        for (String s : hat.getDescription())
                            loreList.add(s);
                        loreList.add("");
                    }
                    if (lore != null)
                        loreList.add(lore);
                    itemMeta.setLore(loreList);
                    is.setItemMeta(itemMeta);
                    inv.setItem(i, is);
                    if (i == 25 || i == 34 || i == 43 || i == 16 || i == 7) {

                        i += 3;
                    } else {
                        i++;
                    }
                }

                if (Category.HATS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - (Category.HATS.hasGoBackArrow() ? 4 : 5), ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Hat")));

                Bukkit.getScheduler().runTask(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        p.openInventory(inv);
                    }
                });
            }
        });
    }

    /**
     * Cancel players from dropping the hat in their inventory.
     *
     * @param event
     */
    @EventHandler
    public void cancelHatDropping(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack().clone();
        if (item != null
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals("§8§oHat")) {
            event.getItemDrop().remove();
            event.getPlayer().closeInventory();
            event.getPlayer().updateInventory();
        }
    }

    /**
     * Cancel players from removing, picking the hat in their inventory.
     *
     * @param EVENT
     */
    @EventHandler
    public void cancelHatClick(final InventoryClickEvent EVENT) {
        if (EVENT.getCurrentItem() != null
                && EVENT.getCurrentItem().hasItemMeta()
                && EVENT.getCurrentItem().getItemMeta().hasDisplayName()
                && EVENT.getCurrentItem().getItemMeta().getDisplayName().equals("§8§oHat")) {
            EVENT.setCancelled(true);
            EVENT.setResult(Event.Result.DENY);
            EVENT.getWhoClicked().closeInventory();
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    for (ItemStack itemStack : EVENT.getWhoClicked().getInventory().getContents()) {
                        if (itemStack != null
                                && itemStack.hasItemMeta()
                                && itemStack.getItemMeta().hasDisplayName()
                                && itemStack.getItemMeta().getDisplayName().equals("§8§oHat")
                                && itemStack != EVENT.getWhoClicked().getInventory().getHelmet())
                            EVENT.getWhoClicked().getInventory().remove(itemStack);
                    }
                }
            }, 1);
        }
    }

    public static void activateHat(Hat hat, final Player PLAYER) {
        if (!PLAYER.hasPermission(hat.getPermission())) {
            if (!playerList.contains(PLAYER)) {
                PLAYER.sendMessage(MessageManager.getMessage("No-Permission"));
                playerList.add(PLAYER);
                Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        playerList.remove(PLAYER);
                    }
                }, 1);
            }
            return;
        }
        Core.getCustomPlayer(PLAYER).setHat(hat);
    }

    public static Hat getHatByType(String name) {
        for (Hat hat : Core.getHats()) {
            if (hat.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return hat;
            }
        }
        return null;
    }

    @EventHandler
    public void hatSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Hats"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Hats"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    MainMenuManager.openMainMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Hat"))) {
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentHat != null) {
                        event.getWhoClicked().closeInventory();
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removeHat();
                        openHatsMenu((Player) event.getWhoClicked());
                    } else return;
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unequip"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeHat();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Equip"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeHat();
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replace(MessageManager.getMessage("Menu.Equip"), "");
                    int j = name.split(" ").length;
                    if (name.contains("("))
                        j--;
                    for (int i = 1; i < j; i++) {
                        sb.append(name.split(" ")[i]);
                        try {
                            if (event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i + 1] != null)
                                sb.append(" ");
                        } catch (Exception exc) {

                        }
                    }
                    activateHat(getHatByType(sb.toString()), (Player) event.getWhoClicked());
                }

            }
        }
    }

}
