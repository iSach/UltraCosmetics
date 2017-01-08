<<<<<<< HEAD
package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 22/12/15.
 */
public class SubCommandClear extends SubCommand {

    public SubCommandClear(UltraCosmetics ultraCosmetics) {
        super("Clears a Cosmetic.", "ultracosmetics.command.clear", "/uc clear <player> [type]", ultraCosmetics, "clear");
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
            sender.sendMessage("§c§l  Incorrect Usage. " + getUsage());
            return;
        }

        if (!sender.hasPermission(getPermission() + ".others")) return;
        receiver = Bukkit.getPlayer(args[1]);

        if (receiver == null) {
            sender.sendMessage("  §c§lPlayer " + args[1] + " not found!");
            return;
        }
        if (args.length < 3) {
            getUltraCosmetics().getPlayerManager().getUltraPlayer(receiver).clear();
            return;
        }

        UltraPlayer up = getUltraCosmetics().getPlayerManager().getUltraPlayer(receiver);
        String s = args[2].toLowerCase();

        if (s.startsWith("g")) up.removeGadget();
        else if (s.startsWith("pa")) up.removeParticleEffect();
        else if (s.startsWith("pe")) up.removePet();
        else if (s.startsWith("h")) up.removeHat();
        else if (s.startsWith("s")) up.removeSuit();
        else if (s.startsWith("mor")) up.removeMorph();
        else if (s.startsWith("mou")) up.removeMount();
        else if (s.startsWith("e")) up.removeEmote();
        else {
            sender.sendMessage("§c§l/uc menu <menu>\n§c§lInvalid Type.\n§c§lAvailable types: gadgets, particleeffects, pets, mounts, suits, hats, morphs");
        }
    }
}
=======
package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 22/12/15.
 */
public class SubCommandClear extends SubCommand {

    public SubCommandClear(UltraCosmetics ultraCosmetics) {
        super("Clears a Cosmetic.", "ultracosmetics.command.clear", "/uc clear <player> [type]", ultraCosmetics, "clear");
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
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. " + getUsage());
            return;
        }

        if (!sender.hasPermission(getPermission() + ".others")) return;
        receiver = Bukkit.getPlayer(args[1]);

        if (receiver == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[1] + " not found!");
            return;
        }
        if (args.length < 3) {
            getUltraCosmetics().getPlayerManager().getUltraPlayer(receiver).clear();
            return;
        }

        UltraPlayer up = getUltraCosmetics().getPlayerManager().getUltraPlayer(receiver);
        String s = args[2].toLowerCase();

        if (s.startsWith("g")) up.removeGadget();
        else if (s.startsWith("pa")) up.removeParticleEffect();
        else if (s.startsWith("pe")) up.removePet();
        else if (s.startsWith("h")) up.removeHat();
        else if (s.startsWith("s")) up.removeSuit();
        else if (s.startsWith("mor")) up.removeMorph();
        else if (s.startsWith("mou")) up.removeMount();
        else if (s.startsWith("e")) up.removeEmote();
        else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l/uc menu <menu>\n&c&lInvalid Type.\n&c&lAvailable types: gadgets, particleeffects, pets, mounts, suits, hats, morphs"));
        }
    }
}
>>>>>>> refs/remotes/origin/master
