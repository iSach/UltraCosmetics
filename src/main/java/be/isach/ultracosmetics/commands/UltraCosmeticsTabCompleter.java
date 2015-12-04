package be.isach.ultracosmetics.commands;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sacha on 14/08/15.
 */
public class UltraCosmeticsTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("toggle")) {
                if (args[1].equalsIgnoreCase("gadget")) {
                    ArrayList<String> gadgets = new ArrayList<>();

                    if (!args[2].equals("")) {
                        for (GadgetType type : GadgetType.values()) {
                            if (type.isEnabled() && type.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
                                gadgets.add(type.toString().toLowerCase());
                            }
                        }
                    } else {
                        for (GadgetType type : GadgetType.values()) {
                            if (type.isEnabled()) {
                                gadgets.add(type.toString().toLowerCase());
                            }
                        }
                    }

                    Collections.sort(gadgets);

                    return gadgets;
                } else if (args[1].equalsIgnoreCase("mount")) {
                    ArrayList<String> mounts = new ArrayList<>();

                    if (!args[2].equals("")) {
                        for (Mount.MountType type : Mount.MountType.values()) {
                            if (type != Mount.MountType.DEFAULT)
                                if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    mounts.add(type.toString().toLowerCase());
                                }
                        }
                    } else {
                        for (Mount.MountType type : Mount.MountType.values()) {
                            if (type != Mount.MountType.DEFAULT)
                                if (type.isEnabled()) {
                                    mounts.add(type.toString().toLowerCase());
                                }
                        }
                    }

                    Collections.sort(mounts);

                    return mounts;
                } else if (args[1].equalsIgnoreCase("hat")) {
                    ArrayList<String> hats = new ArrayList<>();

                    if (!args[2].equals("")) {
                        for (Hat hat : Hat.values())
                            if (hat.isEnabled() && hat.toString().toLowerCase().toLowerCase().startsWith(args[2].toLowerCase()))
                                hats.add(hat.toString().toLowerCase());
                    } else {
                        for (Hat hat : Hat.values()) {
                            if (hat.isEnabled())
                                hats.add(hat.toString().toLowerCase());

                        }
                    }
                    Collections.sort(hats);

                    return hats;
                } else if (args[1].equalsIgnoreCase("effect")) {
                    ArrayList<String> effects = new ArrayList<>();

                    if (!args[2].equals("")) {
                        for (ParticleEffect.ParticleEffectType type : ParticleEffect.ParticleEffectType.values()) {
                            if (type != ParticleEffect.ParticleEffectType.DEFAULT)
                                if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    effects.add(type.toString().toLowerCase());
                                }
                        }
                    } else {
                        for (ParticleEffect.ParticleEffectType type : ParticleEffect.ParticleEffectType.values()) {
                            if (type != ParticleEffect.ParticleEffectType.DEFAULT)
                                if (type.isEnabled()) {
                                    effects.add(type.toString().toLowerCase());
                                }
                        }
                    }

                    Collections.sort(effects);

                    return effects;
                } else if (args[1].equalsIgnoreCase("pet")) {
                    ArrayList<String> pets = new ArrayList<>();

                    if (!args[2].equals("")) {
                        for (Pet.PetType type : Pet.PetType.values()) {
                            if (type != Pet.PetType.DEFAULT)
                                if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    pets.add(type.toString().toLowerCase());
                                }
                        }
                    } else {
                        for (Pet.PetType type : Pet.PetType.values()) {
                            if (type != Pet.PetType.DEFAULT)
                                if (type.isEnabled()) {
                                    pets.add(type.toString().toLowerCase());
                                }
                        }
                    }

                    Collections.sort(pets);

                    return pets;
                } else if (args[1].equalsIgnoreCase("morph")) {
                    ArrayList<String> morphs = new ArrayList<>();

                    if (!args[2].equals("")) {
                        for (Morph.MorphType type : Morph.MorphType.values()) {
                            if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[2].toLowerCase())) {
                                morphs.add(type.toString().toLowerCase());
                            }
                        }
                    } else {
                        for (Morph.MorphType type : Morph.MorphType.values()) {
                            if (type.isEnabled()) {
                                morphs.add(type.toString().toLowerCase());
                            }
                        }
                    }

                    Collections.sort(morphs);

                    return morphs;
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                if (args[1].equalsIgnoreCase("ammo")) {
                    ArrayList<String> gadgets = new ArrayList<>();

                    if (!args[2].equals("")) {
                        for (GadgetType type : GadgetType.values()) {
                            if (type.isEnabled() && type.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
                                gadgets.add(type.toString().toLowerCase());
                            }
                        }
                    } else {
                        for (GadgetType type : GadgetType.values()) {
                            if (type.isEnabled()) {
                                gadgets.add(type.toString().toLowerCase());
                            }
                        }
                    }

                    Collections.sort(gadgets);

                    return gadgets;
                }
            } else if (args[0].equalsIgnoreCase("menu")) {
                ArrayList<String> completes = new ArrayList<>();

                ArrayList<String> completeBefore = new ArrayList<>();
                completeBefore.add("pet");
                completeBefore.add("gadget");
                completeBefore.add("mount");
                completeBefore.add("effect");
                completeBefore.add("morph");
                completeBefore.add("hat");
                completeBefore.add("main");

                if (!args[1].equals("")) {
                    for (String complete : completeBefore) {
                        if (complete.toLowerCase().toLowerCase().startsWith(args[2].toLowerCase()))
                            completes.add(complete.toLowerCase());
                    }
                } else {
                    for (String complete : completeBefore) {
                        completes.add(complete.toLowerCase());
                    }
                }

                Collections.sort(completes);

                return completes;
            }
        } else if (args.length == 2)

        {

            if (args[0].equalsIgnoreCase("toggle")) {

                ArrayList<String> completes = new ArrayList<>();

                ArrayList<String> completeBefore = new ArrayList<>();
                for (Category category : Core.enabledCategories) {
                    completeBefore.add(category.toString().toLowerCase().replace("s", ""));
                }

                if (!args[1].equals("")) {
                    for (String complete : completeBefore) {
                        if (complete.toLowerCase().toLowerCase().startsWith(args[1].toLowerCase()))
                            completes.add(complete.toLowerCase());
                    }
                } else {
                    for (String complete : completeBefore) {
                        completes.add(complete.toLowerCase());
                    }
                }

                Collections.sort(completes);

                return completes;
            } else if (args[0].equalsIgnoreCase("give")) {
                ArrayList<String> completes = new ArrayList<>();

                ArrayList<String> completeBefore = new ArrayList<>();
                completeBefore.add("key");
                completeBefore.add("ammo");


                if (!args[1].equals("")) {
                    for (String complete : completeBefore) {
                        if (complete.toLowerCase().toLowerCase().startsWith(args[1].toLowerCase()))
                            completes.add(complete.toLowerCase());
                    }
                } else {
                    for (String complete : completeBefore) {
                        completes.add(complete.toLowerCase());
                    }
                }

                Collections.sort(completes);

                return completes;
            }

        } else if (args.length == 1)

        {
            ArrayList<String> completes = new ArrayList<>();

            ArrayList<String> completeBefore = new ArrayList<>();
            completeBefore.add("toggle");
            completeBefore.add("chest");
            completeBefore.add("onClear");
            completeBefore.add("reload");
            completeBefore.add("give");
            completeBefore.add("menu");


            if (!args[0].equals("")) {
                for (String complete : completeBefore) {
                    if (complete.toLowerCase().toLowerCase().startsWith(args[0].toLowerCase()))
                        completes.add(complete.toLowerCase());
                }
            } else {
                for (String complete : completeBefore) {
                    completes.add(complete.toLowerCase());
                }
            }

            Collections.sort(completes);

            return completes;
        }

        return null;
    }
}
