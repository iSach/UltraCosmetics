package be.isach.ultracosmetics.commands;

import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.listeners.MenuListener;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
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
    public boolean onCommand(final CommandSender SENDER, Command command, String label, final String[] ARGS) {
        if (!(SENDER instanceof Player)) {

            if (ARGS != null && ARGS.length > 1) {
                if (ARGS[0].equalsIgnoreCase("give")) {
                    if (ARGS[1].equalsIgnoreCase("ammo")) {
                        if (ARGS.length != 5) {
                            SENDER.sendMessage("§l§oCosmetics > §c§l/uc give ammo <gadget> <amount> <player>");
                            return true;
                        } else {
                            Player giveTo = Bukkit.getPlayer(ARGS[4]);
                            if (giveTo == null) {
                                SENDER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                                return true;
                            }
                            int amount;
                            try {
                                amount = Integer.parseInt(ARGS[3]);
                            } catch (Exception exc) {
                                SENDER.sendMessage("§l§oCosmetics > §c§l" + ARGS[2] + " isn't a valid number.");
                                return true;
                            }
                            for (Gadget g : Core.getGadgets()) {
                                if (g.getType().toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase())) {
                                    if (!g.getType().isEnabled()) {
                                        SENDER.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                                    } else if (!g.getType().requiresAmmo()) {
                                        SENDER.sendMessage("§l§oCosmetics > §c§lThis gadget doesn't require ammo.");
                                    } else {
                                        Core.getCustomPlayer(giveTo).addAmmo(g.getType().toString().toLowerCase(), amount);
                                        SENDER.sendMessage("§l§oCoscmetics > §c§lSuccesfully given " + amount + " " + g.getType().toString().toLowerCase() + " ammo to " + giveTo.getName());
                                    }
                                    return true;
                                }
                            }
                            SENDER.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                        }
                    } else if (ARGS[1].equalsIgnoreCase("key")) {
                        if (ARGS.length != 4) {
                            SENDER.sendMessage("§l§oCosmetics > §c§l/uc give key <amount> <player>");
                            return true;
                        } else {
                            Player giveTo = Bukkit.getPlayer(ARGS[3]);
                            if (giveTo == null) {
                                SENDER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                                return true;
                            }
                            int amount;
                            try {
                                amount = Integer.parseInt(ARGS[2]);
                            } catch (Exception exc) {
                                SENDER.sendMessage("§l§oCosmetics > §c§l" + ARGS[2] + " isn't a valid number.");
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
        final Player PLAYER = (Player) SENDER;
        if (ARGS == null || ARGS.length == 0) {
            SENDER.sendMessage(getHelp());
            return true;
        } else {
            String argZero = ARGS[0];
            if (argZero.equalsIgnoreCase("toggle")) {

                if (!SENDER.hasPermission("ultracosmetics.commands.toggle")) {
                    noPerm(PLAYER);
                    return true;
                }

                if (ARGS.length < 3) {
                    PLAYER.sendMessage("§l§oCosmetics > §c§l/uc toggle <type> <cosmetic> [player]");
                    return true;
                }
                String argOne = ARGS[1];
                String argTwo = ARGS[2];
                if (argOne.equalsIgnoreCase("gadget")) {
                    // /uc give gadget <gadget> [player]

                    if (!Core.Category.GADGETS.isEnabled()) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lGadgets aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (ARGS.length != 3 && ARGS.length != 4) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l/uc toggle gadget <gadget> [player]");
                        return true;
                    }

                    Player giveTo = PLAYER;

                    if (ARGS.length == 4 && SENDER.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(ARGS[3]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[3]);
                    }
                    Gadget.GadgetType gadgetType = null;

                    for (Gadget gadget : Core.getGadgets())
                        if (gadget.getType().toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase()))
                            if (gadget.getType().isEnabled())
                                gadgetType = gadget.getType();
                    try {
                        if (Core.getCustomPlayer(giveTo).currentGadget != null) {
                            if (Core.getCustomPlayer(giveTo).currentGadget.getType() == gadgetType) {
                                Core.getCustomPlayer(giveTo).removeGadget();
                                return true;
                            }
                            Core.getCustomPlayer(giveTo).removeGadget();
                        }
                        MenuListener.activateGadgetByType(gadgetType, giveTo);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        PLAYER.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                    }
                    return true;
                } else if (argOne.equalsIgnoreCase("effect")) {

                    if (!Core.Category.EFFECTS.isEnabled()) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lParticle Effects aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (ARGS.length != 3 && ARGS.length != 4) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l/uc toggle effect <effect> [player]");
                        return true;
                    }

                    Player giveTo = PLAYER;

                    if (ARGS.length == 4 && SENDER.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(ARGS[3]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[3]);
                    }
                    ParticleEffect.ParticleEffectType effectToGive = null;

                    for (ParticleEffect particleEffect : Core.getParticleEffects())
                        if (particleEffect.getType().toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase()))
                            if (particleEffect.getType().isEnabled())
                                effectToGive = particleEffect.getType();
                    try {
                        if (Core.getCustomPlayer(giveTo).currentParticleEffect != null) {
                            if (Core.getCustomPlayer(giveTo).currentParticleEffect.getType() == effectToGive) {
                                Core.getCustomPlayer(giveTo).removeParticleEffect();
                                return true;
                            }
                            Core.getCustomPlayer(giveTo).removeParticleEffect();
                        }
                        MenuListener.activateParticleEffectByType(effectToGive, giveTo);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        PLAYER.sendMessage(MessageManager.getMessage("Invalid-Particle-Effect"));
                    }
                    return true;
                } else if (argOne.equalsIgnoreCase("pet")) {

                    if (!Core.Category.PETS.isEnabled()) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lPets aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (ARGS.length != 3 && ARGS.length != 4) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l/uc toggle pet <pet> [player]");
                        return true;
                    }

                    Player giveTo = PLAYER;

                    if (ARGS.length == 4
                            && SENDER.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(ARGS[3]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[3]);
                    }
                    Pet.PetType petToGive = null;

                    for (Pet pet : Core.getPets())
                        if (pet.getType().toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase()))
                            if (pet.getType().isEnabled())
                                petToGive = pet.getType();
                    try {
                        if (Core.getCustomPlayer(giveTo).currentPet != null) {
                            if (Core.getCustomPlayer(giveTo).currentPet.getType() == petToGive) {
                                Core.getCustomPlayer(giveTo).removePet();
                                return true;
                            }
                            Core.getCustomPlayer(giveTo).removePet();
                        }
                        MenuListener.activatePetByType(petToGive, giveTo);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        PLAYER.sendMessage(MessageManager.getMessage("Invalid-Pet"));
                    }
                    return true;
                } else if (argOne.equalsIgnoreCase("morph")) {

                    if (!Core.Category.MORPHS.isEnabled()) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lGadgets aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (ARGS.length != 3 && ARGS.length != 4) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l/uc toggle morph <morph> [player]");
                        return true;
                    }

                    Player giveTo = PLAYER;

                    if (ARGS.length == 4 && SENDER.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(ARGS[3]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[3]);
                    }
                    Morph.MorphType morphToGive = null;

                    for (Morph morph : Core.getMorphs())
                        if (morph.getType().toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase()))
                            if (morph.getType().isEnabled())
                                morphToGive = morph.getType();
                    try {
                        if (Core.getCustomPlayer(giveTo).currentMorph != null) {
                            if (Core.getCustomPlayer(giveTo).currentMorph.getType() == morphToGive) {
                                Core.getCustomPlayer(giveTo).removeMorph();
                                return true;
                            }
                            Core.getCustomPlayer(giveTo).removeMorph();
                        }
                        MenuListener.activateMorphByType(morphToGive, giveTo);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        PLAYER.sendMessage(MessageManager.getMessage("Invalid-Morph"));
                    }
                    return true;
                } else if (argOne.equalsIgnoreCase("mount")) {

                    if (!Core.Category.MOUNTS.isEnabled()) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lMounts aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (ARGS.length != 3 && ARGS.length != 4) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l/uc toggle mount <mount> [player]");
                        return true;
                    }

                    Player giveTo = PLAYER;

                    if (ARGS.length == 4 && SENDER.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(ARGS[3]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[3]);
                    }
                    Mount.MountType mountToGive = null;

                    for (Mount mount : Core.getMounts())
                        if (mount.getType().toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase()))
                            if (mount.getType().isEnabled())
                                mountToGive = mount.getType();
                    try {
                        if (Core.getCustomPlayer(giveTo).currentMount != null) {
                            if (Core.getCustomPlayer(giveTo).currentMount.getType() == mountToGive) {
                                Core.getCustomPlayer(giveTo).removeMount();
                                return true;
                            }
                            Core.getCustomPlayer(giveTo).removeMount();
                        }
                        MenuListener.activateMountByType(mountToGive, giveTo);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        PLAYER.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                    }
                    return true;
                } else if (argOne.equalsIgnoreCase("hat")) {

                    if (!Core.Category.HATS.isEnabled()) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lHats aren't enabled!");
                        return true;
                    }
                    // Check if right amount of args
                    if (ARGS.length != 3 && ARGS.length != 4) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l/uc toggle hat <hat> [player]");
                        return true;
                    }

                    Player giveTo = PLAYER;

                    if (ARGS.length == 4 && SENDER.hasPermission("ultracosmetics.commands.toggle.others")) {
                        if (Bukkit.getPlayer(ARGS[3]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[3]);
                    }

                    Hat hatToGive = null;

                    for (Hat hat : Core.getHats())
                        if (hat.toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase()))
                            if (hat.isEnabled())
                                hatToGive = hat;
                    try {
                        if (Core.getCustomPlayer(giveTo).currentHat != null) {
                            if (Core.getCustomPlayer(giveTo).currentHat == hatToGive) {
                                Core.getCustomPlayer(giveTo).removeHat();
                                return true;
                            }
                            Core.getCustomPlayer(giveTo).removeHat();
                        }
                        Core.getCustomPlayer(giveTo).setHat(hatToGive);
                    } catch (Exception exc) {
                        PLAYER.sendMessage(MessageManager.getMessage("Invalid-Hat"));
                    }
                    return true;
                }
            } else if (argZero.equalsIgnoreCase("renamepet")) {
                if (Core.getCustomPlayer(PLAYER).currentPet == null) {
                    PLAYER.sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                    return true;
                } else if (!(boolean) SettingsManager.getConfig().get("Pets-Rename.Enabled"))
                    return true;
                else {
                    Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            MenuListener.renamePet(PLAYER);
                        }
                    }, 4);
                }
            } else if (argZero.equalsIgnoreCase("selfmorphview")) {
                Core.getCustomPlayer(PLAYER).setSeeSelfMorph(Core.getCustomPlayer(PLAYER).canSeeSelfMorph() ? false : true);
            } else if (argZero.equalsIgnoreCase("gadgets")) {
                Core.getCustomPlayer(PLAYER).setGadgetsEnabled(Core.getCustomPlayer(PLAYER).hasGadgetsEnabled() ? false : true);
            } else if (argZero.equalsIgnoreCase("give")) {

                if (!SENDER.hasPermission("ultracosmetics.commands.give")) {
                    noPerm(PLAYER);
                    return true;
                }

                if (ARGS.length < 3) {
                    if (ARGS.length == 2) {
                        if (ARGS[1].equalsIgnoreCase("key")) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§l/uc give key <amount> [player]");
                            return true;
                        } else if (ARGS[1].equalsIgnoreCase("ammo")) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§l/uc give ammo <gadget> <amount> [player]");
                            return true;
                        }
                    }
                    PLAYER.sendMessage("§l§oCosmetics > §c§l/uc give <key|ammo>");
                    return true;
                }
                String argOne = ARGS[1];
                String argTwo = ARGS[2];

                if (argOne.equals("key")) {
                    //uc give key <amount> [player]

                    Player giveTo = PLAYER;

                    if (ARGS.length == 4) {
                        if (Bukkit.getPlayer(ARGS[3]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[3]);
                    }
                    int amount;
                    try {
                        amount = Integer.parseInt(ARGS[2]);
                    } catch (Exception exc) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l" + ARGS[2] + " isn't a valid number.");
                        return true;
                    }

                    for (int i = 0; i < amount; i++)
                        Core.getCustomPlayer(giveTo).addKey();
                    SENDER.sendMessage("§l§oCosmetics > §c§lSuccesfully given " + amount + " treasure keys to " + giveTo.getName());

                } else if (argOne.equals("ammo")) {
                    //uc give ammo <gadget> <amount> [player]

                    Player giveTo = PLAYER;

                    if (ARGS.length < 4) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l/uc give ammo <gadget> <amount> [player]");
                        return true;
                    }

                    if (ARGS.length == 5) {
                        if (Bukkit.getPlayer(ARGS[4]) == null) {
                            PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                            return true;
                        }
                        giveTo = Bukkit.getPlayer(ARGS[4]);
                    }
                    int amount;
                    try {
                        amount = Integer.parseInt(ARGS[3]);
                    } catch (Exception exc) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§l" + ARGS[3] + " isn't a valid number.");
                        return true;
                    }

                    for (Gadget g : Core.getGadgets()) {
                        if (g.getType().toString().toLowerCase().equalsIgnoreCase(ARGS[2].toLowerCase())) {
                            if (!g.getType().isEnabled()) {
                                SENDER.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            } else if (!g.getType().requiresAmmo()) {
                                SENDER.sendMessage("§l§oCosmetics > §c§lThis gadget doesn't require ammo.");
                            } else {
                                Core.getCustomPlayer(giveTo).addAmmo(g.getType().toString().toLowerCase(), amount);
                                SENDER.sendMessage("§l§oCoscmetics > §c§lSuccesfully given " + amount + " " + g.getType().toString().toLowerCase() + " ammo to " + giveTo.getName());
                            }
                            return true;
                        }
                    }
                    PLAYER.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                }

            } else if (argZero.equalsIgnoreCase("clear")) {

                Player giveTo = PLAYER;

                if (!PLAYER.hasPermission("ultracosmetics.commands.clear")) {
                    noPerm(PLAYER);
                    return true;
                }

                if (ARGS.length == 2 && SENDER.hasPermission("ultracosmetics.commands.clear.others")) {
                    if (Bukkit.getPlayer(ARGS[1]) == null) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                        return true;
                    }
                    giveTo = Bukkit.getPlayer(ARGS[1]);
                }

                Core.getCustomPlayer(giveTo).clear();
                return true;
            } else if (argZero.equalsIgnoreCase("chest")) {
                int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
                Player giveTo = PLAYER;

                if (!PLAYER.hasPermission("ultracosmetics.commands.chest")) {
                    noPerm(PLAYER);
                    return true;
                }

                if (ARGS.length == 2 && SENDER.hasPermission("ultracosmetics.commands.chest.others")) {
                    if (Bukkit.getPlayer(ARGS[1]) == null) {
                        PLAYER.sendMessage("§l§oCosmetics > §c§lPlayer could not be found.");
                        return true;
                    }
                    giveTo = Bukkit.getPlayer(ARGS[1]);
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
                PLAYER.getInventory().setItem(slot, ItemFactory.create(material, data, name));
                return true;
            } else if (argZero.equalsIgnoreCase("reload")) {
                if (SENDER.hasPermission("ultracosmetics.commands.reload")) {
                    SettingsManager.getConfig().reload();
                    SettingsManager.getMessages().reload();
                    Core.enabledCategories.clear();
                    for (CustomPlayer cp : Core.getCustomPlayers())
                        cp.clear();
                    for (Core.Category c : Core.Category.values()) {
                        if (c.isEnabled())
                            Core.enabledCategories.add(c);
                    }
                    SENDER.sendMessage("§l§oCosmetics > §c§lConfig and messages Reloaded!");
                } else {
                    SENDER.sendMessage(MessageManager.getMessage("No-Permission"));
                }
                return true;
            } else if (argZero.equalsIgnoreCase("menu")) {
                Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {

                        if (ARGS.length == 1) {
                            MenuListener.openMainMenu((Player) SENDER);
                        } else if (ARGS.length > 1) {
                            if (ARGS[1].startsWith("gadget")) {
                                if (Core.Category.GADGETS.isEnabled())
                                    MenuListener.openGadgetsMenu((Player) SENDER);
                            } else if (ARGS[1].startsWith("effect")) {
                                if (Core.Category.EFFECTS.isEnabled())
                                    MenuListener.openParticlesMenu((Player) SENDER);
                            } else if (ARGS[1].startsWith("mount")) {
                                if (Core.Category.MOUNTS.isEnabled())
                                    MenuListener.openMountsMenu((Player) SENDER);
                            } else if (ARGS[1].startsWith("pet")) {
                                if (Core.Category.PETS.isEnabled())
                                    MenuListener.openPetsMenu((Player) SENDER);
                            } else if (ARGS[1].equalsIgnoreCase("main")) {
                                MenuListener.openMainMenu((Player) SENDER);
                            } else if (ARGS[1].startsWith("morph")) {
                                if (Core.Category.MORPHS.isEnabled())
                                    MenuListener.openMorphsMenu((Player) SENDER);
                            } else if (ARGS[1].startsWith("hat")) {
                                if (Core.Category.HATS.isEnabled())
                                    MenuListener.openHatsMenu((Player) SENDER);
                            } else {
                                SENDER.sendMessage(MessageManager.getMessage("Invalid-Menu"));
                            }
                        }
                    }
                }, 4);
                return true;
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
