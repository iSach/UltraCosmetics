package be.isach.ultracosmetics;

import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.LivingEntity;

/**
 * Cosmetic manager.
 *
 * @author iSach
 * @since 08-09-2016
 */
public class CosmeticManager {

    private UltraCosmetics ultraCosmetics;

    public CosmeticManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    /**
     * Setup default Cosmetics config.
     */
    public void setupCosmeticsConfigs() {
        CustomConfiguration config = ultraCosmetics.getConfig();
        for (Category category : Category.values()) {
            config.addDefault("Categories-Enabled." + category.getConfigPath(), true);
            config.addDefault("Categories." + category.getConfigPath() + ".Go-Back-Arrow", true, "Want Go back To Menu Item in that menu?");
        }

        config.addDefault("TreasureChests.Loots.Emotes.Enabled", true);
        config.addDefault("TreasureChests.Loots.Emotes.Chance", 5);
        config.addDefault("TreasureChests.Loots.Emotes.Message.enabled", true);
        config.addDefault("TreasureChests.Loots.Emotes.Message.message", "%prefix% &6&l%name% found rare %emote%");
        config.addDefault("Ammo-System-For-Gadgets.Show-Ammo-In-Menu-As-Item-Amount", true, "Do you want that in the gadgets menu", "each gadget item has an amount", "corresponding to your ammo.");

        // CALL STATIC BLOCK.
        GadgetType.register();
        MountType.register();
        ParticleEffectType.register();
        PetType.register();
        HatType.register();
        if (Category.MORPHS.isEnabled()) {
            MorphType.register();
        }

        for (GadgetType gadgetType : GadgetType.values()) {
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Affect-Players", true, "Should it affect players? (Velocity, etc.)");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Enabled", true, "if true, the gadget will be enabled.");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Show-Description", true, "if true, the description of gadget will be showed.");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
            if (gadgetType == GadgetType.valueOf("paintballgun")) {
                // default "" so we don't have to deal with null
                if (config.getString("Gadgets." + gadgetType.getConfigName() + ".Block-Type", "").equals("STAINED_CLAY")) {
                    config.set("Gadgets." + gadgetType.getConfigName() + ".Block-Type", "_TERRACOTTA", "With what block will it paint?", "Uses all blocks that end with the supplied string. For values, see:", "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
                }
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Block-Type", "_TERRACOTTA", "With what block will it paint?", "Uses all blocks that end with the supplied string. For values, see:", "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Particle.Enabled", false, "Should it display particles?");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Particle.Effect", "FIREWORKS_SPARK", "what particles? (List: http://pastebin.com/CVKkufck)");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Radius", 2, "The radius of painting.");
                List<String> blackListedBlocks = new ArrayList<>();
                blackListedBlocks.add("REDSTONE_BLOCK");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".BlackList", blackListedBlocks, "A list of the BLOCKS that", "can't be painted.");
            }
            if (UltraCosmeticsData.get().isAmmoEnabled()) {
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Enabled", true, "You want this gadget to need ammo?");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Price", 500, "What price for the ammo?");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Result-Amount", 20, "And how much ammo is given", "when bought?");
            }
        }

        for (MountType mountType : MountType.values()) {
            config.addDefault("Mounts." + mountType.getConfigName() + ".Enabled", true, "if true, the mount will be enabled.");
            config.addDefault("Mounts." + mountType.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Mounts." + mountType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
            // If the mount type has a movement speed (is LivingEntity)
            if (LivingEntity.class.isAssignableFrom(mountType.getEntityType().getEntityClass())) {
                config.addDefault("Mounts." + mountType.getConfigName() + ".Speed", mountType.getDefaultMovementSpeed(), "The movement speed of the mount, see:", "https://minecraft.fandom.com/wiki/Attribute#Attributes_available_on_all_living_entities");
            }
            if (mountType.doesPlaceBlocks()) {
                // Don't use Stream#toList(), it doesn't exist in Java 8
                config.addDefault("Mounts." + mountType.getConfigName() + ".Blocks-To-Place", mountType.getDefaultBlocks().stream().map(m -> m.name()).collect(Collectors.toList()), "Blocks to choose from as this mount walks.");
            }
        }

        for (ParticleEffectType particleEffect : ParticleEffectType.values()) {
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Enabled", true, "if true, the effect will be enabled.");
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (PetType pet : PetType.values()) {
            config.addDefault("Pets." + pet.getConfigName() + ".Enabled", true, "if true, the pet will be enabled.");
            config.addDefault("Pets." + pet.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Pets." + pet.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }
        if (Category.MORPHS.isEnabled())
            for (MorphType morphType : MorphType.values()) {
                config.addDefault("Morphs." + morphType.getConfigName() + ".Enabled", true, "if true, the morph will be enabled.");
                config.addDefault("Morphs." + morphType.getConfigName() + ".Show-Description", true, "if true, the description of this morph will be showed.");
                config.addDefault("Morphs." + morphType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
            }
        for (HatType hat : HatType.values()) {
            config.addDefault("Hats." + hat.getConfigName() + ".Enabled", true, "if true, the hat will be enabled.");
            config.addDefault("Hats." + hat.getConfigName() + ".Show-Description", true, "if true, the description of this hat will be showed.");
            config.addDefault("Hats." + hat.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }
        for (SuitType suit : SuitType.values()) {
            config.addDefault("Suits." + suit.getConfigName() + ".Enabled", true, "if true, the suit will be enabled.");
            config.addDefault("Suits." + suit.getConfigName() + ".Show-Description", true, "if true, the description of this suit will be showed.");
            config.addDefault("Suits." + suit.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (EmoteType emoteType : EmoteType.values()) {
            config.addDefault("Emotes." + emoteType.getConfigName() + ".Enabled", true, "if true, the mount will be enabled.");
            config.addDefault("Emotes." + emoteType.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Emotes." + emoteType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        try {
            config.save(ultraCosmetics.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        GadgetType.checkEnabled();
        MountType.checkEnabled();
        ParticleEffectType.checkEnabled();
        PetType.checkEnabled();
        HatType.checkEnabled();
        SuitType.checkEnabled();
        EmoteType.checkEnabled();
        if (Category.MORPHS.isEnabled()) {
            MorphType.checkEnabled();
        }

        try {
            config.save(ultraCosmetics.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
