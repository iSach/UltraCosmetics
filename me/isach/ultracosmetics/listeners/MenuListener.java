package me.isach.ultracosmetics.listeners;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.CustomPlayer;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.morphs.Morph;
import me.isach.ultracosmetics.cosmetics.mounts.Mount;
import me.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import me.isach.ultracosmetics.cosmetics.pets.Pet;
import me.isach.ultracosmetics.util.AnvilGUI;
import me.isach.ultracosmetics.util.Cuboid;
import me.isach.ultracosmetics.util.ItemFactory;
import me.isach.ultracosmetics.util.MathUtils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by sacha on 03/08/15.
 */
public class MenuListener implements Listener {

    private Core core;

    public MenuListener(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Core.getCustomPlayer(event.getPlayer()).currentTreasureChest != null) {
            event.setCancelled(true);
            return;
        }
        if (event.getItem() != null
                && event.getItem().hasItemMeta()
                && event.getItem().getItemMeta().hasDisplayName()
                && event.getItem().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.setCancelled(true);
            openMainMenu(event.getPlayer());
        }
    }

    public static void openMainMenu(Player p) {
        if (!p.hasPermission("ultracosmetics.openmenu")) {
            p.sendMessage(MessageManager.getMessage("No-Permission"));
            p.closeInventory();
            return;
        }
        boolean chests = Core.treasureChestsEnabled();
        int add = 0;
        if (chests)
            add = 18;
        int slotAmount = 36;
        if (chests)
            slotAmount = 54;

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Main-Menu"));

        int a = Core.enabledCategories.size();

        int b = 9;
        if (a == 1)
            b = 13;
        else if (a == 2)
            b = 12;
        else if (a == 3)
            b = 11;
        else if (a == 4)
            b = 10;
        else if (a == 5)
            b = 9;

        b += add;

        for (int i = 0; i < a; i++) {
            ItemStack is = Core.enabledCategories.get(i).getItemStack().clone();
            inv.setItem(b, is);
            b += 2;
        }

        if (chests) {
            ItemStack chest;

            if (Core.getCustomPlayer(p).getKeys() == 0)
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Dont-Have-Key"), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            else
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Click-Open-Chest"), "");

            ItemStack keys = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, MessageManager.getMessage("Treasure-Keys"), "", MessageManager.getMessage("Your-Keys").replace("%keys%", Core.getCustomPlayer(p).getKeys() + ""), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            inv.setItem(15, keys);
            inv.setItem(11, chest);
        }

        inv.setItem(32 + add, ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Cosmetics")));
        if (Core.getCustomPlayer(p).hasGadgetsEnabled())
            inv.setItem(30 + add, ItemFactory.create(Material.INK_SACK, (byte) 0xa, MessageManager.getMessage("Disable-Gadgets")));
        else
            inv.setItem(30 + add, ItemFactory.create(Material.INK_SACK, (byte) 0x8, MessageManager.getMessage("Enable-Gadgets")));


        p.openInventory(inv);
    }

    public static void openPetsMenu(Player p) {
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


        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Pets"));

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
            if (SettingsManager.getConfig().get("No-Permission.Dont-Show-Item"))
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
            if (SettingsManager.getConfig().get("No-Permission.Show-In-Lore")) {
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
                is = ItemFactory.create(pet.getMaterial(), pet.getData(), toggle + " " + pet.getMenuName() + customName, lore);
            if (cp.currentPet != null && cp.currentPet.getType() == pet.getType())
                is = addGlow(is);
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 16) {
                i += 3;
            } else {
                i++;
            }
        }

        inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
        inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Pet")));

        if (SettingsManager.getConfig().get("Pets-Rename.Enabled")) {
            if (SettingsManager.getConfig().get("Pets-Rename.Permission-Required")) {
                if (p.hasPermission("ultracosmetics.pets.rename")) {
                    if (Core.getCustomPlayer(p).currentPet != null)
                        inv.setItem(inv.getSize() - 5, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet").replace("%petname%", Core.getCustomPlayer(p).currentPet.getMenuName())));
                    else
                        inv.setItem(inv.getSize() - 5, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Active-Pet-Needed")));
                }
            } else {
                if (Core.getCustomPlayer(p).currentPet != null)
                    inv.setItem(inv.getSize() - 5, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet").replace("%petname%", Core.getCustomPlayer(p).currentPet.getMenuName())));
                else
                    inv.setItem(inv.getSize() - 5, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Active-Pet-Needed")));
            }
        }

        p.openInventory(inv);
    }

    public static void openParticlesMenu(Player p) {
        int listSize = 0;
        for (ParticleEffect m : Core.getParticleEffects()) {
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

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Particle-Effects"));

        int i = 10;
        for (ParticleEffect particleEffect : Core.getParticleEffects()) {
            if (!particleEffect.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
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
            if (!particleEffect.getType().isEnabled()) continue;
            if (SettingsManager.getConfig().get("No-Permission.Dont-Show-Item"))
                if (!p.hasPermission(particleEffect.getType().getPermission()))
                    continue;
            if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(particleEffect.getType().getPermission())) {
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
                lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(particleEffect.getType().getPermission()) ? "Yes" : "No")))));
            }
            String toggle = MessageManager.getMessage("Menu.Summon");
            CustomPlayer cp = Core.getCustomPlayer(p);
            if (cp.currentParticleEffect != null && cp.currentParticleEffect.getType() == particleEffect.getType())
                toggle = MessageManager.getMessage("Menu.Unsummon");
            ItemStack is = ItemFactory.create(particleEffect.getMaterial(), particleEffect.getData(), toggle + " " + particleEffect.getName());
            if (lore != null)
                is = ItemFactory.create(particleEffect.getMaterial(), particleEffect.getData(), toggle + " " + particleEffect.getName(), lore);
            if (cp.currentParticleEffect != null && cp.currentParticleEffect.getType() == particleEffect.getType())
                is = addGlow(is);
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 16) {
                i += 3;
            } else {
                i++;
            }
        }

        inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
        inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Effect")));

        p.openInventory(inv);
    }

    public static void openMountsMenu(Player p) {
        int listSize = 0;
        for (Mount m : Core.getMounts()) {
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

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Mounts"));

        int i = 10;
        for (Mount m : Core.getMounts()) {
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
            if (SettingsManager.getConfig().get("No-Permission.Dont-Show-Item"))
                if (!p.hasPermission(m.getType().getPermission()))
                    continue;
            if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(m.getType().getPermission())) {
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
                lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(m.getType().getPermission()) ? "Yes" : "No")))));
            }
            String toggle = MessageManager.getMessage("Menu.Spawn");
            CustomPlayer cp = Core.getCustomPlayer(p);
            if (cp.currentMount != null && cp.currentMount.getType() == m.getType())
                toggle = MessageManager.getMessage("Menu.Despawn");
            ItemStack is = ItemFactory.create(m.getMaterial(), m.getData(), toggle + " " + m.getMenuName());
            if (lore != null)
                is = ItemFactory.create(m.getMaterial(), m.getData(), toggle + " " + m.getMenuName(), lore);
            if (cp.currentMount != null && cp.currentMount.getType() == m.getType())
                is = addGlow(is);
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 16) {
                i += 3;
            } else {
                i++;
            }
        }

        inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
        inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Mount")));

        p.openInventory(inv);
    }

    public static void openMorphsMenu(Player p) {
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

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Morphs"));

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
            if (SettingsManager.getConfig().get("No-Permission.Dont-Show-Item"))
                if (!p.hasPermission(m.getType().getPermission()))
                    continue;
            if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(m.getType().getPermission())) {
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
                lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(m.getType().getPermission()) ? "Yes" : "No")))));
            }
            String toggle = MessageManager.getMessage("Menu.Morph");
            CustomPlayer cp = Core.getCustomPlayer(p);
            if (cp.currentMorph != null && cp.currentMorph.getType() == m.getType())
                toggle = MessageManager.getMessage("Menu.Unmorph");
            ItemStack is = ItemFactory.create(m.getMaterial(), m.getData(), toggle + " " + m.getName());
            if (lore != null)
                is = ItemFactory.create(m.getMaterial(), m.getData(), toggle + " " + m.getName(), lore);
            if (cp.currentMorph != null && cp.currentMorph.getType() == m.getType())
                is = addGlow(is);
            ItemMeta itemMeta = is.getItemMeta();
            List<String> loreList = new ArrayList<>();
            if (itemMeta.hasLore())
                loreList = itemMeta.getLore();
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

        inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
        if (Core.getCustomPlayer(p).canSeeSelfMorph())
            inv.setItem(inv.getSize() - 5, ItemFactory.create(Material.EYE_OF_ENDER, (byte) 0x0, MessageManager.getMessage("Disable-Third-Person-View")));
        else
            inv.setItem(inv.getSize() - 5, ItemFactory.create(Material.ENDER_PEARL, (byte) 0x0, MessageManager.getMessage("Enable-Third-Person-View")));
        inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Morph")));


        p.openInventory(inv);
    }

    public static void openGadgetsMenu(Player p) {
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

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Gadgets"));

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
            if (lore != null)
                is = ItemFactory.create(g.getMaterial(), g.getData(), toggle + " " + g.getName(), lore);
            if (cp.currentGadget != null && cp.currentGadget.getType() == g.getType())
                is = addGlow(is);
            if (Core.isAmmoEnabled() && g.getType().requiresAmmo()) {
                ItemMeta itemMeta = is.getItemMeta();
                List<String> loreList = new ArrayList<>();
                if (itemMeta.hasLore())
                    loreList = itemMeta.getLore();
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

        inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
        inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Gadget")));

        p.openInventory(inv);
    }

    public static Mount.MountType getMountByName(String name) {
        for (Mount mount : Core.getMounts()) {
            if (mount.getMenuName().replace(" ", "").equals(name.replace(" ", ""))) {
                return mount.getType();
            }
        }
        return null;
    }

    public static Gadget.GadgetType getGadgetByName(String name) {
        for (Gadget g : Core.getGadgets()) {
            if (g.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return g.getType();
            }
        }
        return null;
    }

    static List<Player> playerList = new ArrayList<>();

    public static void activateMountByType(Mount.MountType type, final Player player) {
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

        for (Mount mount : Core.getMounts()) {
            if (mount.getType().isEnabled() && mount.getType() == type) {
                Class mountClass = mount.getClass();

                Class[] cArg = new Class[1];
                cArg[0] = UUID.class;

                UUID uuid = player.getUniqueId();

                try {
                    mountClass.getDeclaredConstructor(UUID.class).newInstance(uuid);
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

    public static void activatePetByType(Pet.PetType type, final Player player) {
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

        for (Pet pet : Core.getPets()) {
            if (pet.getType().isEnabled() && pet.getType() == type) {
                Class petClass = pet.getClass();

                Class[] cArg = new Class[1];
                cArg[0] = UUID.class;

                UUID uuid = player.getUniqueId();

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

    public static ParticleEffect.ParticleEffectType getParticleEffectByName(String name) {
        for (ParticleEffect particleEffect : Core.getParticleEffects()) {
            if (particleEffect.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return particleEffect.getType();
            }
        }
        return null;
    }

    public static Morph.MorphType getMorphByName(String name) {
        for (Morph morph : Core.getMorphs()) {
            if (morph.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return morph.getType();
            }
        }
        return null;
    }

    public static void activateParticleEffectByType(ParticleEffect.ParticleEffectType type, final Player player) {
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

        for (ParticleEffect particleEffect : Core.getParticleEffects()) {
            if (particleEffect.getType().isEnabled() && particleEffect.getType() == type) {
                Class particleEffectClass = particleEffect.getClass();

                Class[] cArg = new Class[1]; //Our constructor has 3 arguments
                cArg[0] = UUID.class; //First argument is of *object* type Long

                UUID uuid = player.getUniqueId();

                try {
                    particleEffectClass.getDeclaredConstructor(UUID.class).newInstance(uuid);
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

    public static void activateMorphByType(Morph.MorphType type, final Player player) {
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
        for (Morph morph : Core.getMorphs()) {
            if (morph.getType().isEnabled() && morph.getType() == type) {
                Class gadgetClass = morph.getClass();

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
                    openMainMenu((Player) event.getWhoClicked());
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

    @EventHandler
    public void mountsSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Mounts"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    openMainMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Mount"))) {
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMount != null) {
                        event.getWhoClicked().closeInventory();
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                        openMountsMenu((Player) event.getWhoClicked());
                    } else return;
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Despawn"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Spawn"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
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
                    activateMountByType(getMountByName(sb.toString()), (Player) event.getWhoClicked());
                }

            }
        }
    }

    @EventHandler
    public void mainMenuSelection(final InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Main-Menu"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu")))
                    return;
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))) {
                    openGadgetsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                    openMountsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))) {
                    openPetsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))) {
                    openParticlesMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Morphs"))) {
                    openMorphsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Cosmetics"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).clear();
                    event.getWhoClicked().closeInventory();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Enable-Gadgets"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).setGadgetsEnabled(true);
                    event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.INK_SACK, (byte) 0xa, MessageManager.getMessage("Disable-Gadgets")));
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Disable-Gadgets"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).setGadgetsEnabled(false);
                    event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.INK_SACK, (byte) 0x8, MessageManager.getMessage("Enable-Gadgets")));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void particleEffectSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Particle-Effects"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                    || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    openMainMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Effect"))) {
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentParticleEffect != null) {
                        event.getWhoClicked().closeInventory();
                        Core.getCustomPlayer((Player) event.getWhoClicked()).removeParticleEffect();
                        openParticlesMenu((Player) event.getWhoClicked());
                    } else return;
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unsummon"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeParticleEffect();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Summon"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeParticleEffect();
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replace(MessageManager.getMessage("Menu.Summon"), "");
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
                    activateParticleEffectByType(getParticleEffectByName(sb.toString()), (Player) event.getWhoClicked());
                }

            }
        }
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
                    openMainMenu((Player) event.getWhoClicked());
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
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removePet();
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

    public void openTreasureChest(Player player) {


        Class treasureChestClass = Core.getTreasureChests().get(MathUtils.randomRangeInt(0, Core.getTreasureChests().size() - 1)).getClass();

        Class[] cArg = new Class[1];
        cArg[0] = UUID.class;

        UUID uuid = player.getUniqueId();

        player.closeInventory();

        try {
            treasureChestClass.getDeclaredConstructor(UUID.class).newInstance(uuid);
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

    @EventHandler
    public void openChest(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Chests"))) {
            if (Core.getCustomPlayer((Player) event.getWhoClicked()).getKeys() > 0) {
                Cuboid c = new Cuboid(event.getWhoClicked().getLocation().add(-2, 0, -2), event.getWhoClicked().getLocation().add(2, 1, 2));
                if (!c.isEmpty()) {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Chest-Not-Enough-Space"));
                    return;
                }
                for (Entity ent : event.getWhoClicked().getNearbyEntities(5, 5, 5)) {
                    if (ent instanceof Player && Core.getCustomPlayer((Player) ent).currentTreasureChest != null) {
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().sendMessage(MessageManager.getMessage("Too-Close-To-Other-Chest"));
                        return;
                    }
                }
                if (!((Player) event.getWhoClicked()).isOnGround()) {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
                    return;
                }
                Core.getCustomPlayer((Player) event.getWhoClicked()).removeKey();
                openTreasureChest((Player) event.getWhoClicked());
            } else {
                event.getWhoClicked().closeInventory();
                Core.getCustomPlayer((Player) event.getWhoClicked()).openBuyKeyInventory();
            }
        }
    }

    @EventHandler
    public void buyKeyOpenInv(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Keys"))) {
            event.getWhoClicked().closeInventory();
            Core.getCustomPlayer((Player) event.getWhoClicked()).openBuyKeyInventory();
        }
    }

    @EventHandler
    public void buyKeyConfirm(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage("Buy-Treasure-Key"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Purchase"))) {
                if (Core.economy.getBalance((Player) event.getWhoClicked()) >= (int) SettingsManager.getConfig().get("TreasureChests.Key-Price")) {
                    Core.economy.withdrawPlayer((Player) event.getWhoClicked(), (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"));
                    Core.getCustomPlayer((Player) event.getWhoClicked()).addKey();
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Successful-Purchase"));
                    event.getWhoClicked().closeInventory();
                    MenuListener.openMainMenu((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    event.getWhoClicked().closeInventory();
                    return;
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
                event.getWhoClicked().closeInventory();
                MenuListener.openMainMenu((Player) event.getWhoClicked());
            }
        }
    }

    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @EventHandler
    public void onInventoryMoveItem(PlayerPickupItemEvent event) {
        try {
            if (event.getItem().getItemStack().hasItemMeta()
                    && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                    && UUID.fromString(event.getItem().getItemStack().getItemMeta().getDisplayName()) != null) {
                event.setCancelled(true);
            }
        } catch (Exception exception) {
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryPickupItemEvent event) {
        try {
            if (event.getInventory() != null
                    && event.getInventory().getType() == InventoryType.HOPPER) {
                if (event.getItem().getItemStack().hasItemMeta()
                        && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                        && UUID.fromString(event.getItem().getItemStack().getItemMeta().getDisplayName()) != null) {
                    event.setCancelled(true);
                }
            }
        } catch (Exception exception) {
        }
    }

    // ************ Pet renaming ************

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
                    if (SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Enabled")) {
                        buyRenamePet(p, event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace('&', '§').replace(" ", ""));
                    } else {
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
