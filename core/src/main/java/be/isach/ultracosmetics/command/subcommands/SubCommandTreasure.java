package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.UCTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.manager.TreasureChestManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Cuboid;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Treasure {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-22-2015
 */
public class SubCommandTreasure extends SubCommand {

    public SubCommandTreasure(UltraCosmetics ultraCosmetics) {
        super("Starts Treasure Chest.", "ultracosmetics.command.treasure", "/uc treasure <player> <x> <y> <z> <world>", ultraCosmetics, "treasure");
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
        if (args.length < 6) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage! " + getUsage());
            return;
        }
        Player opener = Bukkit.getPlayer(args[1]);
        if (opener == null) {
            sender.sendMessage("§c§lPlayer " + args[1] + " not found!");
            return;
        }

        UltraPlayer ultraPlayer = UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(opener);

        if (ultraPlayer.getKeys() <= 0) {
            opener.closeInventory();
            ultraPlayer.openKeyPurchaseMenu();
            return;
        }

        if (!UltraCosmeticsData.get().getEnabledWorlds().contains(opener.getWorld().getName())) {
            sender.sendMessage(MessageManager.getMessage("World-Disabled"));
            return;
        }

        double x, y, z;
        if (!MathUtils.isDouble(args[2])) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[2] + " isn't a number!");
            return;
        }
        x = Integer.parseInt(args[2]);
        if (!MathUtils.isDouble(args[3])) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[3] + " isn't a number!");
            return;
        }
        y = Integer.parseInt(args[3]);
        if (!MathUtils.isDouble(args[4])) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[4] + " isn't a number!");
            return;
        }
        z = Integer.parseInt(args[4]);

        World world = Bukkit.getWorld(args[5]);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "World " + args[5] + " doesn't exist!");
            return;
        }

        Location preLoc = opener.getLocation();
        Location location = new Location(world, x + 0.5, y, z + 0.5);

        Cuboid c = new Cuboid(location.add(-2, 0, -2), location.add(2, 1, 2));

        if (!c.isEmptyExcept(location.getBlock().getLocation())) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This isn't a valid location for teleporting.");
            opener.sendMessage(MessageManager.getMessage("Chest-Not-Enough-Space"));
            return;
        }

        opener.teleport(location);

        TreasureChestManager.tryOpenChest(opener, preLoc);
    }

    @Override
    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args) {
        //uc treasure <player> <x> <y> <z> <world>
        List<String> tabSuggestion = new ArrayList<>();

        // Check if the root argument doesn't match our command's alias, or if no additional arguments are given (shouldn't happen)
        if(!Arrays.stream(getAliases()).anyMatch(args[0]::equals) || args.length < 2)
            return tabSuggestion;

        else if(args.length == 2) { // Tab-completing first argument: <player>
            tabSuggestion = UCTabCompleter.GetOnlinePlayers(sender);
            return tabSuggestion;
        }

        // No need to tab complete <x>, <y>, or <z>

        else if(args.length == 6) { // Tab-completing fifth argument: <world>
            for (World world : Bukkit.getWorlds()) {
                tabSuggestion.add(world.getName().toLowerCase());
            }
            Collections.sort(tabSuggestion);
            return tabSuggestion;
        }

        else {
            return tabSuggestion;
        }
    }
}
