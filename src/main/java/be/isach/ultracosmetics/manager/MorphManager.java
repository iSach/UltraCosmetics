package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.morphs.MorphType;
import be.isach.ultracosmetics.util.ItemFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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
public class MorphManager implements Listener {

    static List<Player> playerList = new ArrayList<>();

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };

    public static void openMenu(final Player p, int page) {
        page = Math.max(1, Math.min(page, getMaxPagesAmount()));
        final int finalPAGE = page;
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                int listSize = MorphType.enabled().size();
                int slotAmount = 54;
                if (listSize < 22)
                    slotAmount = 54;
                if (listSize < 15)
                    slotAmount = 45;
                if (listSize < 8)
                    slotAmount = 36;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Morphs")
                        + " §7§o(" + finalPAGE + "/" + getMaxPagesAmount() + ")");

                int i = 0;
                int from = 1;
                if (finalPAGE > 1)
                    from = 21 * (finalPAGE - 1) + 1;
                int to = 21 * finalPAGE;
                for (int h = from; h <= to; h++) {
                    if (h > MorphType.enabled().size())
                        break;
                    MorphType morphType = MorphType.enabled.get(h - 1);
                    if (!morphType.isEnabled()) continue;

                    if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(morphType.getPermission()))
                            continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled") && !p.hasPermission(morphType.getPermission())) {
                        Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                        Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                        String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("&", "§");
                        inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name));
                        i++;
                        continue;
                    }
                    String lore = null;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore"))
                        lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(morphType.getPermission()) ? "Yes" : "No")))));
                    String toggle = MessageManager.getMessage("Menu.Morph");
                    CustomPlayer cp = Core.getCustomPlayer(p);
                    if (cp.currentMorph != null && cp.currentMorph.getType() == morphType)
                        toggle = MessageManager.getMessage("Menu.Unmorph");
                    ItemStack is = ItemFactory.create(morphType.getMaterial(), morphType.getData(), toggle + " " + morphType.getName());
                    if (cp.currentMorph != null && cp.currentMorph.getType() == morphType)
                        is = ItemFactory.addGlow(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> loreList = new ArrayList<>();
                    if (morphType.showsDescription()) {
                        loreList.add("");
                        for (String s : morphType.getDescription())
                            loreList.add(s);
                        loreList.add("");
                    }
                    if (lore != null)
                        loreList.add(lore);
                    loreList.add("");
                    loreList.add(morphType.getSkill());
                    itemMeta.setLore(loreList);
                    is.setItemMeta(itemMeta);
                    inv.setItem(COSMETICS_SLOTS[i], is);
                    i++;
                }

                if (Category.MORPHS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getItemType(), ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getData(), MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - 4, ItemFactory.create(ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getItemType(), ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getData(), MessageManager.getMessage("Clear-Morph")));
                int d = (Category.MORPHS.hasGoBackArrow() ? 5 : 6);

                if (Core.getCustomPlayer(p).canSeeSelfMorph())
                    inv.setItem(inv.getSize() - d, ItemFactory.create(ItemFactory.createFromConfig("Categories.Self-View-Item.When-Enabled").getItemType(), ItemFactory.createFromConfig("Categories.Self-View-Item.When-Enabled").getData(), MessageManager.getMessage("Disable-Third-Person-View")));
                else
                    inv.setItem(inv.getSize() - d, ItemFactory.create(ItemFactory.createFromConfig("Categories.Self-View-Item.When-Disabled").getItemType(), ItemFactory.createFromConfig("Categories.Self-View-Item.When-Disabled").getData(), MessageManager.getMessage("Enable-Third-Person-View")));

                if (finalPAGE > 1)
                    inv.setItem(inv.getSize() - 18, ItemFactory.create(ItemFactory.createFromConfig("Categories.Previous-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Previous-Page-Item").getData(), MessageManager.getMessage("Menu.Previous-Page")));
                if (finalPAGE < getMaxPagesAmount())
                    inv.setItem(inv.getSize() - 10, ItemFactory.create(ItemFactory.createFromConfig("Categories.Next-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Next-Page-Item").getData(), MessageManager.getMessage("Menu.Next-Page")));


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

    public static void equipMorph(final MorphType TYPE, final Player PLAYER) {
        if (!PLAYER.hasPermission(TYPE.getPermission())) {
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
                TYPE.equip(PLAYER);
            }
        }.run();
    }

    public static MorphType getMorph(String name) {
        for (MorphType morphType : MorphType.enabled())
            if (morphType.getName().replace(" ", "").equals(name.replace(" ", "")))
                return morphType;
        return null;
    }

    @EventHandler
    public void morphSelection(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Morphs"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Morphs"))
                    || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                return;
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                MainMenuManager.openMenu((Player) event.getWhoClicked());
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Enable-Third-Person-View"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).setSeeSelfMorph(true);
                event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.EYE_OF_ENDER, (byte) 0x0, MessageManager.getMessage("Disable-Third-Person-View")));
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    Morph morph = Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph;
                    morph.disguise = new me.libraryaddict.disguise.disguisetypes.MobDisguise(morph.getType().getDisguiseType());
                    me.libraryaddict.disguise.DisguiseAPI.disguiseToAll(event.getWhoClicked(), morph.disguise);
                    morph.disguise.setShowName(true);
                    if (!Core.getCustomPlayer((Player) event.getWhoClicked()).canSeeSelfMorph())
                        morph.disguise.setViewSelfDisguise(false);
                }
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Disable-Third-Person-View"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).setSeeSelfMorph(false);
                event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.ENDER_PEARL, (byte) 0x0, MessageManager.getMessage("Enable-Third-Person-View")));
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    Morph morph = Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph;
                    morph.disguise = new me.libraryaddict.disguise.disguisetypes.MobDisguise(morph.getType().getDisguiseType());
                    me.libraryaddict.disguise.DisguiseAPI.disguiseToAll(event.getWhoClicked(), morph.disguise);
                    morph.disguise.setShowName(true);
                    if (!Core.getCustomPlayer((Player) event.getWhoClicked()).canSeeSelfMorph())
                        morph.disguise.setViewSelfDisguise(false);
                }
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Next-Page"))) {
                openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) + 1);
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Previous-Page"))) {
                openMenu((Player) event.getWhoClicked(), getCurrentPage((Player) event.getWhoClicked()) - 1);
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Morph"))) {
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    int currentPage = getCurrentPage((Player) event.getWhoClicked());
                    event.getWhoClicked().closeInventory();
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
                    openMenu((Player) event.getWhoClicked(), currentPage);
                } else return;
                return;
            }
            int currentPage = getCurrentPage((Player) event.getWhoClicked());
            if (Core.closeAfterSelect)
                event.getWhoClicked().closeInventory();
            if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unmorph"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
                if (!Core.closeAfterSelect)
                    openMenu((Player) event.getWhoClicked(), currentPage);
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Morph"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
                StringBuilder sb = new StringBuilder();
                String name = event.getCurrentItem().getItemMeta().getDisplayName().replace(MessageManager.getMessage("Menu.Morph"), "");
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
                equipMorph(getMorph(sb.toString()), (Player) event.getWhoClicked());
                if(!Core.closeAfterSelect)
                    openMenu((Player)event.getWhoClicked(), currentPage);
            }

        }
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    private static int getMaxPagesAmount() {
        int max = 21;
        int i = MorphType.enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    private static int getCurrentPage(Player player) {
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Morphs"))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus.Morphs") + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

}
