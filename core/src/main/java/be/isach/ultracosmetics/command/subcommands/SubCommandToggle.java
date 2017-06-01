package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Clear {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-21-2015
 */
public class SubCommandToggle extends SubCommand {


    public SubCommandToggle(UltraCosmetics ultraCosmetics) {
        super("Toggles a cosmetic.", "ultracosmetics.command.toggle", "/uc toggle <type> <cosmetic> [player]", ultraCosmetics, "toggle");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(sender);

        if (args.length < 3) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + getUsage());
            return;
        }

        String type = args[1].toLowerCase();
        String cosm = args[2].toLowerCase();

        Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
        if (categories.length == 1) {
            Category category = (Category) categories[0];
            if (args.length > 3) {
                try {
                    UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
                    if (other.getCosmetic(category) != null) {
                        other.removeCosmetic(category);
                        return;
                    }
                } catch (Exception exc) {
                    sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                    return;
                }
            } else {
                if (ultraPlayer.getCosmetic(category) != null) {
                    ultraPlayer.removeCosmetic(category);
                    return;
                }
            }
            Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().contains(cosm)).toArray();
            if (cosmeticTypes.length == 1) {
                CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];
                if (args.length > 3) {
                    try {
                        UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
                        cosmeticType.equip(other, getUltraCosmetics());
                    } catch (Exception exc) {
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                    }
                } else {
                    cosmeticType.equip(ultraPlayer, getUltraCosmetics());
                }
            } else {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid cosmetic.");
            }
        } else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid category.");
        }
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        if (args.length < 4) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle <type> <cosmetic> <player>");
            return;
        }

        String type = args[1].toLowerCase();
        String cosm = args[2].toLowerCase();

        Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
        if (categories.length == 1) {
            Category category = (Category) categories[0];
            try {
                UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
                if (other.getCosmetic(category) != null) {
                    other.removeCosmetic(category);
                    return;
                }
            } catch (Exception exc) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                return;
            }
            Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().startsWith(cosm)).toArray();
            if (cosmeticTypes.length == 1) {
                CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];
                try {
                    UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
                    cosmeticType.equip(other, getUltraCosmetics());
                } catch (Exception exc) {
                    sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                }
            } else {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid cosmetic.");
            }
        } else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid category.");
        }
    }
}
