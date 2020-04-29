package be.isach.ultracosmetics.command.showcase;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UCShowcaseTabCompleter implements TabCompleter {

    private UltraCosmetics uc;

    public UCShowcaseTabCompleter(UltraCosmetics uc) {
        this.uc = uc;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> tabSuggestion = new ArrayList<>(); // Start with empty tab suggestion

        if(args.length < 1) { // No arguments, should never happen.
            return tabSuggestion; // return empty tab suggestion
        }

        // Root command: ucs <subcommand>
        else if(args.length == 1) { // Tab-completing <subcommand>
            for (SubCommand sc : uc.getShowcaseCommandManager().getCommands()) {
                tabSuggestion.add(sc.getAliases()[0]);
            }
            Collections.sort(tabSuggestion);
            // copyPartialMatches() allows us to filter based on what we've already typed.
            return StringUtil.copyPartialMatches(args[0], tabSuggestion, new ArrayList<>(tabSuggestion.size()));
        }

        else { // Sub command, delegate to SubCommand's getTabCompleteSuggestion()
            try {
                tabSuggestion = uc.getShowcaseCommandManager().getCommand(args[0]).getTabCompleteSuggestion(sender, args);
                return StringUtil.copyPartialMatches(args[args.length - 1], tabSuggestion, new ArrayList<>(tabSuggestion.size()));
            } catch (ClassNotFoundException e) { // Invalid subcommand
                return tabSuggestion;
            }
        }
    }

    public static List<String> GetNPCs() {
        List<String> tabSuggestion = new ArrayList<>();

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            tabSuggestion.add(String.valueOf(npc.getId()));
        }
        Collections.sort(tabSuggestion);

        return tabSuggestion;
    }
}