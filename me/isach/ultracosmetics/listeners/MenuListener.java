package me.isach.ultracosmetics.listeners;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.CustomPlayer;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.mounts.Mount;
import me.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import me.isach.ultracosmetics.cosmetics.pets.Pet;
import me.isach.ultracosmetics.util.Cuboid;
import me.isach.ultracosmetics.util.ItemFactory;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class MenuListener implements Listener {


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Core.getCustomPlayer(event.getPlayer()).currentTreasureChest != null) {
            event.setCancelled(true);
            return;
        }
        if (event.getItem() != null
                && event.getItem().hasItemMeta()
                && event.getItem().getItemMeta().hasDisplayName()
                && event.getItem().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§"))) {
            event.setCancelled(true);
            if (Core.getCustomPlayer(event.getPlayer()).currentMenu == CustomPlayer.MenuCategory.GADGETS) {
                openGadgetsMenu(event.getPlayer());
            } else if (Core.getCustomPlayer(event.getPlayer()).currentMenu == CustomPlayer.MenuCategory.PARTICLEEFFECTS) {
                openParticlesMenu(event.getPlayer());
            } else if (Core.getCustomPlayer(event.getPlayer()).currentMenu == CustomPlayer.MenuCategory.MOUNTS) {
                openMountsMenu(event.getPlayer());
            } else if (Core.getCustomPlayer(event.getPlayer()).currentMenu == CustomPlayer.MenuCategory.PETS) {
                openPetsMenu(event.getPlayer());
            }
        }
    }

    public static void openPetsMenu(Player p) {
        int add = 0;
        if (Core.treasureChests)
            add = 9;
        int listSize = 0;
        for (Pet m : Core.petList) {
            if (!m.getType().isEnabled()) continue;
            listSize++;
        }
        int slotAmount = 45 + add;
        if (listSize < 29)
            slotAmount = 54 + add;
        if (listSize < 22)
            slotAmount = 45 + add;
        if (listSize < 15)
            slotAmount = 45;
        if (listSize < 8)
            slotAmount = 27 + add;


        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Pets"));

        ItemStack pets = ItemFactory.create(Material.MONSTER_EGG, (byte) 0, MessageManager.getMessage("Menu.Pets"));
        ItemStack particleEffects = ItemFactory.create(Material.MELON_SEEDS, (byte) 0, MessageManager.getMessage("Menu.Particle-Effects"));
        ItemStack gadgets = ItemFactory.create(Material.SLIME_BALL, (byte) 0, MessageManager.getMessage("Menu.Gadgets"));
        ItemStack mounts = ItemFactory.create(Material.SADDLE, (byte) 0, MessageManager.getMessage("Menu.Mounts"));

        pets = addGlow(pets);

        inv.setItem(1 + add, pets);
        inv.setItem(3 + add, particleEffects);
        inv.setItem(5 + add, gadgets);
        inv.setItem(7 + add, mounts);

        if (Core.treasureChests) {
            ItemStack chest;

            if (Core.getCustomPlayer(p).getKeys() == 0)
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Dont-Have-Key"), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            else
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Click-Open-Chest"), "");

            ItemStack keys = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, MessageManager.getMessage("Treasure-Keys"), "", MessageManager.getMessage("Your-Keys").replaceAll("%keys%", Core.getCustomPlayer(p).getKeys() + ""), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            inv.setItem(6, keys);
            inv.setItem(2, chest);
        }

        for (int i = 9 + add; i < 19 + add; i++)
            inv.setItem(i, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        inv.setItem(26 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        if (slotAmount > 27 + add) {
            inv.setItem(27 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            inv.setItem(35 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            if (slotAmount > 36 + add) {
                inv.setItem(36 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
                inv.setItem(44 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            }
        }

        int i = 19 + add;
        for (Pet pet : Core.petList) {
            if (!pet.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
            ItemStack is = ItemFactory.create(pet.getMaterial(), pet.getData(), toggle + " " + pet.getMenuName(), (lore != null) ? lore : null);
            if (cp.currentPet != null && cp.currentPet.getType() == pet.getType())
                is = addGlow(is);
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 43) {
                i += 3;
            } else {
                i++;
            }
        }

        p.openInventory(inv);
        Core.getCustomPlayer(p).currentMenu = CustomPlayer.MenuCategory.PETS;
    }

    public static void openParticlesMenu(Player p) {
        int add = 0;
        if (Core.treasureChests)
            add = 9;
        int listSize = 0;
        for (ParticleEffect m : Core.particleEffectList) {
            if (!m.getType().isEnabled()) continue;
            listSize++;
        }
        int slotAmount = 45 + add;
        if (listSize < 29)
            slotAmount = 54 + add;
        if (listSize < 22)
            slotAmount = 45 + add;
        if (listSize < 15)
            slotAmount = 45;
        if (listSize < 8)
            slotAmount = 27 + add;

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Particle-Effects"));

        ItemStack pets = ItemFactory.create(Material.MONSTER_EGG, (byte) 0, MessageManager.getMessage("Menu.Pets"));
        ItemStack particleEffects = ItemFactory.create(Material.MELON_SEEDS, (byte) 0, MessageManager.getMessage("Menu.Particle-Effects"));
        ItemStack gadgets = ItemFactory.create(Material.SLIME_BALL, (byte) 0, MessageManager.getMessage("Menu.Gadgets"));
        ItemStack mounts = ItemFactory.create(Material.SADDLE, (byte) 0, MessageManager.getMessage("Menu.Mounts"));

        particleEffects = addGlow(particleEffects);

        inv.setItem(1 + add, pets);
        inv.setItem(3 + add, particleEffects);
        inv.setItem(5 + add, gadgets);
        inv.setItem(7 + add, mounts);

        if (Core.treasureChests) {
            ItemStack chest;

            if (Core.getCustomPlayer(p).getKeys() == 0)
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Dont-Have-Key"), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            else
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Click-Open-Chest"), "");

            ItemStack keys = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, MessageManager.getMessage("Treasure-Keys"), "", MessageManager.getMessage("Your-Keys").replaceAll("%keys%", Core.getCustomPlayer(p).getKeys() + ""), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            inv.setItem(6, keys);
            inv.setItem(2, chest);
        }

        for (int i = 9 + add; i < 19 + add; i++)
            inv.setItem(i, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        inv.setItem(26 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        if (slotAmount > 27 + add) {
            inv.setItem(27 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            inv.setItem(35 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            if (slotAmount > 36 + add) {
                inv.setItem(36 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
                inv.setItem(44 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            }
        }
        int i = 19 + add;
        for (ParticleEffect particleEffect : Core.particleEffectList) {
            if (!particleEffect.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
            ItemStack is = ItemFactory.create(particleEffect.getMaterial(), particleEffect.getData(), toggle + " " + particleEffect.getName(), (lore != null) ? lore : null);
            if (cp.currentParticleEffect != null && cp.currentParticleEffect.getType() == particleEffect.getType())
                is = addGlow(is);
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 43) {
                i += 3;
            } else {
                i++;
            }
        }

        p.openInventory(inv);
        Core.getCustomPlayer(p).currentMenu = CustomPlayer.MenuCategory.PARTICLEEFFECTS;
    }

    public static void openMountsMenu(Player p) {

        int add = 0;
        if (Core.treasureChests)
            add = 9;
        int listSize = 0;
        for (Mount m : Core.mountList) {
            if (!m.getType().isEnabled()) continue;
            listSize++;
        }
        int slotAmount = 45 + add;
        if (listSize < 29)
            slotAmount = 54 + add;
        if (listSize < 22)
            slotAmount = 45 + add;
        if (listSize < 15)
            slotAmount = 45;
        if (listSize < 8)
            slotAmount = 27 + add;

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Mounts"));

        ItemStack pets = ItemFactory.create(Material.MONSTER_EGG, (byte) 0, MessageManager.getMessage("Menu.Pets"));
        ItemStack particleEffects = ItemFactory.create(Material.MELON_SEEDS, (byte) 0, MessageManager.getMessage("Menu.Particle-Effects"));
        ItemStack gadgets = ItemFactory.create(Material.SLIME_BALL, (byte) 0, MessageManager.getMessage("Menu.Gadgets"));
        ItemStack mounts = ItemFactory.create(Material.SADDLE, (byte) 0, MessageManager.getMessage("Menu.Mounts"));

        mounts = addGlow(mounts);

        inv.setItem(1 + add, pets);
        inv.setItem(3 + add, particleEffects);
        inv.setItem(5 + add, gadgets);
        inv.setItem(7 + add, mounts);

        if (Core.treasureChests) {
            ItemStack chest;

            if (Core.getCustomPlayer(p).getKeys() == 0)
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Dont-Have-Key"), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            else
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Click-Open-Chest"), "");

            ItemStack keys = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, MessageManager.getMessage("Treasure-Keys"), "", MessageManager.getMessage("Your-Keys").replaceAll("%keys%", Core.getCustomPlayer(p).getKeys() + ""), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            inv.setItem(6, keys);
            inv.setItem(2, chest);
        }

        for (int i = 9 + add; i < 19 + add; i++)
            inv.setItem(i, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        inv.setItem(26 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        if (slotAmount > 27 + add) {
            inv.setItem(27 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            inv.setItem(35 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            if (slotAmount > 36 + add) {
                inv.setItem(36 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
                inv.setItem(44 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            }
        }

        int i = 19 + add;
        for (Mount m : Core.mountList) {
            if (!m.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
            ItemStack is = ItemFactory.create(m.getMaterial(), m.getData(), toggle + " " + m.getMenuName(), (lore != null) ? lore : null);
            if (cp.currentMount != null && cp.currentMount.getType() == m.getType())
                is = addGlow(is);
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 43) {
                i += 3;
            } else {
                i++;
            }
        }

        p.openInventory(inv);
        Core.getCustomPlayer(p).currentMenu = CustomPlayer.MenuCategory.MOUNTS;
    }

    public static void openGadgetsMenu(Player p) {
        int add = 0;
        if (Core.treasureChests)
            add = 9;
        int listSize = 0;
        for (Gadget g : Core.gadgetList) {
            if (!g.getType().isEnabled()) continue;
            listSize++;
        }
        int slotAmount = 45 + add;
        if (listSize < 29)
            slotAmount = 54 + add;
        if (listSize < 22)
            slotAmount = 45 + add;
        if (listSize < 15)
            slotAmount = 45;
        if (listSize < 8)
            slotAmount = 27 + add;

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Gadgets"));

        ItemStack pets = ItemFactory.create(Material.MONSTER_EGG, (byte) 0, MessageManager.getMessage("Menu.Pets"));
        ItemStack particleEffects = ItemFactory.create(Material.MELON_SEEDS, (byte) 0, MessageManager.getMessage("Menu.Particle-Effects"));
        ItemStack gadgets = ItemFactory.create(Material.SLIME_BALL, (byte) 0, MessageManager.getMessage("Menu.Gadgets"));
        ItemStack mounts = ItemFactory.create(Material.SADDLE, (byte) 0, MessageManager.getMessage("Menu.Mounts"));

        gadgets = addGlow(gadgets);

        inv.setItem(1 + add, pets);
        inv.setItem(3 + add, particleEffects);
        inv.setItem(5 + add, gadgets);
        inv.setItem(7 + add, mounts);

        if (Core.treasureChests) {
            ItemStack chest;

            if (Core.getCustomPlayer(p).getKeys() == 0)
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Dont-Have-Key"), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            else
                chest = ItemFactory.create(Material.CHEST, (byte) 0x0, MessageManager.getMessage("Treasure-Chests"), "", MessageManager.getMessage("Click-Open-Chest"), "");

            ItemStack keys = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0x0, MessageManager.getMessage("Treasure-Keys"), "", MessageManager.getMessage("Your-Keys").replaceAll("%keys%", Core.getCustomPlayer(p).getKeys() + ""), "", "", MessageManager.getMessage("Click-Buy-Key"), "");
            inv.setItem(6, keys);
            inv.setItem(2, chest);
        }

        for (int i = 9 + add; i < 19 + add; i++)
            inv.setItem(i, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        inv.setItem(26 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
        if (slotAmount > 27 + add) {
            inv.setItem(27 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            inv.setItem(35 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            if (slotAmount > 36 + add) {
                inv.setItem(36 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
                inv.setItem(44 + add, ItemFactory.create(Material.STAINED_GLASS_PANE, (byte) 0xb, "§k"));
            }
        }

        int i = 19 + add;
        for (Gadget g : Core.gadgetList) {
            if (!g.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replaceAll("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 43) {
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
            ItemStack is = ItemFactory.create(g.getMaterial(), g.getData(), toggle + " " + g.getName(), (lore != null) ? lore : null);
            if (cp.currentGadget != null && cp.currentGadget.getType() == g.getType())
                is = addGlow(is);
            if (Core.ammoEnabled && g.getType().requiresAmmo()) {
                ItemMeta itemMeta = is.getItemMeta();
                List<String> loreList = itemMeta.getLore();
                loreList.add("");
                loreList.add(MessageManager.getMessage("Ammo").replaceAll("%ammo%", "" + Core.getCustomPlayer(p).getAmmo(g.getType().toString().toLowerCase())));
                loreList.add(MessageManager.getMessage("Right-Click-Buy-Ammo"));
                itemMeta.setLore(loreList);
                is.setItemMeta(itemMeta);
            }
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 43) {
                i += 3;
            } else {
                i++;
            }
        }

        p.openInventory(inv);
        Core.getCustomPlayer(p).currentMenu = CustomPlayer.MenuCategory.GADGETS;
    }

    public static Mount.MountType getMountByName(String name) {
        for (Mount mount : Core.mountList) {
            if (mount.getMenuName().equals(name)) {
                return mount.getType();
            }
        }
        return null;
    }

    public static Gadget.GadgetType getGadgetByName(String name) {
        for (Gadget g : Core.gadgetList) {
            if (g.getName().equals(name)) {
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

        for (Mount mount : Core.mountList) {
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
        for (Pet pet : Core.petList) {
            if (pet.getMenuName().equals(name)) {
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

        for (Pet pet : Core.petList) {
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
        for (ParticleEffect particleEffect : Core.particleEffectList) {
            if (particleEffect.getName().equals(name)) {
                return particleEffect.getType();
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

        for (ParticleEffect particleEffect : Core.particleEffectList) {
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
        for (Gadget g : Core.gadgetList) {
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

    @EventHandler
    public void gadgetSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Gadgets"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta())
                return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                    openMountsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))) {
                    openParticlesMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))) {
                    openPetsMenu((Player) event.getWhoClicked());
                    return;
                }
                CustomPlayer cp = Core.getCustomPlayer((Player) event.getWhoClicked());
                if (Core.ammoEnabled && event.getAction() == InventoryAction.PICKUP_HALF) {
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
                        activateGadgetByType(getGadgetByName(sb.toString()), (Player) event.getWhoClicked());
                    if (cp.currentGadget.getType().requiresAmmo())
                        cp.currentGadget.buyAmmo();
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Deactivate"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Activate"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeGadget();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < event.getCurrentItem().getItemMeta().getDisplayName().split(" ").length; i++) {
                        sb.append(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i]);
                        try {
                            if (event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i + 1] != null)
                                sb.append(" ");
                        } catch (Exception exc) {

                        }

                    }
                    activateGadgetByType(getGadgetByName(sb.toString()), (Player) event.getWhoClicked());
                    if (cp.currentGadget != null && Core.ammoEnabled && cp.getAmmo(cp.currentGadget.getType().toString().toLowerCase()) < 1 && cp.currentGadget.getType().requiresAmmo()) {
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
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))) {
                    openGadgetsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))) {
                    openParticlesMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))) {
                    openPetsMenu((Player) event.getWhoClicked());
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Despawn"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Spawn"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < event.getCurrentItem().getItemMeta().getDisplayName().split(" ").length; i++) {
                        sb.append(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i]);
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
    public void particleEffectSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Particle-Effects"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))) {
                    openGadgetsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getType() == Material.SADDLE) {
                    openMountsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))) {
                    openPetsMenu((Player) event.getWhoClicked());
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unsummon"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeParticleEffect();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Summon"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeParticleEffect();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < event.getCurrentItem().getItemMeta().getDisplayName().split(" ").length; i++) {
                        sb.append(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i]);
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
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Pets"))
                        || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Gadgets"))) {
                    openGadgetsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getType() == Material.SADDLE) {
                    openMountsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Particle-Effects"))) {
                    openParticlesMenu((Player) event.getWhoClicked());
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Despawn"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removePet();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Spawn"))) {
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removePet();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < event.getCurrentItem().getItemMeta().getDisplayName().split(" ").length; i++) {
                        sb.append(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i]);
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
        Class treasureChestClass = Core.treasureChestList.get(new Random().nextInt(Core.treasureChestList.size() - 1)).getClass();

        Class[] cArg = new Class[1];
        cArg[0] = UUID.class;

        UUID uuid = player.getUniqueId();

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
                if(!c.isEmpty()) {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Chest-Not-Enough-Space"));
                    return;
                }
                if(!((Player)event.getWhoClicked()).isOnGround()) {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
                    return;
                }
                Core.getCustomPlayer((Player) event.getWhoClicked()).removeKey();
                openTreasureChest((Player) event.getWhoClicked());
            } else {
                Inventory inventory = Bukkit.createInventory(null, 54, MessageManager.getMessage("Buy-Treasure-Key"));

                try {
                    ItemStack itemStack = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0, ChatColor.translateAlternateColorCodes('&', ((String) SettingsManager.getMessages().get("Buy-Treasure-Key-ItemName")).replaceAll("%price%", "" + (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"))));
                    inventory.setItem(13, itemStack);
                    event.getWhoClicked().getInventory().addItem(itemStack);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                for (int i = 27; i < 30; i++) {
                    inventory.setItem(i, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xb, MessageManager.getMessage("Purchase")));
                    inventory.setItem(i + 9, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xb, MessageManager.getMessage("Purchase")));
                    inventory.setItem(i + 18, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xb, MessageManager.getMessage("Purchase")));
                    inventory.setItem(i + 6, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xe, MessageManager.getMessage("Cancel")));
                    inventory.setItem(i + 9 + 6, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xe, MessageManager.getMessage("Cancel")));
                    inventory.setItem(i + 18 + 6, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xe, MessageManager.getMessage("Cancel")));
                }

                event.getWhoClicked().openInventory(inventory);
            }
        }
    }

    @EventHandler
    public void buyKeyOpenInv(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Keys"))) {

            Inventory inventory = Bukkit.createInventory(null, 54, MessageManager.getMessage("Buy-Treasure-Key"));

            ItemStack itemStack = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0, ChatColor.translateAlternateColorCodes('&', ((String) SettingsManager.getMessages().get("Buy-Treasure-Key-ItemName")).replaceAll("%price%", "" + (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"))));
            inventory.setItem(13, itemStack);

            for (int i = 27; i < 30; i++) {
                inventory.setItem(i, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xb, MessageManager.getMessage("Purchase")));
                inventory.setItem(i + 9, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xb, MessageManager.getMessage("Purchase")));
                inventory.setItem(i + 18, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xb, MessageManager.getMessage("Purchase")));
                inventory.setItem(i + 6, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xe, MessageManager.getMessage("Cancel")));
                inventory.setItem(i + 9 + 6, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xe, MessageManager.getMessage("Cancel")));
                inventory.setItem(i + 18 + 6, ItemFactory.create(Material.STAINED_CLAY, (byte) 0xe, MessageManager.getMessage("Cancel")));
            }

            event.getWhoClicked().openInventory(inventory);
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
                    if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.GADGETS) {
                        MenuListener.openGadgetsMenu((Player) event.getWhoClicked());
                    } else if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.PARTICLEEFFECTS) {
                        MenuListener.openParticlesMenu((Player) event.getWhoClicked());
                    } else if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.MOUNTS) {
                        MenuListener.openMountsMenu((Player) event.getWhoClicked());
                    } else if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.PETS) {
                        MenuListener.openPetsMenu((Player) event.getWhoClicked());
                    }
                } else {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    event.getWhoClicked().closeInventory();
                    return;
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
                event.getWhoClicked().closeInventory();
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.GADGETS) {
                    MenuListener.openGadgetsMenu((Player) event.getWhoClicked());
                } else if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.PARTICLEEFFECTS) {
                    MenuListener.openParticlesMenu((Player) event.getWhoClicked());
                } else if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.MOUNTS) {
                    MenuListener.openMountsMenu((Player) event.getWhoClicked());
                } else if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMenu == CustomPlayer.MenuCategory.PETS) {
                    MenuListener.openPetsMenu((Player) event.getWhoClicked());
                }
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
    public void onInventoryMoveItem(InventoryPickupItemEvent event) {

        if (event.getInventory() != null
                && event.getInventory().getType() == InventoryType.HOPPER) {
            if (event.getItem().getItemStack().hasItemMeta()
                    && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                    && UUID.fromString(event.getItem().getItemStack().getItemMeta().getDisplayName()) != null) {
                event.setCancelled(true);
            }
        }
    }

}
