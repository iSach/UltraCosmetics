package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.MorphType;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffectType;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.SuitType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sacha on 24/12/15.
 */
public class GeneralUtil {

    /**
     * Print permissions in a permissions.txt file.
     */
    public static void printPermissions() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(Core.getPlugin().getDataFolder(), "permissions.yml"), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        writer.println();
        writer.println("UltraCosmetics v" + Core.currentVersion + " permissions.");
        writer.println("Generated automatically on " + dateFormat.format(date));
        writer.println();
        writer.println();

        writer.println("General permissions, enabled by default.");
        writer.println("  - ultracosmetics.receivechest");
        writer.println("  - ultracosmetics.openmenu");
        writer.println("");
        writer.println("Commands:");
        writer.println("  - ultracosmetics.command.*");
        for (SubCommand subCommand : Core.commandManager.commands)
            writer.println("  - " + subCommand.getPermission());
        writer.println("");
        writer.println("Gadgets:");
        writer.println("  - ultracosmetics.gadgets.*");
        for (GadgetType gadgetType : GadgetType.values())
            writer.println("  - " + gadgetType.getPermission());
        writer.println("");
        writer.println("Pets:");
        writer.println("  - ultracosmetics.pets.*");
        for (PetType petType : PetType.values())
            writer.println("  - " + petType.getPermission());
        writer.println("");
        writer.println("Mounts:");
        writer.println("  - ultracosmetics.mounts.*");
        for (MountType mountType : MountType.values())
            writer.println("  - " + mountType.getPermission());
        writer.println("");
        writer.println("Morphs:");
        writer.println("  - ultracosmetics.morphs.*");
        for (MorphType morphType : MorphType.values())
            writer.println("  - " + morphType.getPermission());
        writer.println("");
        writer.println("Hats:");
        writer.println("  - ultracosmetics.hats.*");
        for (Hat hat : Hat.values())
            writer.println("  - " + hat.getPermission());
        writer.println("");
        writer.println("Particle Effects:");
        writer.println("  - ultracosmetics.particleeffects.*");
        for (ParticleEffectType effect : ParticleEffectType.values())
            writer.println("  - " + effect.getPermission());
        writer.println("");
        writer.println("Suits:");
        writer.println("  - ultracosmetics.suits.*");
        for (SuitType suit : SuitType.values()) {
            writer.println("  - ultracosmetics.suits." + suit.getConfigName().toLowerCase() + ".*");
            for (ArmorSlot armorSlot : ArmorSlot.values())
                writer.println("  - " + suit.getPermission(armorSlot));
        }
        writer.println("");

        writer.close();
    }

}
