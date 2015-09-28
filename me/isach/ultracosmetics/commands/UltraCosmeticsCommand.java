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
import org.bukkit.Bukkit;
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

            if (args != null && args.length > 1) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (args[1].equalsIgnoreCase("ammo")) {
                        if (args.length != 5) {
                            sender.sendMessage("§l§oCosmetics > §c§l/uc give ammo <gadget> <amount> <player>");
                            return true;
                        } else {
                            Player giveTo = Bukkit.getPlayer(args[4]);
                            if (giveTo == null) {
                                sender.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                                return true;
                            }
                            int amount;
                            try {
                                amount = Integer.parseInt(args[3]);
                            } catch (Exception exc) {
                                sender.sendMessage("§l§oCosmetics > §c§l" + args[2] + " isn't a valid number.");
                                return true;
                            }
                            for (Gadget g : Core.getGadgets()) {
                                if (g.getType().toString().toLowerCase().equalsIgnoreCase(args[2].toLowerCase())) {
                                    if (!g.getType().isEnabled()) {
                                        sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                                    } else if (!g.getType().requiresAmmo()) {
                                        sender.sendMessage("§l§oCosmetics > §c§lThis gadget doesn't require ammo.");
                                    } else {
                                        Core.getCustomPlayer(giveTo).addAmmo(g.getType().toString().toLowerCase(), amount);
                                        sender.sendMessage("§l§oCoscmetics > §c§lSuccesfully given " + amount + " " + g.getType().toString().toLowerCase() + " ammo to " + giveTo.getName());
                                    }
                                    return true;
                                }
                            }
                            sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                        }
                    } else if (args[1].equalsIgnoreCase("key")) {
                        if (args.length != 4) {
                            sender.sendMessage("§l§oCosmetics > §c§l/uc give key <amount> <player>");
                            return true;
                        } else {
                            Player giveTo = Bukkit.getPlayer(args[3]);
                            if (giveTo == null) {
                                sender.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                                return true;
                            }
                            int amount;
                            try {
                                amount = Integer.parseInt(args[2]);
                            } catch (Exception exc) {
                                sender.sendMessage("§l§oCosmetics > §c§l" + args[2] + " isn't a valid number.");
                                return true;
                            }

                            for (int i = 0; i < amount; i++)
                                Core.getCustomPlayer(giveTo).addKey();
                        }
                    }
                }
            }

            return true;
        }
        Player player = (Player) sender;
        if (args == null || args.length == 0) {
            sender.sendMessage(getHelp());
            return true;
        } else {
            String argZero = args[0];
            if (argZero.equalsIgnoreCase("toggle")) {

                if (!sender.hasPermission("ultracosmetics.commands.toggle")) {
                    noPerm(player);
                    return true;
                }

                if (args.length < 3) {
                    player.sendMessage("§l§oCosmetics > §c§l/uc toggle <type> <cosmetic> [player]");
                    return true;
                }
                String argOne = args[1];
                String argTwo = args[2];
                if (argOne.equalsIgnoreCase("gadget")) {
                    // /uc give gadget <gadget> [player]

                    if (!Core.Category.GADGETS.isEnabled()) {
                        player.sendMessage("§l§oCosmetics > §c§lGadgets aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (args.length != 3 && args.length != 4) {
                        player.sendMessage("§l§oCosmetics > §c§l/uc toggle gadget <gadget> [player]");
                        return true;
                    }

                    Player giveTo = player;

                    if (args.length == 4 && sender.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(args[3]) == null) {
                            player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(args[3]);
                    }
                    if (Core.getCustomPlayer(giveTo).currentGadget != null) {
                        Core.getCustomPlayer(giveTo).removeGadget();
                        return true;
                    }
                    for (Gadget g : Core.getGadgets()) {
                        if (g.getType().toString().toLowerCase().equalsIgnoreCase(args[2].toLowerCase())) {
                            if (!g.getType().isEnabled()) {
                                sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            } else {
                                MenuListener.activateGadgetByType(g.getType(), giveTo);
                            }
                            return true;
                        }
                    }
                    player.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                    return true;
                } else if (argOne.equalsIgnoreCase("effect")) {

                    if (!Core.Category.EFFECTS.isEnabled()) {
                        player.sendMessage("§l§oCosmetics > §c§lParticle Effects aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (args.length != 3 && args.length != 4) {
                        player.sendMessage("§l§oCosmetics > §c§l/uc toggle effect <effect> [player]");
                        return true;
                    }

                    Player giveTo = player;

                    if (args.length == 4 && sender.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(args[3]) == null) {
                            player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(args[3]);
                    }
                    if (Core.getCustomPlayer(giveTo).currentParticleEffect != null) {
                        Core.getCustomPlayer(giveTo).removeParticleEffect();
                        return true;
                    }
                    for (ParticleEffect effect : Core.getParticleEffects()) {
                        if (effect.getType().toString().toLowerCase().equalsIgnoreCase(args[2].toLowerCase())) {
                            if (!effect.getType().isEnabled()) {
                                sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            } else {
                                MenuListener.activateParticleEffectByType(effect.getType(), giveTo);
                            }
                            return true;
                        }
                    }
                    player.sendMessage(MessageManager.getMessage("Invalid-Particle-Effect"));
                    return true;
                } else if (argOne.equalsIgnoreCase("pet")) {

                    if (!Core.Category.PETS.isEnabled()) {
                        player.sendMessage("§l§oCosmetics > §c§lPets aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (args.length != 3 && args.length != 4) {
                        player.sendMessage("§l§oCosmetics > §c§l/uc toggle pet <pet> [player]");
                        return true;
                    }

                    Player giveTo = player;

                    if (args.length == 4
                            && sender.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(args[3]) == null) {
                            player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(args[3]);
                    }
                    if (Core.getCustomPlayer(giveTo).currentPet != null) {
                        Core.getCustomPlayer(giveTo).removePet();
                        return true;
                    }
                    for (Pet pet : Core.getPets()) {
                        if (pet.getType().toString().toLowerCase().equalsIgnoreCase(args[2].toLowerCase())) {
                            if (!pet.getType().isEnabled()) {
                                sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            } else {
                                MenuListener.activatePetByType(pet.getType(), giveTo);
                            }
                            return true;
                        }
                    }
                    player.sendMessage(MessageManager.getMessage("Invalid-Pet"));
                    return true;
                } else if (argOne.equalsIgnoreCase("morph")) {

                    if (!Core.Category.MORPHS.isEnabled()) {
                        player.sendMessage("§l§oCosmetics > §c§lGadgets aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (args.length != 3 && args.length != 4) {
                        player.sendMessage("§l§oCosmetics > §c§l/uc toggle morph <morph> [player]");
                        return true;
                    }

                    Player giveTo = player;

                    if (args.length == 4 && sender.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(args[3]) == null) {
                            player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(args[3]);
                    }
                    if (Core.getCustomPlayer(giveTo).currentMorph != null) {
                        Core.getCustomPlayer(giveTo).removeMorph();
                        return true;
                    }
                    for (Morph morph : Core.getMorphs()) {
                        if (morph.getType().toString().toLowerCase().equalsIgnoreCase(args[2].toLowerCase())) {
                            if (!morph.getType().isEnabled()) {
                                sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            } else {
                                MenuListener.activateMorphByType(morph.getType(), giveTo);
                            }
                            return true;
                        }
                    }
                    player.sendMessage(MessageManager.getMessage("Invalid-Morph"));
                    return true;
                } else if (argOne.equalsIgnoreCase("mount")) {

                    if (!Core.Category.MOUNTS.isEnabled()) {
                        player.sendMessage("§l§oCosmetics > §c§lMounts aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (args.length != 3 && args.length != 4) {
                        player.sendMessage("§l§oCosmetics > §c§l/uc toggle mount <mount> [player]");
                        return true;
                    }

                    Player giveTo = player;

                    if (args.length == 4 && sender.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(args[3]) == null) {
                            player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(args[3]);
                    }
                    if (Core.getCustomPlayer(giveTo).currentMount != null) {
                        Core.getCustomPlayer(giveTo).removeMount();
                        return true;
                    }
                    for (Mount mount : Core.getMounts()) {
                        if (mount.getType().toString().toLowerCase().equalsIgnoreCase(args[2].toLowerCase())) {
                            if (!mount.getType().isEnabled()) {
                                sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            } else {
                                MenuListener.activateMountByType(mount.getType(), giveTo);
                            }
                            return true;
                        }
                    }
                    player.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                    return true;
                }
            } else if (argZero.equalsIgnoreCase("renamepet")) {
                if (Core.getCustomPlayer(player).currentPet == null) {
                    player.sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                    return true;
                } else if (!(boolean) SettingsManager.getConfig().get("Pets-Rename.Enabled"))
                    return true;
                else
                    MenuListener.renamePet(player);
            } else if (argZero.equalsIgnoreCase("selfmorphview")) {
                Core.getCustomPlayer(player).setSeeSelfMorph(Core.getCustomPlayer(player).canSeeSelfMorph() ? false : true);
            } else if (argZero.equalsIgnoreCase("gadgets")) {
                Core.getCustomPlayer(player).setGadgetsEnabled(Core.getCustomPlayer(player).hasGadgetsEnabled() ? false : true);
            } else if (argZero.equalsIgnoreCase("give")) {

                if (!sender.hasPermission("ultracosmetics.commands.give")) {
                    noPerm(player);
                    return true;
                }

                if (args.length < 3) {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("key")) {
                            player.sendMessage("§l§oCosmetics > §c§l/uc give key <amount> [player]");
                            return true;
                        } else if (args[1].equalsIgnoreCase("ammo")) {
                            player.sendMessage("§l§oCosmetics > §c§l/uc give ammo <gadget> <amount> [player]");
                            return true;
                        }
                    }
                    player.sendMessage("§l§oCosmetics > §c§l/uc give <key|ammo>");
                    return true;
                }
                String argOne = args[1];
                String argTwo = args[2];

                if (argOne.equals("key")) {
                    //uc give key <amount> [player]

                    Player giveTo = player;

                    if (args.length == 4) {
                        if (Bukkit.getPlayer(args[3]) == null) {
                            player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(args[3]);
                    }
                    int amount;
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (Exception exc) {
                        player.sendMessage("§l§oCosmetics > §c§l" + args[2] + " isn't a valid number.");
                        return true;
                    }

                    for (int i = 0; i < amount; i++)
                        Core.getCustomPlayer(giveTo).addKey();
                    sender.sendMessage("§l§oCosmetics > §c§lSuccesfully given " + amount + " treasure keys to " + giveTo.getName());

                } else if (argOne.equals("ammo")) {
                    //uc give ammo <gadget> <amount> [player]

                    Player giveTo = player;

                    if (args.length < 4) {
                        player.sendMessage("§l§oCosmetics > §c§l/uc give ammo <gadget> <amount> [player]");
                        return true;
                    }

                    if (args.length == 5) {
                        if (Bukkit.getPlayer(args[4]) == null) {
                            player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(args[4]);
                    }
                    int amount;
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (Exception exc) {
                        player.sendMessage("§l§oCosmetics > §c§l" + args[3] + " isn't a valid number.");
                        return true;
                    }

                    for (Gadget g : Core.getGadgets()) {
                        if (g.getType().toString().toLowerCase().equalsIgnoreCase(args[2].toLowerCase())) {
                            if (!g.getType().isEnabled()) {
                                sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            } else if (!g.getType().requiresAmmo()) {
                                sender.sendMessage("§l§oCosmetics > §c§lThis gadget doesn't require ammo.");
                            } else {
                                Core.getCustomPlayer(giveTo).addAmmo(g.getType().toString().toLowerCase(), amount);
                                sender.sendMessage("§l§oCoscmetics > §c§lSuccesfully given " + amount + " " + g.getType().toString().toLowerCase() + " ammo to " + giveTo.getName());
                            }
                            return true;
                        }
                    }
                    player.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                }

            } else if (argZero.equalsIgnoreCase("clear")) {

                Player giveTo = player;

                if (!player.hasPermission("ultracosmetics.command.clear")) {
                    noPerm(player);
                    return true;
                }

                if (args.length == 2 && sender.hasPermission("ultracosmetics.commands.clear.others")) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                        return true;
                    }
                    giveTo = Bukkit.getPlayer(args[1]);
                }

                Core.getCustomPlayer(giveTo).clear();
                return true;
            } else if (argZero.equalsIgnoreCase("chest")) {
                int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
                Player giveTo = player;

                if (!player.hasPermission("ultracosmetics.commands.chest")) {
                    noPerm(player);
                    return true;
                }

                if (args.length == 2 && sender.hasPermission("ultracosmetics.commands.chest.others")) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        player.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                        return true;
                    }
                    giveTo = Bukkit.getPlayer(args[1]);
                }
                if (giveTo.getInventory().getItem(slot) != null) {
                    if (giveTo.getInventory().getItem(slot).hasItemMeta()
                            && giveTo.getInventory().getItem(slot).getItemMeta().hasDisplayName()
                            && giveTo.getInventory().getItem(slot).getItemMeta().getDisplayName().equals(((String) SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
                        return true;
                    }
                    giveTo.getWorld().dropItemNaturally(giveTo.getLocation(), giveTo.getInventory().getItem(slot));
                    giveTo.getInventory().remove(slot);
                    giveTo.getInventory().setItem(slot, null);
                }
                String name = ((String) SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
                byte data = ((Integer) SettingsManager.getConfig().get("Menu-Item.Data")).byteValue();
                player.getInventory().setItem(slot, ItemFactory.create(material, data, name));
                return true;
            } else if (argZero.equalsIgnoreCase("reload")) {
                if (sender.hasPermission("ultracosmetics.commands.reload")) {
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
                    if (args[1].startsWith("gadget")) {
                        if (Core.Category.GADGETS.isEnabled())
                            MenuListener.openGadgetsMenu((Player) sender);
                    } else if (args[1].startsWith("effect")) {
                        if (Core.Category.EFFECTS.isEnabled())
                            MenuListener.openParticlesMenu((Player) sender);
                    } else if (args[1].startsWith("mount")) {
                        if (Core.Category.MOUNTS.isEnabled())
                            MenuListener.openMountsMenu((Player) sender);
                    } else if (args[1].startsWith("pet")) {
                        if (Core.Category.PETS.isEnabled())
                            MenuListener.openPetsMenu((Player) sender);
                    } else if (args[1].equalsIgnoreCase("main")) {
                        MenuListener.openMainMenu((Player) sender);
                    } else if (args[1].startsWith("morph")) {
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

    private void noPerm(Player p) {
        p.sendMessage(MessageManager.getMessage("No-Permission"));
    }

    public String getHelp() {
        return "\n§r"
                + "  §f§lUltra Cosmetics Help (/uc)" + "\n§r"
                + "      §8┃ §7/uc reload §fReloads the config" + "\n§r"
                + "      §8┃ §7/uc menu [menu] §fOpens a menu" + "\n§r"
                + "      §8┃ §7/uc toggle <type> <cosmetic> [player] §fToggles a gadget" + "\n§r"
                + "      §8┃ §7/uc give key <amount> [player] §fGive key" + "\n§r"
                + "      §8┃ §7/uc give ammo <gadget> <amount> [player] §fGive ammo" + "\n§r"
                + "      §8┃ §7/uc clear [player]§f Clears current cosmetics" + "\n§r"
                + "      §8┃ §7/uc chest [player]§f Gets the menu item." + "\n§r"
                + "      §8┃ §7/uc gadgets§f Toggle gadgets" + "\n§r"
                + "      §8┃ §7/uc selfmorphview§f Toggle Self Morph View" + "\n§r";
    }
}
