package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };
    static List<Player> playerList = new ArrayList<>();

    public static void openMenu(final Player p, int page) {
        page = Math.max(1, Math.min(page, getMaxPagesAmount()));

        int listSize = GadgetType.enabled().size();
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
            if (h > GadgetType.enabled().size())
                break;
            GadgetType g = GadgetType.enabled().get(h - 1);
            if (!g.isEnabled()) continue;
            if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                if (!p.hasPermission(g.getPermission()))
                    continue;
            if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled") && !p.hasPermission(g.getPermission())) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", g.getName()).replace("&", "§");
                List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                String[] array = new String[npLore.size()];
                npLore.toArray(array);
                inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array));
                i++;
                continue;
            }

            String toggle = MessageManager.getMessage("Menu.Activate");
            CustomPlayer cp = UltraCosmetics.getCustomPlayer(p);
            if (cp.currentGadget != null && cp.currentGadget.getType() == g)
                toggle = MessageManager.getMessage("Menu.Deactivate");
            ItemStack is = ItemFactory.create(g.getMaterial(), g.getData(), toggle + " " + g.getName());
            if (cp.currentGadget != null && cp.currentGadget.getType() == g)
                is = ItemFactory.addGlow(is);
            ItemMeta itemMeta = is.getItemMeta();
            List<String> loreList = new ArrayList<>();
            if (UltraCosmetics.getInstance().isAmmoEnabled() && g.requiresAmmo()) {
                if (itemMeta.hasLore())
                    loreList = itemMeta.getLore();
                loreList.add("");
                loreList.add(MessageManager.getMessage("Ammo").replace("%ammo%", "" + UltraCosmetics.getCustomPlayer(p).getAmmo(g.toString().toLowerCase())));
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
                                ((p.hasPermission(g.getPermission()) ? "Yes" : "No"))))));
            itemMeta.setLore(loreList);
            is.setItemMeta(itemMeta);
            inv.setItem(COSMETICS_SLOTS[i], is);
            i++;
        }

        if (page > 1)
            inv.setItem(inv.getSize() - 18, ItemFactory.create(ItemFactory.createFromConfig("Categories.Previous-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Previous-Page-Item").getData(), MessageManager.getMessage("Menu.Previous-Page")));
        if (page < getMaxPagesAmount())
            inv.setItem(inv.getSize() - 10, ItemFactory.create(ItemFactory.createFromConfig("Categories.Next-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Next-Page-Item").getData(), MessageManager.getMessage("Menu.Next-Page")));

        if (Category.GADGETS.hasGoBackArrow())
            inv.setItem(inv.getSize() - 6, ItemFactory.create(ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getItemType(), ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getData(), MessageManager.getMessage("Menu.Main-Menu")));
        inv.setItem(inv.getSize() - 4, ItemFactory.create(ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getItemType(), ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getData(), MessageManager.getMessage("Clear-Gadget")));

        if (UltraCosmetics.getCustomPlayer(p).hasGadgetsEnabled())
            inv.setItem(inv.getSize() - (Category.GADGETS.hasGoBackArrow() ? 5 : 6), ItemFactory.create(ItemFactory.createFromConfig("Categories.Gadgets-Item.When-Enabled").getItemType(), ItemFactory.createFromConfig("Categories.Gadgets-Item.When-Enabled").getData(), MessageManager.getMessage("Disable-Gadgets")));
        else
            inv.setItem(inv.getSize() - (Category.GADGETS.hasGoBackArrow() ? 5 : 6), ItemFactory.create(ItemFactory.createFromConfig("Categories.Gadgets-Item.When-Disabled").getItemType(), ItemFactory.createFromConfig("Categories.Gadgets-Item.When-Disabled").getData(), MessageManager.getMessage("Enable-Gadgets")));

        ItemFactory.fillInventory(inv);

        Bukkit.getScheduler().runTask(UltraCosmetics.getInstance(), new Runnable() {
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
        int i = GadgetType.enabled().size();
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

    public static GadgetType getGadgetByName(String name) {
        for (GadgetType type : GadgetType.values())
            if (type.getName().replace(" ", "").equals(name.replace(" ", "")))
                return type;
        return null;
    }

    public static void equipGadget(final GadgetType type, final Player PLAYER) {
        if (!PLAYER.hasPermission(type.getPermission())) {
            if (!playerList.contains(PLAYER)) {
                PLAYER.sendMessage(MessageManager.getMessage("No-Permission"));
                playerList.add(PLAYER);
                Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
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
                    UltraCosmetics.openMainMenuFromOther((Player)event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Gadget"))) {
                    if (UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).currentGadget != null) {
                        int currentPage = getCurrentPage((Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                        UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    } else return;
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Next-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) + 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Previous-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) - 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Enable-Gadgets"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).setGadgetsEnabled(true);
                    event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.INK_SACK, (byte) 0xa, MessageManager.getMessage("Disable-Gadgets")));
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Disable-Gadgets"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).setGadgetsEnabled(false);
                    event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.INK_SACK, (byte) 0x8, MessageManager.getMessage("Enable-Gadgets")));
                    return;
                }
                int currentPage = getCurrentPage((Player) event.getWhoClicked());
                if (UltraCosmetics.closeAfterSelect)
                    event.getWhoClicked().closeInventory();
                CustomPlayer cp = UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked());
                if (UltraCosmetics.getInstance().isAmmoEnabled() && event.getAction() == InventoryAction.PICKUP_HALF) {
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
                        cp.currentGadget.openAmmoPurchaseMenu();
                        cp.currentGadget.openGadgetsInvAfterAmmo = true;
                    }
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Deactivate"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                    if (!UltraCosmetics.closeAfterSelect)
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Activate"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replaceFirst(MessageManager.getMessage("Menu.Activate"), "");
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
                    if (cp.currentGadget != null && UltraCosmetics.getInstance().isAmmoEnabled() && cp.getAmmo(cp.currentGadget.getType().toString().toLowerCase()) < 1 && cp.currentGadget.getType().requiresAmmo()) {
                        cp.currentGadget.lastPage = currentPage;
                        cp.currentGadget.openAmmoPurchaseMenu();
                    } else {
                        if (!UltraCosmetics.closeAfterSelect)
                            openMenu((Player) event.getWhoClicked(), currentPage);
                    }
                }

            }
        }
    }

}
