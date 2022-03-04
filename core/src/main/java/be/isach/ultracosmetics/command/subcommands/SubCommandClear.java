package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Clear {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-22-2015
 */
public class SubCommandClear extends SubCommand {

    public SubCommandClear(UltraCosmetics ultraCosmetics) {
        super("clear", "Clears a Cosmetic.", "ultracosmetics.command.clear", "/uc clear <player> [type]", ultraCosmetics);
    }

    @Override
    protected void onExePlayer(Player sender, String[] args) {
        common(sender, args);
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String[] args) {
        common(sender, args);
    }

    private void common(CommandSender sender, String[] args) {
        Player target;
        if (args.length < 2) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                error(sender, "You must specify a player.");
                return;
            }
        } else {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                error(sender, "Player " + args[1] + " not found!");
                return;
            }
        }

        if (target != sender && !sender.hasPermission(getPermission() + ".others")) {
            error(sender, "You do not have permission to clear others.");
            return;
        }

        if (args.length < 3) {
            ultraCosmetics.getPlayerManager().getUltraPlayer(target).clear();
            return;
        }

        UltraPlayer up = ultraCosmetics.getPlayerManager().getUltraPlayer(target);

        Category cat = Category.fromString(args[2]);
        switch(cat) {
        case EFFECTS:
            up.removeParticleEffect();
            break;
        case EMOTES:
            up.removeEmote();
            break;
        case GADGETS:
            up.removeGadget();
            break;
        case HATS:
            up.removeHat();
            break;
        case MORPHS:
            up.removeMorph();
            break;
        case MOUNTS:
            up.removeMount();
            break;
        case PETS:
            up.removePet();
            break;
        case SUITS:
            String[] parts = args[2].split(":");
            if (parts.length < 2) {
                up.removeSuit();
                break;
            }
            up.removeSuit(ArmorSlot.getByName(parts[1]));
            break;
        default: // null
            error(sender, "Invalid cosmetic type.");
            break;
        }
    }
}
