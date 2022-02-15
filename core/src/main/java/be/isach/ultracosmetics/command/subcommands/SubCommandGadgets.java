package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Gadgets {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SubCommandGadgets extends SubCommand {

    public SubCommandGadgets(UltraCosmetics ultraCosmetics) {
        super("gadgets", "Toggle Gadgets", "ultracosmetics.command.gadgets", "/uc gadgets", ultraCosmetics);
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        if (!SettingsManager.isAllowedWorld(sender.getWorld())) {
            sender.sendMessage(MessageManager.getMessage("World-Disabled"));
            return;
        }

        UltraPlayer customPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(sender);
        customPlayer.setGadgetsEnabled(!customPlayer.hasGadgetsEnabled());
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}
