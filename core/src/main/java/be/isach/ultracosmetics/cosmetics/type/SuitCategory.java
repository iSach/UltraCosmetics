package be.isach.ultracosmetics.cosmetics.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.cosmetics.suits.SuitAstronaut;
import be.isach.ultracosmetics.cosmetics.suits.SuitDiamond;
import be.isach.ultracosmetics.cosmetics.suits.SuitFrozen;
import be.isach.ultracosmetics.cosmetics.suits.SuitRave;
import be.isach.ultracosmetics.cosmetics.suits.SuitSanta;
import be.isach.ultracosmetics.util.XMaterial;

public enum SuitCategory {
    RAVE("Rave", "rave", "&7&oSuch amazing colors!", XMaterial.LEATHER_HELMET, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, SuitRave.class),
    ASTRONAUT("Astronaut", "astronaut", "&7&oHouston?", XMaterial.GLASS, XMaterial.GOLDEN_CHESTPLATE, XMaterial.GOLDEN_LEGGINGS, XMaterial.GOLDEN_BOOTS, SuitAstronaut.class),
    DIAMOND("Diamond", "diamond", "&7&oShow your Mining skills\n&7&owith this amazing outfit!", XMaterial.DIAMOND_HELMET, XMaterial.DIAMOND_CHESTPLATE, XMaterial.DIAMOND_LEGGINGS, XMaterial.DIAMOND_BOOTS, SuitDiamond.class),
    SANTA("Santa", "santa", "&7&oBecome Santa and deliver presents!", XMaterial.LEATHER_HELMET, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, SuitSanta.class),
    FROZEN("Frozen", "frozen", "&7&oThis suit belongs to the\nLord of the Frozen Lands!", XMaterial.PACKED_ICE, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, SuitFrozen.class),
    ;
    private final String configName;
    private final String permissionSuffix;
    private final String defaultDesc;
    private final SuitType helmet;
    private final SuitType chestplate;
    private final SuitType leggings;
    private final SuitType boots;
    private final Class<? extends Suit> clazz;
    private SuitCategory(String configName, String permissionSuffix, String defaultDesc,
         XMaterial helmet, XMaterial chestplate, XMaterial leggings, XMaterial boots, Class<? extends Suit> clazz) {
        this.configName = configName;
        this.permissionSuffix = permissionSuffix;
        this.defaultDesc = defaultDesc;
        this.clazz = clazz;

        // suit parts must be initialized last
        this.helmet = new SuitType(helmet, ArmorSlot.HELMET, this);
        this.chestplate = new SuitType(chestplate, ArmorSlot.CHESTPLATE, this);
        this.leggings = new SuitType(leggings, ArmorSlot.LEGGINGS, this);
        this.boots = new SuitType(boots, ArmorSlot.BOOTS, this);
    }

    public String getConfigName() {
        return configName;
    }

    public String getPermissionSuffix() {
        return permissionSuffix;
    }

    public String getDefaultDesc() {
        return defaultDesc;
    }

    public Class<? extends Suit> getSuitClass() {
        return clazz;
    }

    public SuitType getHelmet() {
        return helmet;
    }

    public SuitType getChestplate() {
        return chestplate;
    }

    public SuitType getLeggings() {
        return leggings;
    }

    public SuitType getBoots() {
        return boots;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Suits." + configName + ".Enabled");
    }

    public SuitType getPiece(ArmorSlot slot) {
        switch (slot) {
        case HELMET:
            return getHelmet();
        case CHESTPLATE:
        default:
            return getChestplate();
        case LEGGINGS:
            return getLeggings();
        case BOOTS:
            return getBoots();
        }
    }

    public List<SuitType> getPieces() {
        return Arrays.asList(getHelmet(), getChestplate(), getLeggings(), getBoots());
    }

    public static List<SuitCategory> enabled() {
        List<SuitCategory> enabled = new ArrayList<>();
        for (SuitCategory cat : values()) {
            enabled.add(cat);
        }
        return enabled;
    }
}
