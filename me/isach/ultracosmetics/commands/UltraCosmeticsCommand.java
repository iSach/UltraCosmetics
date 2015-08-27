package me.isach.ultracosmetics.commands;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.CustomPlayer;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.morphs.Morph;
import me.isach.ultracosmetics.cosmetics.mounts.Mount;
import me.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import me.isach.ultracosmetics.cosmetics.pets.Pet;
import me.isach.ultracosmetics.listeners.MenuListener;
import me.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by sacha on 14/08/15.
 */
public class UltraCosmeticsCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args == null || args.length == 0) {
            sender.sendMessage(getHelp());
            return true;
        } else {
            String argZero = args[0];
            if (argZero.equalsIgnoreCase("gadget")) {
                if (!Core.Category.GADGETS.isEnabled()) {
                    sender.sendMessage("§c§lGadgets are disabled!");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc gadget <gadget>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player) sender).removeGadget();
                    return true;
                }
                boolean gadgetFound = false;
                for (Gadget g : Core.getGadgets()) {
                    if (g.getType().toString().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                        if (!g.getType().isEnabled()) {
                            sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            return true;
                        } else {
                            MenuListener.activateGadgetByType(g.getType(), (Player) sender);
                        }
                        gadgetFound = true;
                    }
                }
                if (!gadgetFound) {
                    sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("effect")) {
                if (!Core.Category.EFFECTS.isEnabled()) {
                    sender.sendMessage("§c§lParticle Effects are disabled!");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc effect <effect>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player) sender).removeParticleEffect();
                    return true;
                }
                boolean effectFound = false;
                for (ParticleEffect particleEffect : Core.getParticleEffects()) {
                    if (particleEffect.getType().toString().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                        if (!particleEffect.getType().isEnabled()) {
                            sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            return true;
                        } else {
                            MenuListener.activateParticleEffectByType(particleEffect.getType(), (Player) sender);
                        }
                        effectFound = true;
                    }
                }
                if (!effectFound) {
                    sender.sendMessage(MessageManager.getMessage("Invalid-Effect"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("mount")) {
                if (!Core.Category.MOUNTS.isEnabled()) {
                    sender.sendMessage("§c§lMounts are disabled!");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc mount <mount>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player) sender).removeMount();
                    return true;
                }
                boolean mountFound = false;
                for (Mount mount : Core.getMounts()) {
                    if (mount.getType().toString().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                        if (!mount.getType().isEnabled()) {
                            sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            return true;
                        } else {
                            MenuListener.activateMountByType(mount.getType(), (Player) sender);
                        }
                        mountFound = true;
                    }
                }
                if (!mountFound) {
                    sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("pet")) {
                if (!Core.Category.PETS.isEnabled()) {
                    sender.sendMessage("§c§lPets are disabled!");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc pet <pet>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player) sender).removePet();
                    return true;
                }
                boolean petFound = false;
                for (Pet pet : Core.getPets()) {
                    if (pet.getType().toString().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                        if (!pet.getType().isEnabled()) {
                            sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            return true;
                        } else {
                            MenuListener.activatePetByType(pet.getType(), (Player) sender);
                        }
                        petFound = true;
                    }
                }
                if (!petFound) {
                    sender.sendMessage(MessageManager.getMessage("Invalid-Pet"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("morph")) {
                if (!Core.Category.MORPHS.isEnabled()) {
                    sender.sendMessage("§c§lMorphs are disabled!");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc morph <morph>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player) sender).removeMorph();
                    return true;
                }
                boolean morphFound = false;
                for (Morph morph : Core.getMorphs()) {
                    if (morph.getType().toString().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                        if (!morph.getType().isEnabled()) {
                            sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            return true;
                        } else {
                            MenuListener.activateMorphByType(morph.getType(), (Player) sender);
                        }
                        morphFound = true;
                    }
                }
                if (!morphFound) {
                    sender.sendMessage(MessageManager.getMessage("Invalid-Morph"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("clear")) {
                Core.getCustomPlayer((Player) sender).clear();
                return true;
            } else if (argZero.equalsIgnoreCase("chest")) {
                int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
                if (player.getInventory().getItem(slot) != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItem(slot));
                    player.getInventory().remove(slot);
                    player.getInventory().setItem(slot, null);
                }
                String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
                byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
                player.getInventory().setItem(slot, ItemFactory.create(material, data, name));
                return true;
            } else if (argZero.equalsIgnoreCase("reload")) {
                if (sender.hasPermission("ultracosmetics.reload")) {
                    SettingsManager.getConfig().reload();
                    SettingsManager.getMessages().reload();
                    Core.enabledCategories.clear();
                    for (CustomPlayer cp : Core.getCustomPlayers())
                        cp.clear();
                    for (Core.Category c : Core.Category.values()) {
                        if (c.isEnabled())
                            Core.enabledCategories.add(c);
                    }
                    sender.sendMessage("§l§oCosmetics > §c§lConfig and messages Reloaded!");
                } else {
                    sender.sendMessage(MessageManager.getMessage("No-Permission"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("menu")) {
                if (args.length == 1) {
                    MenuListener.openMainMenu((Player) sender);
                    return true;
                } else if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("gadgets")) {
                        if (Core.Category.GADGETS.isEnabled())
                            MenuListener.openGadgetsMenu((Player) sender);
                    } else if (args[1].equalsIgnoreCase("effects")) {
                        if (Core.Category.EFFECTS.isEnabled())
                            MenuListener.openParticlesMenu((Player) sender);
                    } else if (args[1].equalsIgnoreCase("mounts")) {
                        if (Core.Category.MOUNTS.isEnabled())
                            MenuListener.openMountsMenu((Player) sender);
                    } else if (args[1].equalsIgnoreCase("pets")) {
                        if (Core.Category.PETS.isEnabled())
                            MenuListener.openPetsMenu((Player) sender);
                    } else if (args[1].equalsIgnoreCase("main")) {
                        MenuListener.openMainMenu((Player) sender);
                    } else if (args[1].equalsIgnoreCase("morph")) {
                        MenuListener.openMorphsMenu((Player) sender);
                    } else {
                        sender.sendMessage(MessageManager.getMessage("Invalid-Menu"));
                    }
                    return true;
                }


            }
            return true;
        }
    }

    public String getHelp() {
        return "\n§r"
                + "§b§lCosmetics §8┃ §f§lUltra Cosmetics Help (/uc)" + "\n§r"
                + "                  §8┃ §7/uc reload §f- Reloads the config" + "\n§r"
                + "                  §8┃ §7/uc menu [menu] §f- Opens a menu" + "\n§r"
                + "                  §8┃ §7/uc gadget <gadget/clear> §f- Toggles a gadget" + "\n§r"
                + "                  §8┃ §7/uc pet <pet/clear> §f- Toggles a pet" + "\n§r"
                + "                  §8┃ §7/uc effect <effect/clear> §f- Toggles an effect" + "\n§r"
                + "                  §8┃ §7/uc mount <mount/clear> §f- Toggles a mount" + "\n§r"
                + "                  §8┃ §7/uc clear §f- Clears your current cosmetics" + "\n§r"
                + "                  §8┃ §7/uc chest §f- Gets the menu item." + "\n§r";
    }
}
