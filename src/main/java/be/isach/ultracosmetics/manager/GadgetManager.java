package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 11/11/15.
 */
public class GadgetManager implements Listener {

    static List<Player> playerList = new ArrayList<>();

    public static void openGadgetsMenu(final Player p) {
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

        final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Gadgets"));

        int i = 10;
        for (Gadget g : Core.getGadgets()) {
            if (!g.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replace("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 16) {
                    i += 3;
                } else {
                    i++;
                }
                continue;
            }
            if (!g.getType().isEnabled()) continue;
            if (SettingsManager.getConfig().get("No-Permission.Dont-Show-Item"))
                if (!p.hasPermission(g.getType().getPermission()))
                    continue;
            if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(g.getType().getPermission())) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 16) {
                    i += 3;
                } else {
                    i++;
                }
                continue;
            }

            String lore = null;
            if (SettingsManager.getConfig().get("No-Permission.Show-In-Lore")) {
                lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(g.getType().getPermission()) ? "Yes" : "No")))));
            }
            String toggle = MessageManager.getMessage("Menu.Activate");
            CustomPlayer cp = Core.getCustomPlayer(p);
            if (cp.currentGadget != null && cp.currentGadget.getType() == g.getType())
                toggle = MessageManager.getMessage("Menu.Deactivate");
            ItemStack is = ItemFactory.create(g.getMaterial(), g.getData(), toggle + " " + g.getName());
            if (cp.currentGadget != null && cp.currentGadget.getType() == g.getType())
                is = ItemFactory.addGlow(is);
            if (Core.isAmmoEnabled() && g.getType().requiresAmmo()) {
                ItemMeta itemMeta = is.getItemMeta();
                List<String> loreList = new ArrayList<>();
                if (itemMeta.hasLore())
                    loreList = itemMeta.getLore();
                if (g.showsDescription()) {
                    loreList.add("");
                    for (String s : g.getDescription())
                        loreList.add(s);
                    loreList.add("");
                }
                if (lore != null)
                    loreList.add(lore);
                loreList.add("");
                loreList.add(MessageManager.getMessage("Ammo").replace("%ammo%", "" + Core.getCustomPlayer(p).getAmmo(g.getType().toString().toLowerCase())));
                loreList.add(MessageManager.getMessage("Right-Click-Buy-Ammo"));
                itemMeta.setLore(loreList);
                is.setItemMeta(itemMeta);
            }
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 16) {
                i += 3;
            } else {
                i++;
            }
        }

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

    @EventHandler
    public void gadgetSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Gadgets"))) {
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
                        event.getWhoClicked().closeInventory();
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                        openGadgetsMenu((Player) event.getWhoClicked());
                    } else return;
                    return;
                }
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
                    activateGadgetByType(getGadgetByName(sb.toString()), (Player) event.getWhoClicked());
                    if (cp.currentGadget.getType().requiresAmmo()) {
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
                    activateGadgetByType(getGadgetByName(sb.toString()), (Player) event.getWhoClicked());
                    if (cp.currentGadget != null && Core.isAmmoEnabled() && cp.getAmmo(cp.currentGadget.getType().toString().toLowerCase()) < 1 && cp.currentGadget.getType().requiresAmmo()) {
                        cp.currentGadget.buyAmmo();
                    }
                }

            }
        }
    }

    public static Gadget.GadgetType getGadgetByName(String name) {
        for (Gadget g : Core.getGadgets()) {
            if (g.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return g.getType();
            }
        }
        return null;
    }

    public static void activateGadgetByType(Gadget.GadgetType type, final Player player) {
        if (!player.hasPermission(type.getPermission())) {
            if (!playerList.contains(player)) {
                player.sendMessage(MessageManager.getMessage("No-Permission"));
                playerList.add(player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        playerList.remove(player);
                    }
                }, 1);
            }
            return;
        }
        for (Gadget g : Core.getGadgets()) {
            if (g.getType().isEnabled() && g.getType() == type) {
                Class gadgetClass = g.getClass();

                Class[] cArg = new Class[1];
                cArg[0] = UUID.class;

                UUID uuid = player.getUniqueId();

                try {
                    gadgetClass.getDeclaredConstructor(UUID.class).newInstance(uuid);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
