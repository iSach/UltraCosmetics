package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.*;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.XMaterial;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Suit types.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SuitType extends CosmeticMatType<Suit> {

    private static final List<SuitType> ENABLED = new ArrayList<>();
    private static final List<SuitType> VALUES = new ArrayList<>();

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

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    /**
     * The parts materials.
     */
    private final XMaterial helmet, chestplate, leggings, boots;

    /**
     * @param configName       The config path name.
     * @param permissionSuffix The suffix of permission. (ultracosmetic.suits.{suffix}.{part})
     * @param defaultDesc      The default description.
     * @param helmet           The Helmet material.
     * @param chestplate       The Chestplate material.
     * @param leggings         The Leggings material.
     * @param boots            The Boots material.
     * @param clazz            The Suit Class
     */
    private SuitType(String configName, String permissionSuffix, String defaultDesc,
             XMaterial helmet, XMaterial chestplate, XMaterial leggings, XMaterial boots, Class<? extends Suit> clazz) {
        super(Category.SUITS, configName, "ultracosmetics.suits." + permissionSuffix, defaultDesc, helmet, clazz, ServerVersion.earliest());
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;

        VALUES.add(this);
    }

    /**
     * Equips the Suit to a player.
     *
     * @param player    The receiver of the suit.
     * @param armorSlot The Armor Slot.
     * @return The suit Object equipped to the player.
     */
    public Suit equip(UltraPlayer player, UltraCosmetics ultraCosmetics, ArmorSlot armorSlot) {
        Suit suit = null;
        try {
            suit = getClazz().getDeclaredConstructor(UltraPlayer.class, ArmorSlot.class, UltraCosmetics.class).newInstance(player, armorSlot, ultraCosmetics);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        suit.equip(armorSlot);
        return suit;
    }

    /**
     * Get the Helmet material in menu
     *
     * @return The Helmet material in menu
     */
    public XMaterial getHelmet() {
        return helmet;
    }

    /**
     * Get the Chestplate material in menu
     *
     * @return The Chestplate material in menu
     */
    public XMaterial getChestplate() {
        return chestplate;
    }

    /**
     * Get the Leggings material in menu
     *
     * @return The Leggings material in menu
     */
    public XMaterial getLeggings() {
        return leggings;
    }

    /**
     * Get the Boots material in menu
     *
     * @return The Boots material in menu
     */
    public XMaterial getBoots() {
        return boots;
    }

    public XMaterial getMaterial(ArmorSlot armorSlot) {
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

    @Override
    public String getName() {
        return getName(ArmorSlot.CHESTPLATE);
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

    public static void register() {
        new SuitType("Rave", "rave", "&7&oSuch amazing colors!", XMaterial.LEATHER_HELMET, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, SuitRave.class);
        new SuitType("Astronaut", "astronaut", "&7&oHouston?", XMaterial.GLASS, XMaterial.GOLDEN_CHESTPLATE, XMaterial.GOLDEN_LEGGINGS, XMaterial.GOLDEN_BOOTS, SuitAstronaut.class);
        new SuitType("Diamond", "diamond", "&7&oShow your Mining skills\n&7&owith this amazing outfit!", XMaterial.DIAMOND_HELMET, XMaterial.DIAMOND_CHESTPLATE, XMaterial.DIAMOND_LEGGINGS, XMaterial.DIAMOND_BOOTS, SuitDiamond.class);
        new SuitType("Santa", "santa", "&7&oBecome Santa and deliver presents!", XMaterial.LEATHER_HELMET, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, SuitSanta.class);
        new SuitType("Frozen", "frozen", "&7&oThis suit belongs to the\nLord of the Frozen Lands!", XMaterial.PACKED_ICE, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, SuitFrozen.class);
    }
}