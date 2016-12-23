package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Sacha on 20/12/15.
 */
public class SuitType extends CosmeticMatType<Suit> {

    private final static List<SuitType> ENABLED = new ArrayList<>();
    private final static List<SuitType> VALUES = new ArrayList<>();

    public static List<SuitType> enabled() {
        return ENABLED;
    }

    public static List<SuitType> values() {
        return VALUES;
    }

    public static SuitType valueOf(String s) {
        for (SuitType suitType : VALUES) {
            if (suitType.getConfigName().equalsIgnoreCase(s)) return suitType;
        }
        return null;
    }

    public static SuitType getByName(String s) {
        try {
            return VALUES.stream().filter(value -> value.getName().equalsIgnoreCase(s)).findFirst().get();
        } catch (Exception exc) {
            return null;
        }
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    public final static SuitType RAVE = new SuitType("Rave", "rave", "&7&oSuch amazing colors!", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, SuitRave.class);
    public final static SuitType ASTRONAUT = new SuitType("Astronaut", "astronaut", "&7&oHouston?", Material.GLASS, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, SuitAstronaut.class);
    public final static SuitType DIAMOND = new SuitType("Diamond", "diamond", "&7&oShow your Mining skills\n&7&owith this amazing outfit!", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, SuitDiamond.class);
    public final static SuitType SANTA = new SuitType("Santa", "santa", "&7&oBecome Santa and deliver presents!", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, SuitSanta.class);

    /**
     * The parts materials.
     */
    private Material helmet, chestplate, leggings, boots;

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
        super(Category.SUITS, configName, "ultracosmetics.suits." + permissionSuffix, defaultDesc, h, (byte) 0, clazz);
        this.boots = b;
        this.helmet = h;
        this.chestplate = c;
        this.leggings = l;

        VALUES.add(this);
    }

    /**
     * Equips the Suit to a player.
     *
     * @param player    The receiver of the suit.
     * @param armorSlot The Armor Slot.
     * @return The suit Object equipped to the player.
     */
    public Suit equip(Player player, UltraCosmetics ultraCosmetics, ArmorSlot armorSlot) {
        Suit effect = null;
        try {
            effect = getClazz().getDeclaredConstructor(UUID.class, ArmorSlot.class, UltraCosmetics.class).newInstance(player == null ? null : player.getUniqueId(), armorSlot, ultraCosmetics);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
        return MessageManager.getMessage("Suits." + getConfigName() + "." + armorSlot.toString().toLowerCase() + "-name");
    }

    /**
     * Get the permission required to toggle suit.
     *
     * @return The required permission to toggle the suittype.
     */
    public String getPermission(ArmorSlot armorSlot) {
        return getPermission() + "." + armorSlot.toString().toLowerCase();
    }
}
