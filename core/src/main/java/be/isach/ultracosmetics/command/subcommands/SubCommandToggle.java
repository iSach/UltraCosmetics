package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.UCTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Clear {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @author RadBuilder
 * @since 12-21-2015
 */
public class SubCommandToggle extends SubCommand {


    public SubCommandToggle(UltraCosmetics ultraCosmetics) {
        super("Toggles a cosmetic.", "ultracosmetics.command.toggle", "/uc toggle <player|npcID:npcname> <type> <cosmetic>", ultraCosmetics, "toggle");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        common(sender, args);
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        common(sender, args);
    }

    protected void common (CommandSender sender, String... args) {
        if (args.length < 4) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + getUsage());
            return;
        }

        String type = args[2].toLowerCase();
        String cosm = args[3].toLowerCase();

        // TODO: NPC + REMOVE UCS FROM PLUGINS.YML
        try {
            if (!UltraCosmeticsData.get().getEnabledWorlds().contains(Bukkit.getPlayer(args[1]).getWorld().getName())) {
                sender.sendMessage(MessageManager.getMessage("World-Disabled"));
                return;
            }
        } catch (Exception e) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
            return;
        }

        Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
        if (categories.length == 1) {
            Category category = (Category) categories[0];
            try {
                UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[1]));
                if (category == Category.SUITS) {
                    try {
                        ArmorSlot armorSlot = ArmorSlot.getByName(args[3].split(":")[1]);
                        other.removeSuit(armorSlot);
                    } catch (Exception ex) {
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle <player|npcID:npcname> suit <suit type:suit piece>.");
                        return;
                    }
                } else {
                    other.removeCosmetic(category);
                }
            } catch (Exception exc) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
                return;
            }
            Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().contains(cosm.split(":")[0])).toArray();
            if (cosmeticTypes.length == 1) {
                CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];
                try {
                    UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[1]));
                    if (cosmeticType.getCategory() == Category.SUITS) {
                        try {
                            ArmorSlot armorSlot = ArmorSlot.getByName(cosm.split(":")[1]);
                            SuitType suitType = SuitType.valueOf(cosm.split(":")[0]);
                            suitType.equip(other, getUltraCosmetics(), armorSlot);
                        } catch (Exception ex) {
                            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle <player|npcID:npcname> suit <suit type:suit piece>.");
                        }
                    } else {
                        cosmeticType.equip(other, getUltraCosmetics());
                    }
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

    @Override
    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args) {
        //uc toggle <player|npcID:npcname> <type> <cosmetic>
        List<String> tabSuggestion = new ArrayList<>();

        // Check if the root argument doesn't match our command's alias, or if no additional arguments are given (shouldn't happen)
        if(!Arrays.stream(getAliases()).anyMatch(args[0]::equals) || args.length < 2)
            return tabSuggestion;

        else if(args.length == 2) { // Tab-completing first argument: <player|npcID:npcname>
            return UCTabCompleter.GetNPCsAndOnlinePlayers(sender);
        }

        else if(args.length == 3) { // Tab-completing second argument: <type>
            for (Category category : Category.enabled()) {
                tabSuggestion.add(category.toString().toLowerCase());
                tabSuggestion.add("all");
            }
            Collections.sort(tabSuggestion);
            return tabSuggestion;
        }

        else if(args.length == 4) { // Tab-completing third argument: <cosmetic>
            tabSuggestion = getCosmeticNames(args[2].toUpperCase());
            return tabSuggestion;
        }

        else {
            return tabSuggestion;
        }
    }

    private List<String> getCosmeticNames(String type) {
        List<String> emptyTabSuggestion = new ArrayList<>();
        try {
            Category cat = Category.valueOf(type);
            if (cat != null && cat.isEnabled()) {
                List<String> commands = new ArrayList<>();
                for (CosmeticType cosm : cat.getEnabled()) {
                    commands.add(cosm.toString().toLowerCase());
                }
                Collections.sort(commands);
                return commands;
            }
        } catch (Exception exc) {
            return emptyTabSuggestion;
        }
        return emptyTabSuggestion;
    }
}