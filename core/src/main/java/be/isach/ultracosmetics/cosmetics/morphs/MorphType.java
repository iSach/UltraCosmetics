package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ServerVersion;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 19/12/15.
 */
public enum MorphType {

    BAT("ultracosmetics.morphs.bat", "Bat", Material.COAL, (byte) 0, DisguiseType.BAT, "&7&oBecome a bat!", MorphBat.class),
    BLAZE("ultracosmetics.morphs.blaze", "Blaze", Material.BLAZE_POWDER, (byte) 0, DisguiseType.BLAZE, "&7&oIt might be hot on there..", MorphBlaze.class),
    CHICKEN("ultracosmetics.morphs.chicken", "Chicken", Material.EGG, (byte) 0, DisguiseType.CHICKEN, "&7&oBecome a chicken!", MorphChicken.class),
    PIG("ultracosmetics.morphs.pig", "Pig", Material.PORK, (byte) 0, DisguiseType.PIG, "&7&oMust hold your carrot!", MorphPig.class),
    ENDERMAN("ultracosmetics.morphs.enderman", "Enderman", Material.ENDER_PEARL, (byte) 0, DisguiseType.ENDERMAN, "&7&oI go from there to\n" +
            "&7&othere, then you lost me..", MorphEnderman.class),
    SLIME("ultracosmetics.morphs.slime", "Slime", Material.SLIME_BALL, (byte) 0, DisguiseType.SLIME, "&7&oSplat splat!", MorphSlime.class),
    CREEPER("ultracosmetics.morphs.creeper", "Creeper", Material.SULPHUR, (byte) 0, DisguiseType.CREEPER, "&7&oHey What'sssssss up?", MorphCreeper.class),
    WITHERSKELETON("ultracosmetics.morphs.witherskeleton", "WitherSkeleton", Material.SKULL_ITEM, (byte) 1, DisguiseType.WITHER_SKELETON, "&7&oJust a regular skeleton..\n&7&obut from the Nether!", MorphWitherSkeleton.class),
    SNOWNMAN("ultracosmetics.morphs.snowman", "Snowman", Material.SNOW_BALL, (byte) 0, DisguiseType.SNOWMAN, "&7&oBecome Olaf!", MorphSnowman.class),
    ELDERGUARDIAN("ultracosmetics.morphs.elderguardian", "ElderGuardian", Material.PRISMARINE_CRYSTALS, (byte) 0, DisguiseType.valueOf("ELDER_GUARDIAN"), "&7&oBecome an Elder Guardian!!", UltraCosmetics.getInstance().getMorphs().getElderGuardianClass());

    /**
     * List of all the enabled Morphs.
     */
    public static List<MorphType> enabled = new ArrayList<>();

    /**
     * Morph Description.
     */
    private String description,

    /**
     * Required permission to toggle the morph.
     */
    permission,

    /**
     * Path name in config.
     */
    configName;

    /**
     * Item Material in menu.
     */
    private Material material;

    /**
     * Item data in menu.
     */
    private byte data;

    /**
     * Disguise Type of the morph.
     */
    private DisguiseType disguiseType;

    /**
     * Class extending Morph.
     */
    private Class<? extends Morph> clazz;

    MorphType(String permission, String configName, Material material, byte data, DisguiseType disguiseType, String defaultDesc, Class<? extends Morph> clazz) {
        this.permission = permission;
        this.configName = configName;
        this.material = material;
        this.data = data;
        this.disguiseType = disguiseType;
        this.clazz = clazz;

        if (SettingsManager.getConfig().get("Morphs." + configName + ".Description") == null) {
            this.description = defaultDesc;
            SettingsManager.getConfig().addDefault("Morphs." + configName + ".Description", getColoredDescription(), "Description of this Morph.");
        } else
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Morphs." + configName + ".Description")));
    }

    /**
     * Equips the MorphType to player.
     *
     * @param player The Morph receiver.
     * @return The Morph Object equipped to the player.
     */
    public Morph equip(Player player) {
        Morph effect = null;
        try {
            effect = clazz.getDeclaredConstructor(UUID.class).newInstance(player == null ? null : player.getUniqueId());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return effect;
    }

    /**
     * Get a list of enabled Morph Types.
     *
     * @return A list containing all the enabled MorphTypes.
     */
    public static List<MorphType> enabled() {
        return enabled;
    }

    /**
     * Get the required permission.
     *
     * @return The required permission to toggle the morph.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Get the skill message.
     *
     * @return The skill message of the morph.
     */
    public String getSkill() {
        return MessageManager.getMessage("Morphs." + configName + ".skill");
    }

    /**
     * Check if the MorphType is enabled.
     *
     * @return {@code true} if the morphtype is enabled, otherwise {@code false}.
     */
    public boolean isEnabled() {
        if(UltraCosmetics.getServerVersion().compareTo(ServerVersion.v1_9_R1) >= 0 && this == ELDERGUARDIAN) return false;
        return SettingsManager.getConfig().getBoolean("Morphs." + configName + ".Enabled");
    }

    /**
     * Get the Item's Material in Menu.
     *
     * @return The Item's Material in Menu
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Get the Item's Data in Menu.
     *
     * @return The Item's Data in Menu
     */
    public byte getData() {
        return data;
    }

    /**
     * Get the morph Disguise Type.
     *
     * @return
     */
    public DisguiseType getDisguiseType() {
        return disguiseType;
    }

    /**
     * Get the Path name in config.
     *
     * @return The config path name.
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * Get the Item's Name in Menu.
     *
     * @return The Item's Name in Menu
     */
    public String getName() {
        return MessageManager.getMessage("Morphs." + configName + ".name");
    }

    /**
     * Get the Description in Menu as a String List.
     *
     * @return The String List containing the Description of the Morph, One String = One Line.
     */
    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n")) {
            desc.add(string.replace('&', '§'));
        }
        return desc;
    }

    /**
     * Get the description as a String list with colors.
     *
     * @return
     */
    public List<String> getColoredDescription() {
        return Arrays.asList(description.split("\n"));
    }

    /**
     * Converts a List Description to a String Description.
     *
     * @param description The List Description.
     * @return The description as a String, lines seperated by "\n"
     */
    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    /**
     * Check if the MorphType should show its description in menu.
     *
     * @return {@code true} if the morphtype should show its description in menu, otherwise {@code false}.
     */
    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Morphs." + getConfigName() + ".Show-Description");
    }

    /**
     * Check if the morph can be found in a Treasure Chest.
     *
     * @return {@code true} if the morph can be found, {@code false} if it can't.
     */
    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Morphs." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
    }
}
