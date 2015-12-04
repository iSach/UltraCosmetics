package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.util.ItemFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 11/11/15.
 */
public class GadgetManager implements Listener {

    static List<Player> playerList = new ArrayList<>();

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };

    public static void openGadgetsMenu(final Player p, int page) {
        int listSize = 0;
        for (Gadget g : Core.getGadgets()) {
            if (!g.getType().isEnabled()) continue;
            listSize++;
        }
        int slotAmount = 54;
        if (listSize < 22)
            slotAmount = 54;
        if (listSize < 15)
            slotAmount = 45;
        if (listSize < 8)
            slotAmount = 36;

        final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Gadgets") + " §7§o(" + page + "/" + getMaxPagesAmount() + ")");

        int i = 0;
        int from = 1;
        if (page > 1)
            from = 21 * (page - 1) + 1;
        int to = 21 * page;
        for (int h = from; h <= to; h++) {
            if (h > Core.getGadgets().size())
                break;
            Gadget g = Core.getGadgets().get(h - 1);
            if (!g.getType().isEnabled() && SettingsManager.getConfig().getBoolean("Disabled-Items.Show-Custom-Disabled-Item")) {
                Material material = Material.valueOf(SettingsManager.getConfig().getString("Disabled-Items.Custom-Disabled-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replace("&", "§");
                inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name));
                i++;
                continue;
            }
            if (!g.getType().isEnabled()) continue;
            if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                if (!p.hasPermission(g.getType().getPermission()))
                    continue;
            if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled") && !p.hasPermission(g.getType().getPermission())) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("&", "§");
                inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name));
                i++;
                continue;
            }

            String toggle = MessageManager.getMessage("Menu.Activate");
            CustomPlayer cp = Core.getCustomPlayer(p);
            if (cp.currentGadget != null && cp.currentGadget.getType() == g.getType())
                toggle = MessageManager.getMessage("Menu.Deactivate");
            ItemStack is = ItemFactory.create(g.getMaterial(), g.getData(), toggle + " " + g.getName());
            if (cp.currentGadget != null && cp.currentGadget.getType() == g.getType())
                is = ItemFactory.addGlow(is);
            ItemMeta itemMeta = is.getItemMeta();
            List<String> loreList = new ArrayList<>();
            if (Core.isAmmoEnabled() && g.getType().requiresAmmo()) {
                if (itemMeta.hasLore())
                    loreList = itemMeta.getLore();
                loreList.add("");
                loreList.add(MessageManager.getMessage("Ammo").replace("%ammo%", "" + Core.getCustomPlayer(p).getAmmo(g.getType().toString().toLowerCase())));
                loreList.add(MessageManager.getMessage("Right-Click-Buy-Ammo"));
            }
            if (g.showsDescription()) {
                loreList.add("");
                for (String s : g.getDescription())
                    loreList.add(s);
                loreList.add("");
            }
            if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore"))
                loreList.add(ChatColor.translateAlternateColorCodes('&',
                        String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" +
                                ((p.hasPermission(g.getType().getPermission()) ? "Yes" : "No"))))));
            itemMeta.setLore(loreList);
            is.setItemMeta(itemMeta);
            inv.setItem(COSMETICS_SLOTS[i], is);
            i++;
        }

        if (page > 1)
            inv.setItem(inv.getSize() - 18, ItemFactory.create(Material.ENDER_PEARL, (byte) 0, MessageManager.getMessage("Menu.Previous-Page")));
        if (page < getMaxPagesAmount())
            inv.setItem(inv.getSize() - 10, ItemFactory.create(Material.EYE_OF_ENDER, (byte) 0, MessageManager.getMessage("Menu.Next-Page")));

        if (Category.GADGETS.hasGoBackArrow())
            inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
        inv.setItem(inv.getSize() - (Category.GADGETS.hasGoBackArrow() ? 4 : 5), ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Gadget")));

        ItemFactory.fillInventory(inv);

        Bukkit.getScheduler().runTask(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                p.openInventory(inv);
            }
        });
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    private static int getMaxPagesAmount() {
        int max = 21;
        int i = Core.getGadgets().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    private static int getCurrentPage(Player player) {
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Gadgets"))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus.Gadgets") + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    @EventHandler
    public void gadgetSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Gadgets"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName())
                return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    MainMenuManager.openMainMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Gadget"))) {
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentGadget != null) {
                        int currentPage = getCurrentPage((Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                        openGadgetsMenu((Player) event.getWhoClicked(), currentPage);
                    } else return;
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Next-Page"))) {
                    openGadgetsMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) + 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Previous-Page"))) {
                    openGadgetsMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) - 1);
                    return;
                }
                int currentPage = getCurrentPage((Player) event.getWhoClicked());
                event.getWhoClicked().closeInventory();
                CustomPlayer cp = Core.getCustomPlayer((Player) event.getWhoClicked());
                if (Core.isAmmoEnabled() && event.getAction() == InventoryAction.PICKUP_HALF) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < event.getCurrentItem().getItemMeta().getDisplayName().split(" ").length; i++) {
                        sb.append(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i]);
                        try {
                            if (event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i + 1] != null)
                                sb.append(" ");
                        } catch (Exception exc) {

                        }

                    }
                    if (cp.currentGadget == null)
                        cp.removeGadget();
                    equipGadget(getGadgetByName(sb.toString()), (Player) event.getWhoClicked());
                    if (cp.currentGadget.getType().requiresAmmo()) {
                        cp.currentGadget.lastPage = currentPage;
                        cp.currentGadget.buyAmmo();
                        cp.currentGadget.openGadgetsInvAfterAmmo = true;
                    }
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Deactivate"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Activate"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replace(MessageManager.getMessage("Menu.Activate"), "");
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
                    equipGadget(getGadgetByName(sb.toString()), (Player) event.getWhoClicked());
                    if (cp.currentGadget != null && Core.isAmmoEnabled() && cp.getAmmo(cp.currentGadget.getType().toString().toLowerCase()) < 1 && cp.currentGadget.getType().requiresAmmo()) {
                        cp.currentGadget.lastPage = currentPage;
                        cp.currentGadget.buyAmmo();
                    }
                }

            }
        }
    }

    public static GadgetType getGadgetByName(String name) {
        for (Gadget g : Core.getGadgets()) {
            if (g.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return g.getType();
            }
        }
        return null;
    }

    public static void equipGadget(final GadgetType type, final Player PLAYER) {
        if (!PLAYER.hasPermission(type.getPermission())) {
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
        new Thread() {
            @Override
            public void run() {
                type.equip(PLAYER);
            }
        }.run();
    }

}
