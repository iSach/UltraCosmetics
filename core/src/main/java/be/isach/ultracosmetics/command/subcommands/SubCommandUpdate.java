package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.util.UpdateManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class SubCommandUpdate extends SubCommand {

    public SubCommandUpdate(UltraCosmetics ultraCosmetics) {
        super("update", "Updates UltraCosmetics", "", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        UpdateManager updateManager = ultraCosmetics.getUpdateChecker();
        if (updateManager == null) {
            sender.sendMessage(ChatColor.RED + "Version checking must be enabled to update.");
            return;
        }
        if (args.length > 1 && args[1].equalsIgnoreCase("force")) {
            sender.sendMessage(ChatColor.RED + "Ignoring version check and downloading anyway. This is not recommended and may result in a downgrade.");
        } else if (!updateManager.isOutdated()) {
            sender.sendMessage(ChatColor.RED + "There is no new version available for this server version.");
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "Requesting update...");
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean success = updateManager.update();
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "Update succeeded, please restart the server to complete the update.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Update failed or aborted, please check the console for more information.");
                }
            }
        }.runTaskAsynchronously(ultraCosmetics);
    }

}
