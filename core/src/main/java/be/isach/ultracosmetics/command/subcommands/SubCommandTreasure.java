package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.manager.TreasureChestManager;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

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
		
		Location location = new Location(world, x + 0.5, y, z + 0.5);
		
		if (location.getBlock().getType() != Material.AIR) {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This isn't a valid location for teleporting.");
			return;
		}
		
		opener.teleport(location);
		
		TreasureChestManager.tryOpenChest(opener);
	}
}
