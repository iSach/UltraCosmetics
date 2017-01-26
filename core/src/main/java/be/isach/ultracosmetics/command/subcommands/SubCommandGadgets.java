package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Gadgets {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 * 
 * @author 	iSach
 * @since 	12-20-2015
 */
public class SubCommandGadgets extends SubCommand {

    public SubCommandGadgets(UltraCosmetics ultraCosmetics) {
        super("Toggle Gadgets", "ultracosmetics.command.gadgets", "/uc gadgets", ultraCosmetics, "gadgets");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        UltraPlayer customPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(sender);
        customPlayer.setGadgetsEnabled(!customPlayer.hasGadgetsEnabled());
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}
