package be.isach.ultracosmetics.command;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
        if (cmd.getName().equalsIgnoreCase("uc") || cmd.getName().equalsIgnoreCase("ultracosmetics")) {
            if (args.length == 1) {
                List<String> commands = new ArrayList<>();
                for (SubCommand sc : uc.getCommandManager().getCommands()) {
                    commands.add(sc.aliases[0]);
                }

                Collections.sort(commands);

                ArrayList<String> toReturn = new ArrayList<>();
                for (String s : commands) {
                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                        toReturn.add(s);
                    }
                }

                return toReturn;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("menu")) {
                    List<String> commands = new ArrayList<>();

                    for (Category category : Category.enabled()) {
                        commands.add(category.toString().toLowerCase());
                    }

                    commands.add("main");
                    commands.add("buykey");
                    commands.add("renamepet");
                    commands.add("renamepet");

                    Collections.sort(commands);

                    ArrayList<String> toReturn = new ArrayList<>();
                    for (String s : commands) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            toReturn.add(s);
                        }
                    }

                    return toReturn;
                } else if (args[0].equalsIgnoreCase("give")) {
                    List<String> commands = new ArrayList<>();

                    commands.add("ammo");
                    commands.add("key");

                    ArrayList<String> toReturn = new ArrayList<>();
                    for (String s : commands) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            toReturn.add(s);
                        }
                    }

                    return toReturn;
                } else if (args[0].equalsIgnoreCase("toggle")) {
                    List<String> commands = new ArrayList<>();

                    for (Category category : Category.enabled()) {
                        commands.add(category.toString().toLowerCase());
                    }

                    Collections.sort(commands);

                    ArrayList<String> toReturn = new ArrayList<>();
                    for (String s : commands) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            toReturn.add(s);
                        }
                    }

                    return toReturn;
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    String type = args[1].toUpperCase();
                    try {
                        Category cat = Category.valueOf(type);
                        if (cat != null && cat.isEnabled()) {

                            List<String> commands = new ArrayList<>();

                            for (CosmeticType cosm : cat.getEnabled()) {
                                commands.add(cosm.toString().toLowerCase());
                            }

                            Collections.sort(commands);

                            ArrayList<String> toReturn = new ArrayList<>();
                            for (String s : commands) {
                                if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                                    toReturn.add(s);
                                }
                            }

                            return toReturn;
                        }
                    } catch (Exception exc) {
                    }
                } else if (args[0].equalsIgnoreCase("clear")) {
                    List<String> commands = new ArrayList<>();

                    for (Category category : Category.enabled()) {
                        commands.add(category.toString().toLowerCase());
                    }

                    Collections.sort(commands);

                    ArrayList<String> toReturn = new ArrayList<>();
                    for (String s : commands) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            toReturn.add(s);
                        }
                    }

                    return toReturn;
                } else if (args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("ammo")) {
                    List<String> commands = new ArrayList<>();

                    for(GadgetType gadgetType : GadgetType.enabled()) {
                        commands.add(gadgetType.getConfigName());
                    }

                    ArrayList<String> toReturn = new ArrayList<>();
                    for (String s : commands) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            toReturn.add(s);
                        }
                    }

                    return toReturn;
                }
            } else if (args.length == 6) {
                if (args[0].equalsIgnoreCase("treasure")) {
                    List<String> commands = new ArrayList<>();

                    for (World world : Bukkit.getWorlds()) {
                        commands.add(world.getName().toLowerCase());
                    }

                    Collections.sort(commands);

                    ArrayList<String> toReturn = new ArrayList<>();
                    for (String s : commands) {
                        if (s.toLowerCase().startsWith(args[5].toLowerCase())) {
                            toReturn.add(s);
                        }
                    }

                    return toReturn;
                }
            }
        }

        // Enocraft showcase command - TODO: Refactor structure of tab completer
        if (cmd.getName().equalsIgnoreCase("ucs") || cmd.getName().equalsIgnoreCase("ultracosmeticsshowcase")) {
            List<String> commands = new ArrayList<>();
            switch(args.length) {
                case 1:
                    commands = new ArrayList<>();
                    commands.add("clear");
                    commands.add("equip");
                    return commands;
                case 2:
                    if (args[0].equalsIgnoreCase("equip")) {
                        commands = new ArrayList<>();
                        for (Category category : Category.enabled()) {
                            commands.add(category.toString().toLowerCase());
                        }

                        Collections.sort(commands);
                        return StringUtil.copyPartialMatches(args[1], commands, new ArrayList<>());
                    } else if (args[0].equalsIgnoreCase("clear")) {
                        commands = new ArrayList<>();

                        for (NPC npc : CitizensAPI.getNPCRegistry()) {
                            commands.add(String.valueOf(npc.getId()));
                        }

                        Collections.sort(commands);

                        return StringUtil.copyPartialMatches(args[1], commands, new ArrayList<>());
                    }
                case 3:
                    if (args[0].equalsIgnoreCase("equip")) {
                        String type = args[1].toUpperCase();
                        try {
                            Category cat = Category.valueOf(type);
                            if (cat != null && cat.isEnabled()) {
                                commands = new ArrayList<>();
                                for (CosmeticType cosm : cat.getEnabled()) {
                                    commands.add(cosm.toString().toLowerCase());
                                }

                                Collections.sort(commands);

                                return StringUtil.copyPartialMatches(args[2], commands, new ArrayList<>());
                            }
                        } catch (Exception exc) {}
                    } else if (args[0].equalsIgnoreCase("clear")) {
                        commands = new ArrayList<>();

                        for (Category category : Category.enabled()) {
                            commands.add(category.toString().toLowerCase());
                            commands.add("all"); // Add a clear all command for NPCs
                        }

                        Collections.sort(commands);

                        return StringUtil.copyPartialMatches(args[2], commands, new ArrayList<>());
                    }
                case 4:
                    if (args[0].equalsIgnoreCase("equip")) {
                        commands = new ArrayList<>();

                        for (NPC npc : CitizensAPI.getNPCRegistry()) {
                            commands.add(String.valueOf(npc.getId()));
                        }

                        Collections.sort(commands);

                        return StringUtil.copyPartialMatches(args[3], commands, new ArrayList<>());
                    }
                default:
                    break;

            }
        }
        return null;
    }
}