package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SubCommandReload extends SubCommand {

    public SubCommandReload(UltraCosmetics ultraCosmetics) {
        super("reload", "Reloads messages.yml ONLY", "", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "Please note that this currently ONLY reloads the messages.yml file.");
        MessageManager.reload();
        ultraCosmetics.reload();
        sender.sendMessage(ChatColor.GREEN + "Messages reloaded");
    }

}
