package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SubCommandShowcaseClear implements CommandExecutor {

    private UltraCosmetics plugin;

    public SubCommandShowcaseClear(UltraCosmetics uc) {
        plugin = uc;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String str, String[] args) {

        Player sender = (Player) commandSender;
        Player receiver;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. " + "/uc clear <player> [type]");
            return true;
        }

        if (!sender.hasPermission("ultracosmetics.command.clear" + ".others")) return true;
        receiver = Bukkit.getPlayer(args[1]);

        if (receiver == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " not found!");
            return true;
        }
        if (args.length < 3) {
            plugin.getPlayerManager().getUltraPlayer(receiver).clear();
            return true;
        }

        UltraPlayer up = plugin.getPlayerManager().getUltraPlayer(receiver);
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
        else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "/uc clear <player> <type>\n" + ChatColor.RED + "" + ChatColor.BOLD + "Invalid Type.\n" + ChatColor.RED + "" + ChatColor.BOLD + "Available types: gadgets, particleeffects, pets, mounts, suits, hats, morphs");
        }
        return false;
    }
}
