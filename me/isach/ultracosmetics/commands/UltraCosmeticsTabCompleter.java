package me.isach.ultracosmetics.commands;

import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.morphs.Morph;
import me.isach.ultracosmetics.cosmetics.mounts.Mount;
import me.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import me.isach.ultracosmetics.cosmetics.pets.Pet;
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
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("gadget")) {
                ArrayList<String> gadgets = new ArrayList<>();

                if (!args[1].equals("")) {
                    for (Gadget.GadgetType type : Gadget.GadgetType.values()) {
                        if (type.isEnabled() && type.toString().toLowerCase().startsWith(args[1].toLowerCase())) {
                            gadgets.add(type.toString().toLowerCase());
                        }
                    }
                    if ("clear".startsWith(args[1].toLowerCase())) {
                        gadgets.add("clear");
                    }
                } else {
                    for (Gadget.GadgetType type : Gadget.GadgetType.values()) {
                        if (type.isEnabled()) {
                            gadgets.add(type.toString().toLowerCase());
                        }
                    }
                    gadgets.add("clear");
                }

                Collections.sort(gadgets);

                return gadgets;
            } else if (args[0].equalsIgnoreCase("mount")) {
                ArrayList<String> mounts = new ArrayList<>();

                if (!args[1].equals("")) {
                    for (Mount.MountType type : Mount.MountType.values()) {
                        if (type != Mount.MountType.DEFAULT)
                            if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[1].toLowerCase())) {
                                mounts.add(type.toString().toLowerCase());
                            }
                    }
                    if ("clear".startsWith(args[1].toLowerCase())) {
                        mounts.add("clear");
                    }
                } else {
                    for (Mount.MountType type : Mount.MountType.values()) {
                        if (type != Mount.MountType.DEFAULT)
                            if (type.isEnabled()) {
                                mounts.add(type.toString().toLowerCase());
                            }
                    }
                    mounts.add("clear");
                }

                Collections.sort(mounts);

                return mounts;
            } else if (args[0].equalsIgnoreCase("effect")) {
                ArrayList<String> effects = new ArrayList<>();

                if (!args[1].equals("")) {
                    for (ParticleEffect.ParticleEffectType type : ParticleEffect.ParticleEffectType.values()) {
                        if (type != ParticleEffect.ParticleEffectType.DEFAULT)
                            if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[1].toLowerCase())) {
                                effects.add(type.toString().toLowerCase());
                            }
                    }
                    if ("clear".startsWith(args[1].toLowerCase())) {
                        effects.add("clear");
                    }
                } else {
                    for (ParticleEffect.ParticleEffectType type : ParticleEffect.ParticleEffectType.values()) {
                        if (type != ParticleEffect.ParticleEffectType.DEFAULT)
                            if (type.isEnabled()) {
                                effects.add(type.toString().toLowerCase());
                            }
                    }
                    effects.add("clear");
                }

                Collections.sort(effects);

                return effects;
            } else if (args[0].equalsIgnoreCase("pet")) {
                ArrayList<String> pets = new ArrayList<>();

                if (!args[1].equals("")) {
                    for (Pet.PetType type : Pet.PetType.values()) {
                        if (type != Pet.PetType.DEFAULT)
                            if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[1].toLowerCase())) {
                                pets.add(type.toString().toLowerCase());
                            }
                    }
                    if ("clear".startsWith(args[1].toLowerCase())) {
                        pets.add("clear");
                    }
                } else {
                    for (Pet.PetType type : Pet.PetType.values()) {
                        if (type != Pet.PetType.DEFAULT)
                            if (type.isEnabled()) {
                                pets.add(type.toString().toLowerCase());
                            }
                    }
                    pets.add("clear");
                }

                Collections.sort(pets);

                return pets;
            } else if (args[0].equalsIgnoreCase("morph")) {
                ArrayList<String> morphs = new ArrayList<>();

                if (!args[1].equals("")) {
                    for (Morph.MorphType type : Morph.MorphType.values()) {
                        if (type.isEnabled() && type.toString().toLowerCase().toLowerCase().startsWith(args[1].toLowerCase())) {
                            morphs.add(type.toString().toLowerCase());
                        }
                    }
                    if ("clear".startsWith(args[1].toLowerCase())) {
                        morphs.add("clear");
                    }
                } else {
                    for (Morph.MorphType type : Morph.MorphType.values()) {
                        if (type.isEnabled()) {
                            morphs.add(type.toString().toLowerCase());
                        }
                    }
                    morphs.add("clear");
                }

                Collections.sort(morphs);

                return morphs;
            } else if (args[0].equalsIgnoreCase("menu")) {
                ArrayList<String> completes = new ArrayList<>();

                ArrayList<String> completeBefore = new ArrayList<>();
                completeBefore.add("pets");
                completeBefore.add("gadgets");
                completeBefore.add("mounts");
                completeBefore.add("effects");
                completeBefore.add("morphs");

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
        } else if (args.length == 1) {
            ArrayList<String> completes = new ArrayList<>();

            ArrayList<String> completeBefore = new ArrayList<>();
            completeBefore.add("menu");
            completeBefore.add("gadget");
            completeBefore.add("pet");
            completeBefore.add("effect");
            completeBefore.add("mount");
            completeBefore.add("clear");
            completeBefore.add("reload");
            completeBefore.add("chest");
            completeBefore.add("morph");


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
