package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.config.SettingsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sachalewin on 5/07/16.
 */
public abstract class CosmeticType {

    private final static List<CosmeticType> ENABLED = new ArrayList<>();
    private final static List<CosmeticType> VALUES = new ArrayList<>();

    public static List<? extends CosmeticType> enabled() {
        return ENABLED;
    }

    public static List<? extends CosmeticType> values() {
        return VALUES;
    }

    public static CosmeticType valueOf(String s) {
        for(CosmeticType cosmeticType : VALUES) {
            if(cosmeticType.getConfigName().equalsIgnoreCase(s)) return cosmeticType;
        }
        return null;
    }

    public static void checkEnabled() {
        for(CosmeticType cosmeticType : values()) {
            if(cosmeticType.isEnabled()) {
                ENABLED.add(cosmeticType);
            }
        }
    }

    private String configName;
    private String permission;
    private String descriptionAsString;
    private Class clazz;
    private Category category;

    public CosmeticType(Category category, String configName, String permissionSuffix, String description, Class clazz) {
        this.configName = configName;
        this.permission = permissionSuffix;
        this.descriptionAsString = description;
        this.clazz = clazz;
        this.category = category;

        VALUES.add(this);

        if (SettingsManager.getConfig().get(getCategory().getConfigPath() + "." + configName + ".Description") == null) {
            setDescriptionAsString(description);
            SettingsManager.getConfig().set(getCategory().getConfigPath() + "." + configName + ".Description", getDescriptionColored(), "Description of this cosmetic.");
        } else
            setDescriptionAsString(fromList((List<String>) SettingsManager.getConfig().get(category.getConfigPath() + "." + configName + ".Description")));
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + configName + ".Enabled");
    }

    public String getName() {
        return SettingsManager.getConfig().getString(category.getConfigPath() + "." + configName + ".name");
    }

    public String getConfigName() {
        return configName;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescriptionAsString() {
        return descriptionAsString;
    }

    public Class getClazz() {
        return clazz;
    }

    public Category getCategory() {
        return category;
    }

    public void setDescriptionAsString(String descriptionAsString) {
        this.descriptionAsString = descriptionAsString;
    }

    /**
     * Transforms the description from a String to a list.
     * Without colors.
     *
     * @return The description as a list.
     */
    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : getDescriptionAsString().split("\n"))
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
        return Arrays.asList(getDescriptionAsString().split("\n"));
    }

    /**
     * Check if the Suittype should show a description.
     *
     * @return {@code true} if it should show a description, otherwise {@code false}.
     */
    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + getConfigName() + ".Show-Description");
    }

    /**
     * Check if the Suittype can be found in Treasure Chests.
     *
     * @return {@code true} if it can be found in treasure chests, otherwise {@code false}.
     */
    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
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
            stringBuilder.append(description.get(i)).append(i < description.size() - 1 ? "\n" : "");
        return stringBuilder.toString();
    }

}
