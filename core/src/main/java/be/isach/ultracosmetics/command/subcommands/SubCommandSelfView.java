package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Selfview {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SubCommandSelfView extends SubCommand {

    public SubCommandSelfView(UltraCosmetics ultraCosmetics) {
        super("selfview", "Toggle Morph Self View", "ultracosmetics.command.selfview", "/uc selfview", ultraCosmetics);
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        if (!SettingsManager.isAllowedWorld(sender.getWorld())) {
            sender.sendMessage(MessageManager.getMessage("World-Disabled"));
            return;
        }

        UltraPlayer customPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(sender);
        customPlayer.setSeeSelfMorph(!customPlayer.canSeeSelfMorph());
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}
