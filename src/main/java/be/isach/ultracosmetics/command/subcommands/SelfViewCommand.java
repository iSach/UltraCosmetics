package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 20/12/15.
 */
public class SelfViewCommand extends SubCommand {

    public SelfViewCommand() {
        super("Toggle Morph Self View", "ultracosmetics.command.selfview", "/uc selfview", "selfview");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        CustomPlayer customPlayer = Core.getPlayerManager().getCustomPlayer(sender);
        customPlayer.setSeeSelfMorph(!customPlayer.canSeeSelfMorph());
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}

