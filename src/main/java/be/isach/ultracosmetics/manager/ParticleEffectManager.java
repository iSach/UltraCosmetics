package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
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
public class ParticleEffectManager implements Listener {

    static List<Player> playerList = new ArrayList<>();

    public static void openParticlesMenu(final Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
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

                final Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Particle-Effects"));

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
                    if (cp.currentParticleEffect != null && cp.currentParticleEffect.getType() == particleEffect.getType())
                        is = ItemFactory.addGlow(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> loreList = new ArrayList<>();
                    if (particleEffect.showsDescription()) {
                        loreList.add("");
                        for (String s : particleEffect.getDescription())
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

                if (Category.EFFECTS.hasGoBackArrow())
                    inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menu.Main-Menu")));
                inv.setItem(inv.getSize() - (Category.EFFECTS.hasGoBackArrow() ? 4 : 5), ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Effect")));

                Bukkit.getScheduler().runTask(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        p.openInventory(inv);
                    }
                });
            }
        });
    }

    public static void activateParticleEffectByType(ParticleEffect.ParticleEffectType type, final Player PLAYER) {
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

        for (ParticleEffect particleEffect : Core.getParticleEffects()) {
            if (particleEffect.getType().isEnabled() && particleEffect.getType() == type) {
                Class particleEffectClass = particleEffect.getClass();

                Class[] cArg = new Class[1]; //Our constructor has 3 arguments
                cArg[0] = UUID.class; //First argument is of *object* type Long

                UUID uuid = PLAYER.getUniqueId();

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
                    MainMenuManager.openMainMenu((Player) event.getWhoClicked());
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

    public static ParticleEffect.ParticleEffectType getParticleEffectByName(String name) {
        for (ParticleEffect particleEffect : Core.getParticleEffects()) {
            if (particleEffect.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return particleEffect.getType();
            }
        }
        return null;
    }

}
