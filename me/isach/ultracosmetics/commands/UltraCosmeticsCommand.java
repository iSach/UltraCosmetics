package me.isach.ultracosmetics.commands;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.CustomPlayer;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.mounts.Mount;
import me.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import me.isach.ultracosmetics.cosmetics.pets.Pet;
import me.isach.ultracosmetics.listeners.MenuListener;
import org.bukkit.Bukkit;
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
        if (args == null || args.length == 0) {
            sender.sendMessage(getHelp());
            return true;
        } else {
            String argZero = args[0];
            if (argZero.equalsIgnoreCase("gadget")) {
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc gadget <gadget>");
                    return true;
                }
                if(args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player)sender).removeGadget();
                    return true;
                }
                boolean gadgetFound = false;
                for (Gadget g : Core.gadgetList) {
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
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc effect <effect>");
                    return true;
                }
                if(args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player)sender).removeParticleEffect();
                    return true;
                }
                boolean effectFound = false;
                for (ParticleEffect particleEffect : Core.particleEffectList) {
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
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc mount <mount>");
                    return true;
                }
                if(args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player)sender).removeMount();
                    return true;
                }
                boolean mountFound = false;
                for (Mount mount : Core.mountList) {
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
                if (args.length != 2) {
                    sender.sendMessage("§l§oCosmetics > §7/uc pet <pet>");
                    return true;
                }
                if(args[1].equalsIgnoreCase("clear")) {
                    Core.getCustomPlayer((Player)sender).removePet();
                    return true;
                }
                boolean petFound = false;
                for (Pet pet : Core.petList) {
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
            } else if (argZero.equalsIgnoreCase("clear")) {
                Core.getCustomPlayer((Player) sender).clear();
                return true;
            }  else if (argZero.equalsIgnoreCase("reload")) {
                if(((Player)sender).hasPermission("ultracosmetics.reload")) {
                    SettingsManager.getConfig().reload();
                    SettingsManager.getMessages().reload();
                    sender.sendMessage("§l§oCosmetics > §c§lConfig and messages Reloaded!");
                } else {
                    sender.sendMessage(MessageManager.getMessage("No-Permission"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("menu")) {
                if (args.length == 1) {
                    if (Core.getCustomPlayer((Player) sender).currentMenu == CustomPlayer.MenuCategory.GADGETS) {
                        MenuListener.openGadgetsMenu((Player) sender);
                    } else if (Core.getCustomPlayer((Player) sender).currentMenu == CustomPlayer.MenuCategory.PARTICLEEFFECTS) {
                        MenuListener.openParticlesMenu((Player) sender);
                    } else if (Core.getCustomPlayer((Player) sender).currentMenu == CustomPlayer.MenuCategory.MOUNTS) {
                        MenuListener.openMountsMenu((Player) sender);
                    } else if (Core.getCustomPlayer((Player) sender).currentMenu == CustomPlayer.MenuCategory.PETS) {
                        MenuListener.openPetsMenu((Player) sender);
                    }
                    return true;
                } else if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("gadgets"))
                        MenuListener.openGadgetsMenu((Player) sender);
                    else if (args[1].equalsIgnoreCase("effects"))
                        MenuListener.openParticlesMenu((Player) sender);
                    else if (args[1].equalsIgnoreCase("mounts"))
                        MenuListener.openMountsMenu((Player) sender);
                    else if (args[1].equalsIgnoreCase("pets")) {
                        MenuListener.openPetsMenu((Player) sender);
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
                + "§b§lUltra Cosmetics  §8┃ §f§lUltra Cosmetics Help (/uc)" + "\n§r"
                + "                         §8┃ §7/uc reload §f- Reloads the config" + "\n§r"
                + "                         §8┃ §7/uc menu [menu] §f- Opens a menu" + "\n§r"
                + "                         §8┃ §7/uc gadget <gadget> §f- Toggles a gadget" + "\n§r"
                + "                         §8┃ §7/uc pet <pet> §f- Toggles a pet" + "\n§r"
                + "                         §8┃ §7/uc effect <effect> §f- Toggles an effect" + "\n§r"
                + "                         §8┃ §7/uc mount <mount> §f- Toggles a mount" + "\n§r"
                + "                         §8┃ §7/uc clear §f- Clear your current cosmetics" + "\n§r";
    }
}
