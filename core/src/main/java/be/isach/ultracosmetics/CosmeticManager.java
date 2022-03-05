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
        // SuitType uses a static block
        if (Category.MORPHS.isEnabled()) {
            MorphType.register();
        }

        for (GadgetType gadgetType : GadgetType.values()) {
            setupCosmetic(config, gadgetType);
            if (gadgetType.affectPlayersPossible()) {
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Affect-Players", true, "Should it affect players? (Velocity, etc.)");
            }
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
            setupCosmetic(config, mountType);
            // If the mount type has a movement speed (is LivingEntity)
            if (LivingEntity.class.isAssignableFrom(mountType.getEntityType().getEntityClass())) {
                config.addDefault("Mounts." + mountType.getConfigName() + ".Speed", mountType.getDefaultMovementSpeed(), "The movement speed of the mount, see:", "https://minecraft.fandom.com/wiki/Attribute#Attributes_available_on_all_living_entities");
            }
            if (mountType.doesPlaceBlocks()) {
                // Don't use Stream#toList(), it doesn't exist in Java 8
                config.addDefault("Mounts." + mountType.getConfigName() + ".Blocks-To-Place", mountType.getDefaultBlocks().stream().map(m -> m.name()).collect(Collectors.toList()), "Blocks to choose from as this mount walks.");
            }
        }
        for (SuitCategory suit : SuitCategory.values()) {
            setupCosmetic(config, suit.getConfigPath());
        }

        if (Category.MORPHS.isEnabled()) {
            setupCategory(config, MorphType.values());
        }
        setupCategory(config, PetType.values());
        setupCategory(config, HatType.values());
        setupCategory(config, EmoteType.values());
        setupCategory(config, ParticleEffectType.values());

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

    private void setupCategory(CustomConfiguration config, List<? extends CosmeticType<?>> types) {
        for (CosmeticType<?> type : types) {
            setupCosmetic(config, type);
        }
    }

    private void setupCosmetic(CustomConfiguration config, CosmeticType<?> type) {
        setupCosmetic(config, type.getConfigPath());
    }

    private void setupCosmetic(CustomConfiguration config, String path) {
        // If someone can come up with better comments for these please do, but they're pretty self-explanatory
        config.addDefault(path + ".Enabled", true);
        config.addDefault(path + ".Show-Description", true, "Whether to show description when hovering in GUI");
        String findableKey = path + ".Can-Be-Found-In-Treasure-Chests";
        int weight = 1;
        if (config.isBoolean(findableKey)) {
            weight = config.getBoolean(findableKey) ? 1 : 0;
            config.set(findableKey, null);
        }
        config.addDefault(path + ".Treasure-Chest-Weight", weight, "The higher the weight, the better the chance of", "finding this cosmetic when this category is picked.", "Fractional values are not allowed.", "Set to 0 to disable finding in chests.");
        config.addDefault(path + ".Purchase-Price", 500, "Price to buy individually in GUI", "Only works if No-Permission.Allow-Purchase is true and this setting > 0");
    }
}
