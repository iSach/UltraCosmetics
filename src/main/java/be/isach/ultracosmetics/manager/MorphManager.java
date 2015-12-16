package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 11/11/15.
 */
public class MorphManager implements Listener {

    static List<Player> playerList = new ArrayList<>();

    public static void openMorphsMenu(final Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                int listSize = 0;
                for (Morph m : Core.getMorphs()) {
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

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Morphs"));

                int i = 10;
                for (Morph m : Core.getMorphs()) {
                    if (!m.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
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
                    if (!m.getType().isEnabled()) continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                        if (!p.hasPermission(m.getType().getPermission()))
                            continue;
                    if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled") && !p.hasPermission(m.getType().getPermission())) {
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
                        lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(m.getType().getPermission()) ? "Yes" : "No")))));
                    }
                    String toggle = MessageManager.getMessage("Menu.Morph");
                    CustomPlayer cp = Core.getCustomPlayer(p);
                    if (cp.currentMorph != null && cp.currentMorph.getType() == m.getType())
                        toggle = MessageManager.getMessage("Menu.Unmorph");
                    ItemStack is = ItemFactory.create(m.getMaterial(), m.getData(), toggle + " " + m.getName());
                    if (cp.currentMorph != null && cp.currentMorph.getType() == m.getType())
                        is = ItemFactory.addGlow(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> loreList = new ArrayList<>();
                    if (m.showsDescription()) {
                        loreList.add("");
                        for (String s : m.getDescription())
                            loreList.add(s);
                        loreList.add("");
                    }
                    if (lore != null)
                        loreList.add(lore);
                    loreList.add("");
                    loreList.add(m.getType().getSkill());
                    itemMeta.setLore(loreList);
                    is.setItemMeta(itemMeta);
                    inv.setItem(i, is);
                    if (i == 25 || i == 34 || i == 16) {
                        i += 3;
                    } else {
                        i++;
                    }
                }

                if (Category.MORPHS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Clear-Morph")));
                int d = (Category.MORPHS.hasGoBackArrow() ? 5 : 6);
                if (Core.getCustomPlayer(p).canSeeSelfMorph())
                    inv.setItem(inv.getSize() - d, ItemFactory.create(Material.EYE_OF_ENDER, (byte) 0x0, MessageManager.getMessage("Disable-Third-Person-View")));
                else
                    inv.setItem(inv.getSize() - d, ItemFactory.create(Material.ENDER_PEARL, (byte) 0x0, MessageManager.getMessage("Enable-Third-Person-View")));

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

    public static void activateMorphByType(Morph.MorphType type, final Player PLAYER) {
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
        for (Morph morph : Core.getMorphs()) {
            if (morph.getType().isEnabled() && morph.getType() == type) {
                Class gadgetClass = morph.getClass();

                Class[] cArg = new Class[1];
                cArg[0] = UUID.class;

                UUID uuid = PLAYER.getUniqueId();

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

    public static Morph.MorphType getMorphByName(String name) {
        for (Morph morph : Core.getMorphs()) {
            if (morph.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return morph.getType();
            }
        }
        return null;
    }


    @EventHandler
    public void morphSelection(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Morphs"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Morphs"))
                    || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                return;
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                MainMenuManager.openMainMenu((Player) event.getWhoClicked());
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Enable-Third-Person-View"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).setSeeSelfMorph(true);
                event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.EYE_OF_ENDER, (byte) 0x0, MessageManager.getMessage("Disable-Third-Person-View")));
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    Morph morph = Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph;
                    morph.disguise = new me.libraryaddict.disguise.disguisetypes.MobDisguise(morph.disguiseType);
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
                    morph.disguise = new me.libraryaddict.disguise.disguisetypes.MobDisguise(morph.disguiseType);
                    me.libraryaddict.disguise.DisguiseAPI.disguiseToAll(event.getWhoClicked(), morph.disguise);
                    morph.disguise.setShowName(true);
                    if (!Core.getCustomPlayer((Player) event.getWhoClicked()).canSeeSelfMorph())
                        morph.disguise.setViewSelfDisguise(false);
                }
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Morph"))) {
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    event.getWhoClicked().closeInventory();
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
                    openMorphsMenu((Player) event.getWhoClicked());
                } else return;
                return;
            }
            event.getWhoClicked().closeInventory();
            if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unmorph"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
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
                activateMorphByType(getMorphByName(sb.toString()), (Player) event.getWhoClicked());
            }

        }
    }

}
