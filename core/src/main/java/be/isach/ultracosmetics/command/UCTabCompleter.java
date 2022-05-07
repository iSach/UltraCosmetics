package be.isach.ultracosmetics.command;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UCTabCompleter implements TabCompleter {

    private UltraCosmetics uc;

    public UCTabCompleter(UltraCosmetics uc) {
        this.uc = uc;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!cmd.getName().equals("ultracosmetics")) return null;
        List<String> options = new ArrayList<>();
        // TODO: move each subcommand section to its subcommand class
        if (args.length == 1) {
            for (SubCommand sc : uc.getCommandManager().getCommands()) {
                options.add(sc.getName());
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("toggle")) {
                if (args[0].equalsIgnoreCase("menu")) {
                    options.add("main");
                    options.add("buykey");
                    options.add("renamepet");
                }
                for (Category category : Category.enabled()) {
                    options.add(category.toString());
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                options.add("ammo");
                options.add("key");
            } else if (args[0].equalsIgnoreCase("clear")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    options.add(player.getName());
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("toggle")) {
                Category cat;
                try {
                    cat = Category.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException exc) {
                    return options;
                }

                if (cat == null || !cat.isEnabled()) return options;

                for (CosmeticType<?> cosm : cat.getEnabled()) {
                    options.add(cosm.toString());
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                for (Category category : Category.enabled()) {
                    options.add(category.toString());
                }
            } else if (args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("ammo")) {
                for (GadgetType gadgetType : GadgetType.enabled()) {
                    options.add(gadgetType.getConfigName());
                }
            }
        } else if (args.length == 4) {
            if ((args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("key")) || args[0].equalsIgnoreCase("toggle")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    options.add(player.getName());
                }
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("ammo")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    options.add(player.getName());
                }
            }
        } else if (args.length == 6) {
            if (!args[0].equalsIgnoreCase("treasure")) return options;
            for (World world : Bukkit.getWorlds()) {
                options.add(world.getName());
            }
        }
        List<String> results = new ArrayList<>();
        options.replaceAll(String::toLowerCase);
        Collections.sort(options);
        StringUtil.copyPartialMatches(args[args.length - 1], options, results);
        return results;
    }
}