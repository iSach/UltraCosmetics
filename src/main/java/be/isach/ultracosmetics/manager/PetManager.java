package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.util.AnvilGUI;
import be.isach.ultracosmetics.util.ItemFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Sacha on 11/11/15.
 */
public class PetManager implements Listener {

    static List<Player> playerList = new ArrayList<>();

    public static void openPetsMenu(final Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                int listSize = 0;
                for (Pet m : Core.getPets()) {
                    if (!m.getType().isEnabled()) continue;
                    listSize++;
                }
                int slotAmount = 54;
                if (listSize < 22)
                    slotAmount = 54;
                if (listSize < 15)
                    slotAmount = 45;
                if (listSize < 8)
                    slotAmount = 36;


                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Pets"));

                int i = 10;
                for (Pet pet : Core.getPets()) {
                    if (!pet.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
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
                    if (!pet.getType().isEnabled()) continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(pet.getType().getPermission()))
                            continue;
                    if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(pet.getType().getPermission())) {
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
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
                        lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(pet.getType().getPermission()) ? "Yes" : "No")))));
                    }
                    String toggle = MessageManager.getMessage("Menu.Spawn");
                    CustomPlayer cp = Core.getCustomPlayer(p);
                    if (cp.currentPet != null && cp.currentPet.getType() == pet.getType())
                        toggle = MessageManager.getMessage("Menu.Despawn");
                    String customName = "";
                    if (Core.getCustomPlayer(p).getPetName(pet.getConfigName()) != null) {
                        customName = " §f§l(§r" + Core.getCustomPlayer(p).getPetName(pet.getConfigName()) + "§f§l)";
                    }
                    ItemStack is = ItemFactory.create(pet.getMaterial(), pet.getData(), toggle + " " + pet.getMenuName() + customName);
                    if (lore != null)
                        is = ItemFactory.create(pet.getMaterial(), pet.getData(), toggle + " " + pet.getMenuName() + customName);
                    if (cp.currentPet != null && cp.currentPet.getType() == pet.getType())
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
                    inv.setItem(i, is);
                    if (i == 25 || i == 34 || i == 16) {
                        i += 3;
                    } else {
                        i++;
                    }
                }

                if (Category.PETS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Clear-Pet")));
                int d = (Category.PETS.hasGoBackArrow() ? 5 : 6);
                if (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
                    if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required")) {
                        if (p.hasPermission("ultracosmetics.pets.rename")) {
                            if (Core.getCustomPlayer(p).currentPet != null)
                                inv.setItem(inv.getSize() - d, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet").replace("%petname%", Core.getCustomPlayer(p).currentPet.getMenuName())));
                            else
                                inv.setItem(inv.getSize() - d, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Active-Pet-Needed")));
                        }
                    } else {
                        if (Core.getCustomPlayer(p).currentPet != null)
                            inv.setItem(inv.getSize() - d, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet").replace("%petname%", Core.getCustomPlayer(p).currentPet.getMenuName())));
                        else
                            inv.setItem(inv.getSize() - d, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Active-Pet-Needed")));
                    }
                }
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

    @EventHandler
    public void petSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Pets"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    MainMenuManager.openMainMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Pet"))) {
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentPet != null) {
                        event.getWhoClicked().closeInventory();
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removePet();
                        openPetsMenu((Player) event.getWhoClicked());
                    } else return;
                    return;
                }
                if (event.getCurrentItem().getType() == Material.NAME_TAG) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Active-Pet-Needed"))) {
                        return;
                    } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Rename-Pet").replace("%petname%", Core.getCustomPlayer((Player) event.getWhoClicked()).currentPet.getMenuName()))) {
                        renamePet((Player) event.getWhoClicked());
                        return;
                    }
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Despawn"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removePet();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Spawn"))) {
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replace(MessageManager.getMessage("Menu.Spawn"), "");
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
                    activatePetByType(getPetByName(sb.toString()), (Player) event.getWhoClicked());
                }

            }
        }
    }

    public static void activatePetByType(Pet.PetType type, final Player PLAYER) {
        if (!PLAYER.hasPermission(type.getPermission())) {
            if (!playerList.contains(PLAYER)) {
                PLAYER.sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
        }
        if (playerList.contains(PLAYER))
            return;
        playerList.add(PLAYER);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                playerList.remove(PLAYER);
            }
        }, 4);

        for (Pet pet : Core.getPets()) {
            if (pet.getType().isEnabled() && pet.getType() == type) {
                Class petClass = pet.getClass();

                Class[] cArg = new Class[1];
                cArg[0] = UUID.class;

                UUID uuid = PLAYER.getUniqueId();

                try {
                    petClass.getDeclaredConstructor(UUID.class).newInstance(uuid);
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

    public static Pet.PetType getPetByName(String name) {
        for (Pet pet : Core.getPets()) {
            if (pet.getMenuName().replace(" ", "").equals(name.replace(" ", ""))) {
                return pet.getType();
            }
        }
        return null;
    }

    /* =+=+=+=+=+=+=+=+=+=+=+= Rename pet part =+=+=+=+=+=+=+=+=+=+= */

    private static Map<Player, String> renamePetList = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        renamePetList.remove(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        renamePetList.remove(e.getPlayer());
    }

    public static void renamePet(final Player p) {

        AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler() {
            @Override
            public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
                if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                    if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled")) {
                        buyRenamePet(p, event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace('&', '§').replace(" ", ""));
                    } else {
                        if (Core.getCustomPlayer(p).currentPet.getType() == Pet.PetType.WITHER)
                            Core.getCustomPlayer(p).currentPet.ent.setCustomName(event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace('&', '§').replace(" ", ""));
                        else
                            Core.getCustomPlayer(p).currentPet.armorStand.setCustomName(event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace('&', '§').replace(" ", ""));
                        Core.getCustomPlayer(p).setPetName(Core.getCustomPlayer(p).currentPet.getConfigName(), event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace('&', '§').replace(" ", ""));
                    }
                } else {
                    event.setWillClose(false);
                    event.setWillDestroy(false);
                }
            }
        });
        gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemStack(Material.NAME_TAG));
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

        inventory.setItem(13, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet-Purchase").replace("%price%", "" + SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price")).replace("%name%", name)));

        ItemFactory.fillInventory(inventory);

        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                renamePetList.put(p, name);
                p.openInventory(inventory);
            }
        }, 1);
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
                    if (Core.getCustomPlayer(p).getMoney() >= (int) SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price")) {
                        Core.economy.withdrawPlayer(p, (int) SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price"));
                        p.sendMessage(MessageManager.getMessage("Successful-Purchase"));
                        if (Core.getCustomPlayer(p).currentPet.getType() == Pet.PetType.WITHER)
                            Core.getCustomPlayer(p).currentPet.ent.setCustomName(name);
                        else
                            Core.getCustomPlayer(p).currentPet.armorStand.setCustomName(name);
                        Core.getCustomPlayer(p).setPetName(Core.getCustomPlayer(p).currentPet.getConfigName(), name);
                    } else {
                        p.sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    }
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
