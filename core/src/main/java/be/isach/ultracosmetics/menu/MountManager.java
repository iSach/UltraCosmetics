package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 11/11/15.
 */
public class MountManager implements Listener {

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
        Bukkit.getScheduler().runTaskAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                int listSize = MountType.enabled().size();
                int slotAmount = 54;
                if (listSize < 22)
                    slotAmount = 54;
                if (listSize < 15)
                    slotAmount = 45;
                if (listSize < 8)
                    slotAmount = 36;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Mounts") + " §7§o(" + finalPage + "/" + getMaxPagesAmount() + ")");

                int i = 0;
                int from = 1;
                if (finalPage > 1)
                    from = 21 * (finalPage - 1) + 1;
                int to = 21 * finalPage;
                for (int h = from; h <= to; h++) {
                    if (h > MountType.enabled().size())
                        break;
                    MountType mountType = MountType.enabled().get(h - 1);
                    if (!mountType.isEnabled()) continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(mountType.getPermission()))
                            continue;
                    if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(mountType.getPermission())) {
                        Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                        Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                        String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", mountType.getMenuName()).replace("&", "§");
                        List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                        String[] array = new String[npLore.size()];
                        npLore.toArray(array);
                        inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array));
                        i++;
                        continue;
                    }
                    String lore = null;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore"))
                        lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(mountType.getPermission()) ? "Yes" : "No")))));
                    String toggle = MessageManager.getMessage("Menu.Spawn");
                    UltraPlayer cp = UltraCosmetics.getCustomPlayer(p);
                    if (cp.currentMount != null && cp.currentMount.getType() == mountType)
                        toggle = MessageManager.getMessage("Menu.Despawn");
                    ItemStack is = ItemFactory.create(mountType.getMaterial(), mountType.getData(), toggle + " " + mountType.getMenuName());
                    if (cp.currentMount != null && cp.currentMount.getType() == mountType)
                        is = ItemFactory.addGlow(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> loreList = new ArrayList<>();
                    if (mountType.showsDescription()) {
                        loreList.add("");
                        for (String s : mountType.getDescription())
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

                if (Category.MOUNTS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getItemType(), ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getData(), MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - (Category.MOUNTS.hasGoBackArrow() ? 4 : 5), ItemFactory.create(ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getItemType(), ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getData(), MessageManager.getMessage("Clear-Mount")));

                ItemFactory.fillInventory(inv);

                Bukkit.getScheduler().runTask(UltraCosmetics.getInstance(), new Runnable() {
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
        int i = MountType.enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    private static int getCurrentPage(Player player) {
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Mounts"))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus.Mounts") + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    public static void equipMount(final MountType TYPE, final Player PLAYER) {
        if (!PLAYER.hasPermission(TYPE.getPermission())) {
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
                TYPE.equip(PLAYER);
            }
        }.run();
    }

    /**
     * Get a Mount (MountType) from the name.
     *
     * @param name The name in menu.
     * @return The MountType found from the given name.
     */
    public static MountType getMountType(String name) {
        for (MountType type : MountType.values()) {
            if (type.getMenuName().replace(" ", "").equals(name.replace(" ", ""))) {
                return type;
            }
        }
        return null;
    }

    @EventHandler
    public void mountsSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Mounts"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    UltraCosmetics.openMainMenuFromOther((Player)event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Mount"))) {
                    if (UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).currentMount != null) {
                        int currentPage = getCurrentPage((Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                        UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    } else return;
                    return;
                }
                int currentPage = getCurrentPage((Player) event.getWhoClicked());
                if (UltraCosmetics.closeAfterSelect)
                    event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Despawn"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                    if (!UltraCosmetics.closeAfterSelect)
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Next-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) + 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Previous-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) - 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Spawn"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replaceFirst(MessageManager.getMessage("Menu.Spawn"), "");
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
                    equipMount(getMountType(sb.toString()), (Player) event.getWhoClicked());
                    if (!UltraCosmetics.closeAfterSelect)
                        openMenu((Player) event.getWhoClicked(), currentPage);
                }

            }
        }
    }

}
