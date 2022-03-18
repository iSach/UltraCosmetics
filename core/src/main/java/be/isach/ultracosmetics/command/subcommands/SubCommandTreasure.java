package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.manager.TreasureChestManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.treasurechests.TreasureLocation;
import be.isach.ultracosmetics.version.VersionManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Treasure {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-22-2015
 */
public class SubCommandTreasure extends SubCommand {

    public SubCommandTreasure(UltraCosmetics ultraCosmetics) {
        super("treasure", "Starts Treasure Chest.", "ultracosmetics.command.treasure", "/uc treasure <player> <x> <y> <z> <world>", ultraCosmetics);
    }

    @Override
    protected void onExePlayer(Player sender, String[] args) {
        common(sender, args);
    }

    @Override
    protected void onExeNotPlayer(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player.");
            return;
        }
        common(sender, args);
    }

    private void common(CommandSender sender, String... args) {
        if (args.length > 6) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage! " + getUsage());
            return;
        }

        Player opener;
        // form: /uc treasure
        if (args.length == 1) {
            opener = (Player)sender;
        // form: /uc treasure (player) [...]
        } else {
            opener = Bukkit.getPlayer(args[1]);
            if (opener == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " not found!");
                return;
            }
        }

        UltraPlayer ultraPlayer = UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(opener);

        if (ultraPlayer.getKeys() <= 0) {
            opener.closeInventory();
            ultraPlayer.openKeyPurchaseMenu();
            return;
        }

        // form: /uc treasure (player)
        if (args.length < 3) {
            if (!checkWorld(sender, opener.getWorld())) return;
            TreasureChestManager.tryOpenChest(opener);
            return;
        }

        int x, y, z;

        try {
            x = Integer.parseInt(args[2]);
            y = Integer.parseInt(args[3]);
            z = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid coordinates!");
            return;
        }

        World world;
        // form: /uc treasure (player) (x) (y) (z) (world)
        if (args.length > 5) {
            world = Bukkit.getWorld(args[5]);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "World " + args[5] + " doesn't exist!");
                return;
            }
        // form: /uc treasure (player) (x) (y) (z)
        } else {
            world = opener.getWorld();
        }

        VersionManager vm = UltraCosmeticsData.get().getVersionManager();
        // Don't accept equal to world boundaries because treasure chests have to place blocks on player Y-1 through Y+1
        if (y >= vm.getWorldMaxHeight(world) || y <= vm.getWorldMinHeight(world)) {
            sender.sendMessage(MessageManager.getMessage("Chest-Location.Invalid"));
            return;
        }

        Location location = new Location(world, x + 0.5, y, z + 0.5);
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) {
            sender.sendMessage(MessageManager.getMessage("Chest-Location.In-Ground"));
            for (int i = y; i < vm.getWorldMaxHeight(world); i++) {
                if (block.getWorld().getBlockAt(x, i, z).getType() == Material.AIR) {
                    suggest(x, i, z, sender);
                    break;
                }
            }
            return;
        }

        if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            sender.sendMessage(MessageManager.getMessage("Chest-Location.In-Air"));
            for (int i = y; i > vm.getWorldMinHeight(world); i--) {
                if (block.getWorld().getBlockAt(x, i, z).getType() != Material.AIR) {
                    // we found the ground, back up 1
                    suggest(x, i + 1, z, sender);
                    break;
                }
            }
            return;
        }

        TreasureChestManager.tryOpenChest(opener, TreasureLocation.fromLocation(location));
    }

    private boolean checkWorld(CommandSender sender, World world) {
        if (SettingsManager.isAllowedWorld(world)) return true;
        sender.sendMessage(MessageManager.getMessage("World-Disabled"));
        return false;
    }

    private void suggest(int x, int y, int z, CommandSender sender) {
        sender.sendMessage(MessageManager.getMessage("Chest-Location.Suggestion").replace("%location%", x + "," + y + "," + z));
    }
}
