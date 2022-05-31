package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

/**
 * Clear {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @author RadBuilder
 * @since 12-21-2015
 */
public class SubCommandToggle extends SubCommand {
    private static final String ERROR_PREFIX = " " + ChatColor.RED + ChatColor.BOLD;

    public SubCommandToggle(UltraCosmetics ultraCosmetics) {
        super("toggle", "Toggles a cosmetic.", "<type> <cosmetic> [player]", ultraCosmetics, true);
    }

    @Override
    protected void onExePlayer(Player sender, String[] args) {
        if (args.length < 3 || args.length > 4) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + ERROR_PREFIX + getUsage());
            return;
        }

        Player target;
        if (args.length > 3) {
            // null check later
            target = Bukkit.getPlayer(args[3]);
        } else {
            target = sender;
        }

        toggle(sender, target, args[1].toLowerCase(), args[2].toLowerCase());
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + ERROR_PREFIX + "/uc toggle <type> <cosmetic> <player>");
            return;
        }

        toggle(sender, Bukkit.getPlayer(args[3]), args[1].toLowerCase(), args[2].toLowerCase());
    }

    private void toggle(CommandSender sender, Player targetPlayer, String type, String cosm) {
        if (sender != targetPlayer && !sender.hasPermission(getPermission().getName() + ".others")) {
            error(sender, "You do not have permission to toggle cosmetics for others.");
            return;
        }

        UltraPlayer target = ultraCosmetics.getPlayerManager().getUltraPlayer(targetPlayer);
        if (target == null) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + ERROR_PREFIX + "Invalid player.");
            return;
        }

        if (!SettingsManager.isAllowedWorld(target.getBukkitPlayer().getWorld())) {
            sender.sendMessage(MessageManager.getMessage("World-Disabled"));
            return;
        }

        Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
        if (categories.length != 1) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + ERROR_PREFIX + "Invalid category.");
            return;
        }
        Category category = (Category) categories[0];
        boolean suits = category == Category.SUITS; 
        ArmorSlot slot = null;
        if (suits) {
            try {
                slot = ArmorSlot.getByName(cosm.split(":")[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + ERROR_PREFIX + "/uc toggle suit <suit type:suit piece> <player>.");
                return;
            }
            target.removeSuit(slot);
        } else {
            target.removeCosmetic(category);
        }
        Optional<? extends CosmeticType<?>> matchingType = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().contains(cosm.split(":")[0])).findFirst();
        if (!matchingType.isPresent()) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + ERROR_PREFIX + "Invalid cosmetic.");
            return;
        }

        if (!suits) {
            matchingType.get().equip(target, ultraCosmetics);
            return;
        }

        SuitType suitType;
        try {
            suitType = SuitType.getSuitPart(cosm.split(":")[0], slot);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + ERROR_PREFIX + "/uc toggle suit <suit type:suit piece> <player>.");
            return;
        }
        suitType.equip(target, ultraCosmetics);
    }
}
