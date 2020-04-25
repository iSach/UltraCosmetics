package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SubCommandShowcaseClear extends SubCommand {

    private UltraCosmetics plugin;

    public SubCommandShowcaseClear(UltraCosmetics ultraCosmetics) { // TODO: Permissions
        super("Clears a cosmetic on an NPC.", "ultracosmetics.*", "/ucs clear <npc id> [type]", ultraCosmetics, "clear");
        plugin = ultraCosmetics;
    }

    public boolean onCommand(CommandSender commandSender, String... args) {

        Player sender = (Player) commandSender;
        NPC npcTarget;
        Player npc;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. " + getUsage());
            return true;
        }

        if (!sender.hasPermission("ultracosmetics.command.clear" + ".others")) return true;

        // If no NPC specified, use the currently "selected" npc, else parse from arguments
        if (args.length < 2) {
            npcTarget = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "No NPC is selected.");
                return true;
            }
        }
        else {
            npcTarget = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[1]));
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "Invalid NPC ID.");
                return true;
            }
        }

        // Check if NPC is a player-type NPC
        if(npcTarget.getEntity() instanceof Player) npc = (Player) npcTarget.getEntity();
        else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "NPC is invalid. NPCs must be of player type.");
            return true;
        }

        if (npc == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "NPC ID" + args[1] + " was not found!");
            return true;
        }

        UltraPlayer up = plugin.getPlayerManager().getUltraPlayer(npc);

        // If no cosmetic type specified, clear all and remove the NPC from the affected NPC list
        if(args.length < 3) {
            plugin.getNPCManager().RemoveNPC(up.getUuid());
            up.clear();
            return true;
        }

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
        else if (s.startsWith("a")) up.clear();
        else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "/ucs clear <npc id> <type>\n" + ChatColor.RED + "" + ChatColor.BOLD + "Invalid Type.\n" + ChatColor.RED + "" + ChatColor.BOLD + "Available types: gadgets, effects, pets, mounts, suits, hats, morphs, all");
        }
        return true;
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        onCommand(sender, args);
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        onCommand(sender, args);
    }
}
