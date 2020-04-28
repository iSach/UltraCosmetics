package be.isach.ultracosmetics.command;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UCTabCompleter implements TabCompleter {

    private UltraCosmetics uc;

    public UCTabCompleter(UltraCosmetics uc) {
        this.uc = uc;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> tabSuggestion = new ArrayList<>(); // Start with empty tab suggestion

        if(args.length < 1) { // No arguments, should never happen.
            return tabSuggestion; // return empty tab suggestion
        }

        // Root command: uc <subcommand>
        else if(args.length == 1) { // Tab-completing <subcommand>
            for (SubCommand sc : uc.getCommandManager().getCommands()) {
                tabSuggestion.add(sc.commandaliases[0]);
            }
            Collections.sort(tabSuggestion);
            // copyPartialMatches() allows us to filter based on what we've already typed.
            return StringUtil.copyPartialMatches(args[0], tabSuggestion, new ArrayList<>(tabSuggestion.size()));
        }

        else { // Sub command, delegate to SubCommand's getTabCompleteSuggestion()
            try {
                tabSuggestion = uc.getCommandManager().getCommand(args[0]).getTabCompleteSuggestion(sender, args);
                return StringUtil.copyPartialMatches(args[args.length - 1], tabSuggestion, new ArrayList<>(tabSuggestion.size()));
            } catch (ClassNotFoundException e) { // Invalid subcommand
                return tabSuggestion;
            }
        }
    }

    public static List<String> GetOnlinePlayers(CommandSender sender) {
        // The following is derived from Bukkit: Command#tabComplete0

        // Get online players the sender can see (accounts for vanished players)
        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        ArrayList<String> matchedPlayers = new ArrayList<>();
        for (Player player : sender.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if ((senderPlayer == null || senderPlayer.canSee(player))) {
                matchedPlayers.add(name);
            }
        }
        Collections.sort(matchedPlayers, String.CASE_INSENSITIVE_ORDER);
        return matchedPlayers;
    }

    public static List<String> GetNPCsAndOnlinePlayers(CommandSender sender) {
        List<String> tabSuggestion = new ArrayList<>();

        tabSuggestion = GetNPCs();

        tabSuggestion.addAll(GetOnlinePlayers(sender)); // Concatenate players AFTER NPCs

        return tabSuggestion;
    }

    public static List<String> GetNPCs() {
        List<String> tabSuggestion = new ArrayList<>();

        // Display NPCs first
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if(npc.getFullName().contains(" ")) // NPC name has a space in it
                tabSuggestion.add("\"" + npc.getId() + ":" + npc.getFullName() + "\"");
            else
                tabSuggestion.add(npc.getId() + ":" + npc.getFullName());
        }
        // Sort by NPC ID, compare as a numeric value
        Collections.sort(tabSuggestion, (s1, s2) -> {
            // Remove potential " or ' at beginning of string
            s1 = s1.replaceFirst("^(\"|')", "");
            s2 = s2.replaceFirst("^(\"|')", "");

            // Extract only the NPC ID, not the name
            String lhs = s1.split(":", 2)[0];
            String rhs = s2.split(":", 2)[0];

            // Compare as numeric value
            long i1 = Long.parseLong(lhs);
            long i2 = Long.parseLong(rhs);
            return (int) (i1-i2);
        });

        return tabSuggestion;
    }
}