package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.cosmetics.suits.SuitType;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 20/12/15.
 */
public class SuitManager implements Listener {

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 12, 14, 16
            };
    static List<Player> noSpamList = new ArrayList<>();

    public static void openMenu(final Player p, final int PAGE) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                int slotAmount = 54;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Suits") +
                        " §7§o(" + PAGE + "/" + getMaxPagesAmount() + ")");

                int i = 0;
                int from = 1;
                if (PAGE > 1)
                    from = 21 * (PAGE - 1) + 1;
                int to = 21 * PAGE;
                for (int h = from; h <= to; h++) {
                    if (h > SuitType.enabled().size())
                        break;
                    SuitType suit = SuitType.enabled().get(h - 1);
                    if (!suit.isEnabled()) continue;
                    boolean shouldIncrement = false;
                    for (int d = 0; d < ArmorSlot.values().length; d++) {
                        ArmorSlot armorSlot = ArmorSlot.values()[d];
                        if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                            if (!p.hasPermission(suit.getPermission(armorSlot))) {
                                shouldIncrement = false;
                                continue;
                            }
                        if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(suit.getPermission(armorSlot))) {
                            Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                            Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                            String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("&", "§").replace("{cosmetic-name}", suit.getName(armorSlot)).replace("&", "§");
                            List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                            String[] array = new String[npLore.size()];
                            npLore.toArray(array);
                            inv.setItem(COSMETICS_SLOTS[i] + d * 9, ItemFactory.create(material, data, name, array));
                            shouldIncrement = true;
                            continue;
                        }
                        String lore = null;
                        if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore"))
                            lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig()
                                    .get("No-Permission.Lore-Message-" + ((p.hasPermission(suit.getPermission(armorSlot)) ? "Yes" : "No")))));
                        String toggle = MessageManager.getMessage("Menu.Equip");
                        CustomPlayer cp = Core.getCustomPlayer(p);
                        Suit current = null;
                        switch (armorSlot) {
                            case HELMET:
                                current = cp.currentHelmet;
                                break;
                            case CHESTPLATE:
                                current = cp.currentChestplate;
                                break;
                            case LEGGINGS:
                                current = cp.currentLeggings;
                                break;
                            case BOOTS:
                                current = cp.currentBoots;
                                break;
                        }
                        if (current != null && current.getType() == suit)
                            toggle = MessageManager.getMessage("Menu.Unequip");
                        String customName = "";
                        ItemStack is = ItemFactory.create(suit.getMaterial(armorSlot), (byte) 0, toggle + " " + suit.getName(armorSlot) + customName);
                        if (lore != null)
                            is = ItemFactory.create(suit.getMaterial(armorSlot), (byte) 0, toggle + " " + suit.getName(armorSlot) + customName);
                        if (current != null && current.getType() == suit)
                            is = ItemFactory.addGlow(is);
                        ItemMeta itemMeta = is.getItemMeta();
                        List<String> loreList = new ArrayList<>();
                        if (suit.showsDescription()) {
                            loreList.add("");
                            for (String s : suit.getDescription())
                                loreList.add(s);
                            loreList.add("");
                        }
                        if (lore != null)
                            loreList.add(lore);
                        itemMeta.setLore(loreList);
                        is.setItemMeta(itemMeta);

                        if (suit == SuitType.SANTA) {
                            LeatherArmorMeta colorMeta = (LeatherArmorMeta) is.getItemMeta();
                            colorMeta.setColor(Color.fromRGB(255, 0, 0));
                            is.setItemMeta(colorMeta);
                        } else if (suit == SuitType.RAVE) {
                            LeatherArmorMeta colorMeta = (LeatherArmorMeta) is.getItemMeta();
                            colorMeta.setColor(Color.fromRGB(MathUtils.random.nextInt(256),
                                    MathUtils.random.nextInt(256),
                                    MathUtils.random.nextInt(256)));
                            is.setItemMeta(colorMeta);
                        }

                        inv.setItem(COSMETICS_SLOTS[i] + d * 9, is);
                        shouldIncrement = true;
                    }
                    if (shouldIncrement)
                        i++;
                }

                if (Category.SUITS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));

                inv.setItem(inv.getSize() - (Category.SUITS.hasGoBackArrow() ? 4 : 5), ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Clear-Suit")));

                if (PAGE > 1)
                    inv.setItem(inv.getSize() - 18, ItemFactory.create(Material.ENDER_PEARL, (byte) 0, MessageManager.getMessage("Menu.Previous-Page")));
                if (PAGE < getMaxPagesAmount())
                    inv.setItem(inv.getSize() - 10, ItemFactory.create(Material.EYE_OF_ENDER, (byte) 0, MessageManager.getMessage("Menu.Next-Page")));

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
        int i = SuitType.enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    private static int getCurrentPage(Player player) {
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Suits"))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus.Suits") + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    public static void equipSuit(final SuitType type, final Player player, final ArmorSlot armorSlot) {
        if (!player.hasPermission(type.getPermission(armorSlot))) {
            if (!noSpamList.contains(player)) {
                player.sendMessage(MessageManager.getMessage("No-Permission"));
                noSpamList.add(player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        noSpamList.remove(player);
                    }
                }, 1);
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                type.equip(player, armorSlot);
            }
        }.run();
    }

    public static SuitType getSuitType(String name, ArmorSlot armorSlot) {
        for (SuitType suitType : SuitType.enabled())
            if (suitType.getName(armorSlot).replace(" ", "").equals(name.replace(" ", "")))
                return suitType;
        return null;
    }

    @EventHandler
    public void suitSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Suits"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Suits"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE)
                    return;
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    MainMenuManager.openMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Suit"))) {
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentHelmet != null
                            && Core.getCustomPlayer((Player) event.getWhoClicked()).currentChestplate != null
                            && Core.getCustomPlayer((Player) event.getWhoClicked()).currentLeggings != null
                            && Core.getCustomPlayer((Player) event.getWhoClicked()).currentBoots != null) {
                        int currentPage = getCurrentPage((Player) event.getWhoClicked());
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removeSuit();
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    } else return;
                    return;
                }

                int s = event.getSlot();
                int t = (s - (s % 9)) / 9;
                ArmorSlot armorSlot = t < 5 ? ArmorSlot.values()[t - 1] : null;

                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unequip"))) {
                    if (armorSlot == null)
                        return;
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeSuit(armorSlot);
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()));
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Next-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) + 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Previous-Page"))) {
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) - 1);
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Equip"))) {
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replaceFirst(MessageManager.getMessage("Menu.Equip"), "");

                    if (armorSlot == null)
                        return;

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
                    equipSuit(getSuitType(sb.toString(), armorSlot), (Player) event.getWhoClicked(), armorSlot);
                    openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()));
                }

            }
        }
    }

    /**
     * Cancel players from dropping the suit in their inventory.
     *
     * @param event
     */
    @EventHandler
    public void cancelHatDropping(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack().clone();
        if (item != null
                && item.hasItemMeta()
                && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().contains(MessageManager.getMessage("Suits.Suit-Part-Lore"))) {
            event.getItemDrop().remove();
            event.getPlayer().closeInventory();
            event.getPlayer().updateInventory();
        }
    }

    /**
     * Cancel players from removing, picking the suit in their inventory.
     *
     * @param EVENT
     */
    @EventHandler
    public void cancelHatClick(final InventoryClickEvent EVENT) {
        if (EVENT.getCurrentItem() != null
                && EVENT.getCurrentItem().hasItemMeta()
                && EVENT.getCurrentItem().getItemMeta().hasLore()
                && EVENT.getCurrentItem().getItemMeta().getLore().contains(MessageManager.getMessage("Suits.Suit-Part-Lore"))) {
            EVENT.setCancelled(true);
            EVENT.setResult(Event.Result.DENY);
            EVENT.getWhoClicked().closeInventory();
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    for (ItemStack itemStack : EVENT.getWhoClicked().getInventory().getContents()) {
                        if (itemStack != null
                                && itemStack.hasItemMeta()
                                && itemStack.getItemMeta().hasLore()
                                && itemStack.getItemMeta().getLore().contains(MessageManager.getMessage("Suits.Suit-Part-Lore"))
                                && itemStack != EVENT.getWhoClicked().getInventory().getHelmet())
                            EVENT.getWhoClicked().getInventory().remove(itemStack);
                    }
                }
            }, 1);
        }
    }

}

