package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.UCTabCompleter;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
 * @since 12-22-2015
 */
public class SubCommandClear extends SubCommand {

    public SubCommandClear(UltraCosmetics ultraCosmetics) {
        super("Clears a Cosmetic.", "ultracosmetics.command.clear", "/uc clear <player|npcID:npcname> [type]", ultraCosmetics, "clear");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        common(sender, args);
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        common(sender, args);
    }

    private void common(CommandSender sender, String... args) {
        Player receiver;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. " + getUsage());
            return;
        }

        if (!sender.hasPermission(getPermission() + ".others")) return;
        receiver = Bukkit.getPlayer(args[1]); // TODO: Add npc functionality back with NEW FORMAT

        if (receiver == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " not found!");
            return;
        }
        if (args.length < 3) {
            getUltraCosmetics().getPlayerManager().getUltraPlayer(receiver).clear();
            return;
        }

        UltraPlayer up = getUltraCosmetics().getPlayerManager().getUltraPlayer(receiver);
        String s = args[2].toLowerCase();

        if (s.startsWith("g")) up.removeGadget();
        else if (s.startsWith("pa") || s.startsWith("ef")) up.removeParticleEffect();
        else if (s.startsWith("pe")) up.removePet();
        else if (s.startsWith("h")) up.removeHat();
        else if (s.startsWith("s") && !s.contains(":")) up.removeSuit();
        else if (s.startsWith("s") && s.contains(":")) up.removeSuit(ArmorSlot.getByName(s.split(":")[1]));
        else if (s.startsWith("mor")) up.removeMorph();
        else if (s.startsWith("mou")) up.removeMount();
        else if (s.startsWith("e")) up.removeEmote();
        else if (s.startsWith("a")) up.clear(); // add an explicit "all" option as well
        else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "/uc clear <player> <type>\n" + ChatColor.RED + "" + ChatColor.BOLD + "Invalid Type.\n" + ChatColor.RED + "" + ChatColor.BOLD + "Available types: gadgets, particleeffects, pets, mounts, suits, hats, morphs");
        }
    }


    @Override
    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args) {
        //uc clear <player|npcID:npcname> [type]
        List<String> tabSuggestion = new ArrayList<>();

        // Check if the root argument doesn't match our command's alias, or if no additional arguments are given (shouldn't happen)
        if(!Arrays.stream(getAliases()).anyMatch(args[0]::equals) || args.length < 2)
            return tabSuggestion;

        else if(args.length == 2) { // Tab-completing first argument: <player|npcID:npcname>
            return UCTabCompleter.GetNPCsAndOnlinePlayers(sender);
        }

        else if(args.length == 3) { // Tab-completing second argument: [type]
            for (Category category : Category.enabled()) {
                tabSuggestion.add(category.toString().toLowerCase());
                tabSuggestion.add("all");
            }
            Collections.sort(tabSuggestion);
            return tabSuggestion;
        }

        else {
            return tabSuggestion;
        }
    }
}
