package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.version.AAnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sacha on 11/11/15.
 */
public class PetManager implements Listener {

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };
    static List<HumanEntity> noSpam = new ArrayList<>();
    static List<Player> noSpamList = new ArrayList<>();
    private static Map<Player, String> renamePetList = new HashMap<>();

    public static void openMenu(final Player p, int page) {
        page = Math.max(1, Math.min(page, getMaxPagesAmount()));
        final int finalPage = page;
        Bukkit.getScheduler().runTaskAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                int listSize = PetType.enabled().size();
                int slotAmount = 54;
                if (listSize < 22)
                    slotAmount = 54;
                if (listSize < 15)
                    slotAmount = 45;
                if (listSize < 8)
                    slotAmount = 36;

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Pets") +
                        " §7§o(" + finalPage + "/" + getMaxPagesAmount() + ")");

                int i = 0;
                int from = 1;
                if (finalPage > 1)
                    from = 21 * (finalPage - 1) + 1;
                int to = 21 * finalPage;
                for (int h = from; h <= to; h++) {
                    if (h > PetType.enabled().size())
                        break;
                    PetType pet = PetType.enabled().get(h - 1);
                    if (!pet.isEnabled()) continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(pet.getPermission()))
                            continue;
                    if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(pet.getPermission())) {
                        Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                        Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                        String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("&", "§").replace("{cosmetic-name}", pet.getMenuName()).replace("&", "§");
                        List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                        String[] array = new String[npLore.size()];
                        npLore.toArray(array);
                        inv.setItem(COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array));
                        i++;
                        continue;
                    }
                    String lore = null;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
                        lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig()
                                .get("No-Permission.Lore-Message-" + ((p.hasPermission(pet.getPermission()) ? "Yes" : "No")))));
                    }
                    String toggle = MessageManager.getMessage("Menu.Spawn");
                    CustomPlayer cp = UltraCosmetics.getCustomPlayer(p);
                    if (cp.currentPet != null && cp.currentPet.getType() == pet)
                        toggle = MessageManager.getMessage("Menu.Despawn");
                    String customName = "";
                    if (UltraCosmetics.getCustomPlayer(p).getPetName(pet.getConfigName()) != null) {
                        customName = " §f§l(§r" + UltraCosmetics.getCustomPlayer(p).getPetName(pet.getConfigName()) + "§f§l)";
                    }
                    ItemStack is = ItemFactory.create(pet.getMaterial(), pet.getData(), toggle + " " + pet.getMenuName() + customName);
                    if (lore != null)
                        is = ItemFactory.create(pet.getMaterial(), pet.getData(), toggle + " " + pet.getMenuName() + customName);
                    if (cp.currentPet != null && cp.currentPet.getType() == pet)
                        is = ItemFactory.addGlow(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> loreList = new ArrayList<>();
                    if (pet.showsDescription()) {
                        loreList.add("");
                        for (String s : pet.getDescription())
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

                if (Category.PETS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getItemType(), ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item").getData(), MessageManager.getMessage("Menu.Main-Menu")));

                inv.setItem(inv.getSize() - (Category.PETS.hasGoBackArrow() ? 4 : 5), ItemFactory.create(ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getItemType(), ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item").getData(), MessageManager.getMessage("Clear-Pet")));
                int d = (Category.PETS.hasGoBackArrow() ? 5 : 6);
                if (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
                    if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required")) {
                        if (p.hasPermission("ultracosmetics.pets.rename"))
                            if (UltraCosmetics.getCustomPlayer(p).currentPet != null)
                                inv.setItem(inv.getSize() - d, ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Rename-Pet")
                                        .replace("%petname%", UltraCosmetics.getCustomPlayer(p).currentPet.getType().getMenuName())));
                            else
                                inv.setItem(inv.getSize() - d, ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Active-Pet-Needed")));
                    } else if (UltraCosmetics.getCustomPlayer(p).currentPet != null)
                        inv.setItem(inv.getSize() - d, ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Rename-Pet")
                                .replace("%petname%", UltraCosmetics.getCustomPlayer(p).currentPet.getType().getMenuName())));
                    else
                        inv.setItem(inv.getSize() - d, ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Active-Pet-Needed")));
                }

                if (finalPage > 1)
                    inv.setItem(inv.getSize() - 18, ItemFactory.create(ItemFactory.createFromConfig("Categories.Previous-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Previous-Page-Item").getData(), MessageManager.getMessage("Menu.Previous-Page")));
                if (finalPage < getMaxPagesAmount())
                    inv.setItem(inv.getSize() - 10, ItemFactory.create(ItemFactory.createFromConfig("Categories.Next-Page-Item").getItemType(), ItemFactory.createFromConfig("Categories.Next-Page-Item").getData(), MessageManager.getMessage("Menu.Next-Page")));

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
        int i = PetType.enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    private static int getCurrentPage(Player player) {
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Pets"))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus.Pets") + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    public static void equipPet(final PetType type, final Player player) {
        if (!player.hasPermission(type.getPermission())) {
            if (!noSpamList.contains(player)) {
                player.sendMessage(MessageManager.getMessage("No-Permission"));
                noSpamList.add(player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
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
                type.equip(player);
            }
        }.run();
    }

    public static PetType getPetType(String name) {
        for (PetType petType : PetType.enabled())
            if (petType.getMenuName().replace(" ", "").equals(name.replace(" ", "")))
                return petType;
        return null;
    }

    /* =+=+=+=+=+=+=+=+=+=+=+= Rename pet part =+=+=+=+=+=+=+=+=+=+= */

    public static void renamePet(final Player p) {
        AAnvilGUI gui = UltraCosmetics.getInstance().newAnvilGUI(p , new AAnvilGUI.AnvilClickEventHandler() {
            @Override
            public void onAnvilClick(AAnvilGUI.AnvilClickEvent event) {
                if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT) {
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                    if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled")
                            && UltraCosmetics.petRenameMoney) {
                        buyRenamePet(p, event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace('&', '§').replace(" ", ""));
                    } else {
                        if (UltraCosmetics.getCustomPlayer(p).currentPet.getType() == PetType.WITHER)
                            UltraCosmetics.getCustomPlayer(p).currentPet.entity.setCustomName(event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "")
                                    .replace('&', '§').replace(" ", ""));
                        else
                            UltraCosmetics.getCustomPlayer(p).currentPet.armorStand.setCustomName(event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "")
                                    .replace('&', '§').replace(" ", ""));
                        UltraCosmetics.getCustomPlayer(p).setPetName(UltraCosmetics.getCustomPlayer(p).currentPet.getType().getConfigName(), event.getName()
                                .replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace('&', '§').replace(" ", ""));
                    }
                } else {
                    event.setWillClose(false);
                    event.setWillDestroy(false);
                }

            }
        });
        gui.setSlot(AAnvilGUI.AnvilSlot.INPUT_LEFT, ItemFactory.create(Material.NAME_TAG, (byte) 0, "Type here"));
        gui.open();
    }

    private static void buyRenamePet(final Player p, final String name) {
        p.closeInventory();
        final Inventory inventory = Bukkit.createInventory(null, 54, MessageManager.getMessage("Menus.Rename-Pet"));

        for (int i = 27; i < 30; i++) {
            inventory.setItem(i, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 9, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 18, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 9 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 18 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
        }

        inventory.setItem(13, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet-Purchase")
                .replace("%price%", "" + SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price")).replace("%name%", name)));

        ItemFactory.fillInventory(inventory);

        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                renamePetList.put(p, name);
                p.openInventory(inventory);
            }
        }, 1);
    }

    @EventHandler
    public void petSelection(final InventoryClickEvent event) {
        if (event.getInventory().getTitle().startsWith(MessageManager.getMessage("Menus.Pets"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    UltraCosmetics.openMainMenuFromOther((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Pet"))) {
                    if (UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).currentPet != null) {
                        int currentPage = getCurrentPage((Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                        UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removePet();
                        openMenu((Player) event.getWhoClicked(), currentPage);
                    } else return;
                    return;
                }
                if (event.getCurrentItem().getType() == ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType()) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Active-Pet-Needed")))
                        return;
                    else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Rename-Pet").replace("%petname%",
                            UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).currentPet.getType().getMenuName()))) {
                        renamePet((Player) event.getWhoClicked());
                        return;
                    }
                }
                int currentPage = getCurrentPage((Player) event.getWhoClicked());
                if (UltraCosmetics.closeAfterSelect)
                    event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Despawn"))) {
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).removePet();
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
                    if (!noSpam.contains(event.getWhoClicked()))
                        equipPet(getPetType(sb.toString()), (Player) event.getWhoClicked());
                    noSpam.add(event.getWhoClicked());
                    Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            noSpam.remove(event.getWhoClicked());
                        }
                    }, 1);

                    if (!UltraCosmetics.closeAfterSelect)
                        openMenu((Player) event.getWhoClicked(), currentPage);
                }

            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        renamePetList.remove(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        renamePetList.remove(e.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            Player p = (Player) event.getWhoClicked();
            if (renamePetList.containsKey(p)) {
                String name = renamePetList.get(p);
                event.setCancelled(true);
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Purchase"))) {
                    if (UltraCosmetics.getCustomPlayer(p).getBalance() >= (int) SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price")) {
                        UltraCosmetics.economy.withdrawPlayer(p, (int) SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price"));
                        p.sendMessage(MessageManager.getMessage("Successful-Purchase"));
                        if (UltraCosmetics.getCustomPlayer(p).currentPet.getType() == PetType.WITHER)
                            UltraCosmetics.getCustomPlayer(p).currentPet.entity.setCustomName(name);
                        else
                            UltraCosmetics.getCustomPlayer(p).currentPet.armorStand.setCustomName(name);
                        UltraCosmetics.getCustomPlayer(p).setPetName(UltraCosmetics.getCustomPlayer(p).currentPet.getType().getConfigName(), name);
                    } else
                        p.sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    renamePetList.remove(p);
                    p.closeInventory();
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
                    renamePetList.remove(p);
                    p.closeInventory();
                }
            }
        }
    }

}
