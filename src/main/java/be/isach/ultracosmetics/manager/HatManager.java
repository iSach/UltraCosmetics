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

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };
    static List<Player> playerList = new ArrayList<>();

    public static void openMenu(final Player p, int page) {
        page = Math.max(1, Math.min(page, getMaxPagesAmount()));
        final int finalPage = page;
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                int listSize = Hat.enabled().size();
                int slotAmount = 54;
                if (listSize < 22)
                    slotAmount = 54;
                if (listSize < 15)
                    slotAmount = 45;
                if (listSize < 8)
                    slotAmount = 36;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Hats")
                        + " §7§o(" + finalPage + "/" + getMaxPagesAmount() + ")");

                int i = 0;
                int from = 1;
                if (finalPage > 1)
                    from = 21 * (finalPage - 1) + 1;
                int to = 21 * finalPage;
                for (int h = from; h <= to; h++) {
                    if (h > Hat.enabled().size())
                        break;
                    Hat hat = Hat.enabled().get(h - 1);
                    if (!hat.isEnabled()) continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(hat.getPermission()))
                            continue;
                    if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(hat.getPermission())) {
                        Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                        Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                        String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", hat.getName()).replace("&", "§");
                        List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                        String[] array = new String[npLore.size()];
                        npLore.toArray(array);
                        inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array));
                        i++;
                        continue;
                    }
                    String lore = null;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore"))
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
                    inv.setItem(COSMETICS_SLOTS[i], is);
                    i++;
                }

                if (finalPage > 1)
                    inv.setItem(inv.getSize() - 18, ItemFactory.create(ItemFactory.createFromConfig("Categories.Previous-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Previous-Page-Item").getData(), MessageManager.getMessage("Menu.Previous-Page")));
                if (finalPage < getMaxPagesAmount())
                    inv.setItem(inv.getSize() - 10, ItemFactory.create(ItemFactory.createFromConfig("Categories.Next-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Next-Page-Item").getData(), MessageManager.getMessage("Menu.Next-Page")));

                if (Category.HATS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getItemType(), ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getData(), MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - (Category.HATS.hasGoBackArrow() ? 4 : 5), ItemFactory.create(ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getItemType(), ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getData(), MessageManager.getMessage("Clear-Hat")));

                ItemFactory.fillInventory(inv);
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
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    private static int getMaxPagesAmount() {
        int max = 21;
        int i = Hat.enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    public static void equipHat(Hat hat, final Player PLAYER) {
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
        for (Hat hat : Hat.values())
            if (hat.getName().replace(" ", "").equals(name.replace(" ", "")))
                return hat;
        return null;
    }

    private static int getCurrentPage(Player player) {
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Hats"))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus.Hats") + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
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

    @EventHandler
    public void hatSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Hats"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Hats"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    MainMenuManager.openMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Hat"))) {
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentHat != null) {
                        int currentPage = getCurrentPage((Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removeHat();
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    } else return;
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Next-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) + 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Previous-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) - 1);
                    return;
                }
                int currentPage = getCurrentPage((Player) event.getWhoClicked());
                if (Core.closeAfterSelect)
                    event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unequip"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeHat();
                    if (!Core.closeAfterSelect)
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Equip"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeHat();
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replaceFirst(MessageManager.getMessage("Menu.Equip"), "");
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
                    equipHat(getHatByType(sb.toString()), (Player) event.getWhoClicked());
                    if(!Core.closeAfterSelect)
                        openMenu((Player)event.getWhoClicked(), currentPage);
                }

            }
        }
    }

}
