package be.isach.ultracosmetics.command.showcase.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.showcase.UCShowcaseTabCompleter;
import be.isach.ultracosmetics.command.ultracosmetics.UCTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SubCommandShowcaseClear extends SubCommand {

    private UltraCosmetics plugin;

    public SubCommandShowcaseClear(UltraCosmetics ultraCosmetics) {
        super("Clears a cosmetic on an NPC.", "ultracosmetics.*", "/ucs clear [type] [npc id]", ultraCosmetics, "clear");
        plugin = ultraCosmetics;
    }

    public void common(CommandSender commandSender, String... args) {

        Player sender = (Player) commandSender;
        NPC npcTarget;
        Player npc;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. " + getUsage());
            return;
        }

        if (!sender.hasPermission("ultracosmetics.command.clear" + ".others")) return;

        // If no NPC specified, use the currently "selected" npc, else parse from arguments
        if (args.length < 3) {
            npcTarget = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "No NPC is selected.");
                return;
            }
        }
        else {
            npcTarget = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "Invalid NPC ID.");
                return;
            }
        }

        // Check if NPC is a player-type NPC
        if(npcTarget.getEntity() instanceof Player) npc = (Player) npcTarget.getEntity();
        else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "NPC is invalid. NPCs must be of player type.");
            return;
        }

        if (npc == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "NPC ID" + args[2] + " was not found!");
            return;
        }

        UltraPlayer up = plugin.getPlayerManager().getUltraPlayer(npc);

        // If no cosmetic type specified, clear all and remove the NPC from the affected NPC list
        if(args.length < 3) {
            plugin.getNPCManager().RemoveNPC(up.getUuid());
            up.clear();
            return;
        }

        String s = args[1].toLowerCase();

        if (s.startsWith("g")) up.removeGadget();
        else if (s.startsWith("pa") || s.startsWith("ef")) up.removeParticleEffect();
        else if (s.startsWith("pe")) up.removePet();
        else if (s.startsWith("h")) up.removeHat();
        else if (s.startsWith("s") && !s.contains(":")) up.removeSuit();
        else if (s.startsWith("s") && s.contains(":")) up.removeSuit(ArmorSlot.getByName(s.split(":")[1]));
        else if (s.startsWith("mor")) up.removeMorph();
        else if (s.startsWith("mou")) up.removeMount();
        else if (s.startsWith("e")) up.removeEmote();
        else if (s.startsWith("a")) up.clear(); // Add explict "all" option
        else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "/ucs clear [npc id] [type]\n" + ChatColor.RED + "" + ChatColor.BOLD + "Invalid Type.\n" + ChatColor.RED + "" + ChatColor.BOLD + "Available types: gadgets, effects, pets, mounts, suits, hats, morphs, all");
            return;
        }
        sender.sendMessage(MessageManager.getMessage("Prefix") + " §3§lSuccess.");
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
        //ucs clear [type] [npc id]
        List<String> tabSuggestion = new ArrayList<>();

        // Check if the root argument doesn't match our command's alias, or if no additional arguments are given (shouldn't happen)
        if(!Arrays.stream(getAliases()).anyMatch(args[0]::equals) || args.length < 2)
            return tabSuggestion;

        else if(args.length == 2) { // Tab-completing second argument: [type]
            for (Category category : Category.enabled()) {
                tabSuggestion.add(category.toString().toLowerCase());
                tabSuggestion.add("all");
            }
            Collections.sort(tabSuggestion);
            return tabSuggestion;
        }

        else if(args.length == 3) { // Tab-completing first argument: [npc id]
            return UCShowcaseTabCompleter.GetNPCs();
        }

        else {
            return tabSuggestion;
        }
    }
}
