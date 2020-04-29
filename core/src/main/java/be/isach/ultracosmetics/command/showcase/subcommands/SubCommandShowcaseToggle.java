package be.isach.ultracosmetics.command.showcase.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.showcase.UCShowcaseTabCompleter;
import be.isach.ultracosmetics.command.ultracosmetics.UCTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SubCommandShowcaseToggle extends SubCommand {

    private UltraCosmetics plugin;

    public SubCommandShowcaseToggle(UltraCosmetics ultraCosmetics) {
        super("Equips a cosmetic.", "ultracosmetics.*", "/ucs toggle <type> <cosmetic> [npc id]", ultraCosmetics, "toggle");
        plugin = ultraCosmetics;
    }

    // TODO: FIX TOGGLE
    public void common(CommandSender commandSender, String... args) {
        Player sender = (Player) commandSender;

        if (args.length < 3) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + getUsage());
            return;
        }

        NPC npcTarget;
        Player npc;

        // If no NPC specified, use the currently "selected" npc, else parse from arguments
        if (args.length < 4) {
            npcTarget = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "No NPC is selected.");
                return;
            }
        }
        else {
            npcTarget = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3]));
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "Invalid NPC ID.");
                return;
            }
        }

        String type = args[1].toLowerCase();
        String cosm = args[2].toLowerCase();

        // Check if NPC is a player-type NPC
        if(npcTarget.getEntity() instanceof Player) npc = (Player) npcTarget.getEntity();
        else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "NPC is invalid. NPCs must be of player type.");
            return;
        }

        try {
            if (!UltraCosmeticsData.get().getEnabledWorlds().contains(npc.getWorld().getName())) {
                sender.sendMessage(MessageManager.getMessage("World-Disabled"));
                return;
            }
        } catch (Exception e) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid NPC ID.");
            return;
        }

        Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
        if (categories.length == 1) {
            Category category = (Category) categories[0];
            try {
                UltraPlayer other = plugin.getPlayerManager().getUltraPlayer(npc);
                if (category == Category.SUITS) {
                    try {
                        ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
                        other.removeSuit(armorSlot);
                    } catch (Exception ex) {
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/ucs toggle [npc id] suit <suit type:suit piece>.");
                        return;
                    }
                } else {
                    other.removeCosmetic(category);
                }
            } catch (Exception exc) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid NPC ID.");
                return;
            }

            Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().contains(cosm.split(":")[0])).toArray();
            if (cosmeticTypes.length == 1) {
                CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];

                try {
                    UltraPlayer other = plugin.getPlayerManager().getUltraPlayer(npc);
                    if (cosmeticType.getCategory() == Category.SUITS) {
                        try {
                            ArmorSlot armorSlot = ArmorSlot.getByName(cosm.split(":")[1]);
                            SuitType suitType = SuitType.valueOf(cosm.split(":")[0]);
                            suitType.equip(other, plugin, armorSlot);
                        } catch (Exception ex) {
                            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/ucs toggle [npc id] suit <suit type:suit piece>.");
                        }
                    } else {
                        plugin.getNPCManager().AddNPC(npc.getUniqueId());
                        cosmeticType.equip(other, plugin);
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §3§lSuccess.");
                    }
                } catch (Exception exc) {
                    sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid NPC ID.");
                }

            } else {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid cosmetic.");
            }
        } else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid category.");
        }
        return;
    }

    @Override
    public void onExePlayer(Player sender, String... args) {
        common(sender, args);
    }

    @Override
    public void onExeConsole(ConsoleCommandSender sender, String... args) {
        common(sender, args);
    }

    @Override
    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args) {
        //ucs toggle <type> <cosmetic> [npc id]
        List<String> tabSuggestion = new ArrayList<>();

        // Check if the root argument doesn't match our command's alias, or if no additional arguments are given (shouldn't happen)
        if(!Arrays.stream(getAliases()).anyMatch(args[0]::equals) || args.length < 2)
            return tabSuggestion;

        else if(args.length == 2) { // Tab-completing first argument: <type>
            for (Category category : Category.enabled()) {
                tabSuggestion.add(category.toString().toLowerCase());
            }
            Collections.sort(tabSuggestion);
            return tabSuggestion;
        }

        else if(args.length == 3) { // Tab-completing second argument: <cosmetic>
            tabSuggestion = getCosmeticNames(args[1].toUpperCase());
            return tabSuggestion;
        }

        else if(args.length == 4) { // Tab-completing third argument: [npc id]
            return UCShowcaseTabCompleter.GetNPCs();
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
