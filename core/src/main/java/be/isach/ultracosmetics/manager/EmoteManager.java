package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.emotes.Emote;
import be.isach.ultracosmetics.cosmetics.emotes.EmoteType;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.manager
 * Created by: Sacha
 * Created on: 21th June, 2016
 * at 01:00
 */
public class EmoteManager implements Listener {

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };

    private static List<Player> playerList = new ArrayList<>();

    public static void openMenu(final Player p, int page) {
        page = Math.max(1, Math.min(page, getMaxPagesAmount()));
        final int finalPage = page;
        Bukkit.getScheduler().runTaskAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                int listSize = EmoteType.enabled().size();
                int slotAmount = 54;
                if (listSize < 22)
                    slotAmount = 54;
                if (listSize < 15)
                    slotAmount = 45;
                if (listSize < 8)
                    slotAmount = 36;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Emotes")
                        + " §7§o(" + finalPage + "/" + getMaxPagesAmount() + ")");

                int i = 0;
                int from = 1;
                if (finalPage > 1)
                    from = 21 * (finalPage - 1) + 1;
                int to = 21 * finalPage;
                for (int h = from; h <= to; h++) {
                    if (h > EmoteType.enabled().size())
                        break;
                    EmoteType emoteType = EmoteType.enabled().get(h - 1);
                    if (!emoteType.isEnabled()) continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(emoteType.getPermission()))
                            continue;
                    if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(emoteType.getPermission())) {
                        Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                        Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                        String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", emoteType.getName()).replace("&", "§");
                        List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                        String[] array = new String[npLore.size()];
                        npLore.toArray(array);
                        inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array));
                        i++;
                        continue;
                    }
                    String lore = null;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore"))
                        lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(emoteType.getPermission()) ? "Yes" : "No")))));
                    String toggle = MessageManager.getMessage("Menu.Equip");
                    CustomPlayer cp = UltraCosmetics.getCustomPlayer(p);
                    if (cp.currentEmote != null && cp.currentEmote.getEmoteType() == emoteType)
                        toggle = MessageManager.getMessage("Menu.Unequip");
                    ItemStack is;
                    if (emoteType.getFrames().size() > 0) {
                        is = emoteType.getFrames().get(0).clone();
                    } else {
                        is = ItemFactory.create(Material.SKULL_ITEM, (byte) 3, "");
                    }
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(toggle + " " + emoteType.getName());
                    is.setItemMeta(im);
                    if (cp.currentEmote != null && cp.currentEmote.getEmoteType() == emoteType)
                        is = ItemFactory.addGlow(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> loreList = new ArrayList<>();
                    if (emoteType.showsDescription()) {
                        loreList.add("");
                        for (String s : emoteType.getDescription())
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

                if (Category.EMOTES.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getItemType(), ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getData(), MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - (Category.EMOTES.hasGoBackArrow() ? 4 : 5), ItemFactory.create(ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getItemType(), ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getData(), MessageManager.getMessage("Clear-Emote")));

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
        int i = EmoteType.enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    public static void equipEmote(EmoteType emoteType, final Player player) {
        if (!player.hasPermission(emoteType.getPermission())) {
            if (!playerList.contains(player)) {
                player.sendMessage(MessageManager.getMessage("No-Permission"));
                playerList.add(player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        playerList.remove(player);
                    }
                }, 1);
            }
            return;
        }

        Emote emote = emoteType.equip(player);
        emote.equip();
    }

    public static EmoteType getEmoteType(String name) {
        for (EmoteType emoteType : EmoteType.enabled())
            if (emoteType.getName().replace(" ", "").equals(name.replace(" ", "")))
                return emoteType;
        return null;
    }

    private static int getCurrentPage(Player player) {
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Emotes"))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus.Emotes") + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    /**
     * Cancel players from dropping the emote in their inventory.
     *
     * @param event
     */
    @EventHandler
    public void cancelEmoteDropping(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack().clone();
        if (item != null
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals("§8§oEmote")) {
            event.getItemDrop().remove();
            event.getPlayer().closeInventory();
            event.getPlayer().updateInventory();
        }
    }

    /**
     * Cancel players from removing, picking the emote in their inventory.
     *
     * @param EVENT
     */
    @EventHandler
    public void cancelEmoteClick(final InventoryClickEvent EVENT) {
        if (EVENT.getCurrentItem() != null
                && EVENT.getCurrentItem().hasItemMeta()
                && EVENT.getCurrentItem().getItemMeta().hasDisplayName()
                && EVENT.getCurrentItem().getItemMeta().getDisplayName().equals("§8§oEmote")) {
            EVENT.setCancelled(true);
            EVENT.setResult(Event.Result.DENY);
            EVENT.getWhoClicked().closeInventory();
            Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (ItemStack itemStack : EVENT.getWhoClicked().getInventory().getContents()) {
                        if (itemStack != null
                                && itemStack.hasItemMeta()
                                && itemStack.getItemMeta().hasDisplayName()
                                && itemStack.getItemMeta().getDisplayName().equals("§8§oEmote")
                                && itemStack != EVENT.getWhoClicked().getInventory().getHelmet())
                            EVENT.getWhoClicked().getInventory().remove(itemStack);
                    }
                }
            }, 1);
        }
    }

    @EventHandler
    public void emoteSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Emotes"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Emotes"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    UltraCosmetics.openMainMenuFromOther((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Emote"))) {
                    if (UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).currentEmote != null) {
                        int currentPage = getCurrentPage((Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                        UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeEmote();
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
                if (UltraCosmetics.closeAfterSelect)
                    event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unequip"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeEmote();
                    if (!UltraCosmetics.closeAfterSelect)
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Equip"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removeEmote();
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
                    EmoteType emoteType = getEmoteType(sb.toString());
                    System.out.println(emoteType);
                    equipEmote(emoteType, (Player) event.getWhoClicked());
                    if (!UltraCosmetics.closeAfterSelect)
                        openMenu((Player) event.getWhoClicked(), currentPage);
                }

            }
        }
    }

}
