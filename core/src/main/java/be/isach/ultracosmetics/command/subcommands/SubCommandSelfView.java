package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Selfview {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 * 
 * @author 	iSach
 * @since 	12-20-2015
 */
public class SubCommandSelfView extends SubCommand {

    public SubCommandSelfView(UltraCosmetics ultraCosmetics) {
        super("Toggle Morph Self View", "ultracosmetics.command.selfview", "/uc selfview", ultraCosmetics, "selfview");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        UltraPlayer customPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(sender);
        customPlayer.setSeeSelfMorph(!customPlayer.canSeeSelfMorph());
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}
