package be.isach.ultracosmetics.command.showcase.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.showcase.UCShowcaseTabCompleter;
import be.isach.ultracosmetics.command.ultracosmetics.UCTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubCommandShowcaseRenamePet extends SubCommand {

    private UltraCosmetics plugin;

    public SubCommandShowcaseRenamePet(UltraCosmetics ultraCosmetics) {
        super("Rename an NPC's pet.", "ultracosmetics.*", "/ucs renamepet [pet type] [new name] [npc id]", ultraCosmetics, "renamepet");
        plugin = ultraCosmetics;
    }

    @Override
    public void onExePlayer(Player sender, String... args) {
        common(sender, args);
    }

    @Override
    public void onExeConsole(ConsoleCommandSender sender, String... args) {
        common(sender, args);
    }

    private void common(CommandSender sender, String... args) {
        Player player = (Player) sender;
        NPC npcTarget = null;
        Player npc;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. " + getUsage());
            return;
        }

        if (!sender.hasPermission(getPermission() + ".others")) return;

        // If no NPC specified, use the currently "selected" npc, else parse from arguments
        if (args.length < 4) {
            npcTarget = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "No NPC is selected.");
                return;
            }
        }
        else {
            try {
                npcTarget = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3]));
            } catch(Exception ignored) {} // Not a valid integer
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
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "NPC ID not found!");
            return;
        }

        UltraPlayer up = getUltraCosmetics().getPlayerManager().getUltraPlayer(npc);

        switch (args.length) {
            case 1:
                clearAllPetNames(sender, up);
                return;
            case 2:
                clearPetName(sender, up, PetType.valueOf(args[1].toLowerCase()));
                return;
            case 3:
            case 4:
                setPetName(sender, up, PetType.valueOf(args[1].toLowerCase()), args[2].toLowerCase());
                return;
            default:
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Too many arguments. " + getUsage());
        }
    }

    // Set the names all pet types back to default
    private void clearAllPetNames(CommandSender sender, UltraPlayer up) {
        for(PetType petType : PetType.values()) {
            up.renamePetName(petType, "");
        }
        sender.sendMessage(MessageManager.getMessage("Prefix") + ChatColor.DARK_AQUA + "" +
                ChatColor.BOLD + "Cleared " + ChatColor.YELLOW + "all " + ChatColor.DARK_AQUA + "of " + up.getUsername() + "'s pet names");
    }

    // Set the name of the specified pet type back to default
    private void clearPetName(CommandSender sender, UltraPlayer up, PetType petType) {
        up.renamePetName(petType, "");
        sender.sendMessage(MessageManager.getMessage("Prefix") + ChatColor.DARK_AQUA + "" + ChatColor.BOLD +
                "Cleared " + up.getUsername() + "'s pet name for " +
                ChatColor.translateAlternateColorCodes('&', MessageManager.getMessage("Pets." + petType.getConfigName() + ".menu-name")));
    }

    // Set the name of the specified pet type
    private void setPetName(CommandSender sender, UltraPlayer up, PetType petType, String newName) {
        up.renamePetName(petType, newName);
        sender.sendMessage(MessageManager.getMessage("Prefix") + ChatColor.DARK_AQUA + "" + ChatColor.BOLD +
                "Renamed " + up.getUsername() + "'s pet name for " +
                ChatColor.translateAlternateColorCodes('&', MessageManager.getMessage("Pets." + petType.getConfigName() + ".menu-name")));
    }

    @Override
    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args) {
        //ucs renamepet [pet type] [new name] [npc id]
        List<String> tabSuggestion = new ArrayList<>();

        // Check if the root argument doesn't match our command's alias, or if no additional arguments are given (shouldn't happen)
        if(!Arrays.stream(getAliases()).anyMatch(args[0]::equals) || args.length < 2)
            return tabSuggestion;

        else if(args.length == 2) { // Tab-completing first argument: [pet type]
            for(PetType petType : PetType.values()) {
                tabSuggestion.add(petType.getConfigName());
            }
            return tabSuggestion;
        }

        else if(args.length == 4) { // Tab-completing third argument: [npc id]
            return UCShowcaseTabCompleter.GetNPCs();
        }

        else { // No need to tab-complete [new name]
            return tabSuggestion;
        }
    }
}
