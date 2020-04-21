package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SubCommandShowcaseToggle implements CommandExecutor {

    private UltraCosmetics plugin;

    public SubCommandShowcaseToggle(UltraCosmetics uc) {
        plugin = uc;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player sender = (Player) commandSender;

        UltraPlayer ultraPlayer = plugin.getPlayerManager().getUltraPlayer(sender);

        if (args.length < 3) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "/ucs toggle <type> <cosmetic> [npc]");
            return true;
        }

        String type = args[1].toLowerCase();
        String cosm = args[2].toLowerCase();

        if (args.length > 3) {
            try {
                if (!UltraCosmeticsData.get().getEnabledWorlds().contains(Bukkit.getPlayer(args[3]).getWorld().getName())) {
                    sender.sendMessage(MessageManager.getMessage("World-Disabled"));
                    return true;
                }
            } catch (Exception e) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                return true;
            }
        } else {
            if (!UltraCosmeticsData.get().getEnabledWorlds().contains(sender.getWorld().getName())) {
                sender.sendMessage(MessageManager.getMessage("World-Disabled"));
                return true;
            }
        }

        Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
        if (categories.length == 1) {
            Category category = (Category) categories[0];
            if (args.length > 3) {
                try {
                    UltraPlayer other = plugin.getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
                    if (category == Category.SUITS) {
                        try {
                            ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
                            other.removeSuit(armorSlot);
                        } catch (Exception ex) {
                            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
                            return true;
                        }
                    } else {
                        other.removeCosmetic(category);
                    }
                } catch (Exception exc) {
                    sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                    return true;
                }
            } else {
                if (ultraPlayer.getCosmetic(category) != null) {
                    if (category == Category.SUITS) {
                        try {
                            ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
                            ultraPlayer.removeSuit(armorSlot);
                        } catch (Exception ex) {
                            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
                        }
                    } else {
                        ultraPlayer.removeCosmetic(category);
                    }
                    return true;
                }
            }
            Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().contains(cosm.split(":")[0])).toArray();
            if (cosmeticTypes.length == 1) {
                CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];
                if (args.length > 3) {
                    try {
                        UltraPlayer other = plugin.getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
                        if (cosmeticType.getCategory() == Category.SUITS) {
                            try {
                                ArmorSlot armorSlot = ArmorSlot.getByName(cosm.split(":")[1]);
                                SuitType suitType = SuitType.valueOf(cosm.split(":")[0]);
                                suitType.equip(other, plugin, armorSlot);
                            } catch (Exception ex) {
                                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
                            }
                        } else {
                            cosmeticType.equip(other, plugin);
                        }
                    } catch (Exception exc) {
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                    }
                } else {
                    if (cosmeticType.getCategory() == Category.SUITS) {
                        try {
                            ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
                            SuitType suitType = SuitType.valueOf(args[2].split(":")[0]);
                            suitType.equip(ultraPlayer, plugin, armorSlot);
                        } catch (Exception ex) {
                            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
                        }
                    } else {
                        cosmeticType.equip(ultraPlayer, plugin);
                    }
                }
            } else {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid cosmetic.");
            }
        } else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid category.");
        }
        return false;
    }
}
