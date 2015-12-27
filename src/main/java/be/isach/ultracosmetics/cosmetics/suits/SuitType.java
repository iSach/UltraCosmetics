package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 20/12/15.
 */
public enum SuitType {

    RAVE("Rave", "rave", "&7&oSuch amazing colors!", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, SuitRave.class),
    ASTRONAUT("Astronaut", "astronaut", "&7&oHouston?", Material.GLASS, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, SuitAstronaut.class),
    DIAMOND("Diamond", "diamond", "&7&oShow your Mining skills\n&7&owith this amazing outfit!", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, SuitDiamond.class),
    SANTA("Santa", "santa", "&7&oBecome Santa and deliver presents!", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, SuitSanta.class);

    /**
     * The required permission to toggle SuitType.
     */
    private String permission,

    /**
     * SuitType description.
     */
    description,

    /**
     * Config path name.
     */
    configName;

    /**
     * The suit Class, extending Suit.
     */
    private Class<? extends Suit> clazz;

    /**
     * The Helmet material.
     */
    private Material helmet,

    /**
     * The Chestplate material.
     */
    chestplate,

    /**
     * The Leggings material.
     */
    leggings,

    /**
     * The Boots material.
     */
    boots;

    /**
     * List of all the enabled Suits.
     */
    public static List<SuitType> enabled = new ArrayList<>();

    /**
     * @param configName       The config path name.
     * @param permissionSuffix The suffix of permission. (ultracosmetic.suits.{suffix}.{part})
     * @param defaultDesc      The default description.
     * @param h                The Helmet material.
     * @param c                The Chestplate material.
     * @param l                The Leggings material.
     * @param b                The Boots material.
     * @param clazz            The Suit Class
     */
    SuitType(String configName, String permissionSuffix, String defaultDesc,
             Material h, Material c, Material l, Material b, Class<? extends Suit> clazz) {
        this.permission = "ultracosmetics.suits." + permissionSuffix;
        this.description = defaultDesc;
        this.configName = configName;
        this.boots = b;
        this.helmet = h;
        this.chestplate = c;
        this.leggings = l;
        this.clazz = clazz;
    }

    /**
     * Equips the Suit to a player.
     *
     * @param player    The receiver of the suit.
     * @param armorSlot The Armor Slot.
     * @return The suit Object equipped to the player.
     */
    public Suit equip(Player player, ArmorSlot armorSlot) {
        Suit effect = null;
        try {
            effect = clazz.getDeclaredConstructor(UUID.class, ArmorSlot.class)
                    .newInstance(player == null ? null : player.getUniqueId(), armorSlot);
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
     * Get the Helmet material in menu
     *
     * @return The Helmet material in menu
     */
    public Material getHelmet() {
        return helmet;
    }

    /**
     * Get the Chestplate material in menu
     *
     * @return The Chestplate material in menu
     */
    public Material getChestplate() {
        return chestplate;
    }

    /**
     * Get the Leggings material in menu
     *
     * @return The Leggings material in menu
     */
    public Material getLeggings() {
        return leggings;
    }

    /**
     * Get the Boots material in menu
     *
     * @return The Boots material in menu
     */
    public Material getBoots() {
        return boots;
    }

    public Material getMaterial(ArmorSlot armorSlot) {
        switch (armorSlot) {
            default:
                return getChestplate();
            case HELMET:
                return getHelmet();
            case LEGGINGS:
                return getLeggings();
            case BOOTS:
                return getBoots();
        }
    }

    /**
     * Get the SuitType's name in menu.
     *
     * @return The SuitType's name in menu.
     */
    public String getName(ArmorSlot armorSlot) {
        return MessageManager.getMessage("Suits." + configName + "." + armorSlot.toString().toLowerCase() + "-name");
    }

    /**
     * Get the permission required to toggle suit.
     *
     * @return The required permission to toggle the suittype.
     */
    public String getPermission(ArmorSlot armorSlot) {
        return permission + "." + armorSlot.toString().toLowerCase();
    }

    /**
     * Check if the SuitType is enabled.
     *
     * @return {@code true} if the suit is enabled in config, otherwise {@code false}.
     */
    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Suits." + configName + ".Enabled");
    }

    /**
     * Get a list of the SuitTypes enabled.
     *
     * @return A list of all the enabled SuitTypes.
     */
    public static List<SuitType> enabled() {
        return enabled;
    }

    /**
     * Get the path name in config.
     *
     * @return The path name in config.
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * Transforms the description from a String to a list.
     * Without colors.
     *
     * @return The description as a list.
     */
    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n"))
            desc.add(string.replace('&', '§'));
        return desc;
    }

    /**
     * Transforms the description from a String to a list.
     * With colors.
     *
     * @return The description as a list.
     */
    public List<String> getDescriptionColored() {
        return Arrays.asList(description.split("\n"));
    }

    /**
     * Check if the Suittype should show a description.
     *
     * @return {@code true} if it should show a description, otherwise {@code false}.
     */
    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Suits." + getConfigName() + ".Show-Description");
    }

    /**
     * Check if the Suittype can be found in Treasure Chests.
     *
     * @return {@code true} if it can be found in treasure chests, otherwise {@code false}.
     */
    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Suits." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
    }

    /**
     * Get the description as a String from list.
     *
     * @param description The Description as a list.
     * @return The description as a String.
     */
    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++)
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        return stringBuilder.toString();
    }

}
