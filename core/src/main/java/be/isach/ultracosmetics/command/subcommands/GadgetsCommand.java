package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 20/12/15.
 */
public class GadgetsCommand extends SubCommand {


    public GadgetsCommand() {
        super("Toggle Gadgets", "ultracosmetics.command.gadgets", "/uc gadgets", "gadgets");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        CustomPlayer customPlayer = UltraCosmetics.getPlayerManager().getCustomPlayer(sender);
        customPlayer.setGadgetsEnabled(!customPlayer.hasGadgetsEnabled());
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}
