package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ServerVersion;
import com.cryptomorin.xseries.XMaterial;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * A cosmetic type.
 *
 * @author iSach
 * @since 07-05-2016
 */
public abstract class CosmeticType<T extends Cosmetic<?>> {

    private final String configName;
    private final String permission;
    private final String description;
    private final Class<? extends T> clazz;
    private final Category category;
    private final ServerVersion baseVersion;
    private final XMaterial material;

    public CosmeticType(Category category, String configName, String permission, String defaultDescription, XMaterial material, Class<? extends T> clazz, ServerVersion baseVersion) {
        this.configName = configName;
        this.permission = permission;
        this.clazz = clazz;
        this.category = category;
        this.baseVersion = baseVersion;
        this.material = material;

        String descPath = getCategory().getConfigPath() + "." + configName + ".Description";
        if (SettingsManager.getConfig().getStringList(descPath).size() > 0) {
            StringJoiner builder = new StringJoiner("\n");
            SettingsManager.getConfig().getStringList(descPath).forEach(k -> builder.add(k));
            MessageManager.addMessage(descPath, builder.toString());
            SettingsManager.getConfig().set(descPath, null);
        } else {
            MessageManager.addMessage(descPath, defaultDescription);
        }
        description = MessageManager.getMessage(descPath);
    }

    public T equip(UltraPlayer player, UltraCosmetics ultraCosmetics) {
        T cosmetic = null;
        try {
            cosmetic = getClazz().getDeclaredConstructor(UltraPlayer.class, UltraCosmetics.class).newInstance(player, ultraCosmetics);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
        cosmetic.equip();
        return cosmetic;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + configName + ".Enabled") && UltraCosmeticsData.get().getServerVersion().isAtLeast(baseVersion);
    }

    public String getName() {
        return MessageManager.getMessage(category.getConfigPath() + "." + configName + ".name");
    }

    public String getConfigName() {
        return configName;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescriptionAsString() {
        return description;
    }

    public Class<? extends T> getClazz() {
        return clazz;
    }

    public Category getCategory() {
        return category;
    }

    public XMaterial getMaterial() {
        return material;
    }

    public ItemStack getItemStack() {
        return material.parseItem();
    }

    public String getConfigPath() {
        return getCategory().getConfigPath() + "." + getConfigName();
    }

    /**
     * Transforms the description from a String to a list.
     * Without colors.
     *
     * @return The description as a list.
     */
    public List<String> getDescription() {
        return Arrays.asList(ChatColor.translateAlternateColorCodes('&', getDescriptionAsString()).split("\n"));
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
     * Check if the cosmetic should show a description.
     *
     * @return {@code true} if it should show a description, otherwise {@code false}.
     */
    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + getConfigName() + ".Show-Description");
    }

    /**
     * Check if the cosmetic can be found in Treasure Chests.
     *
     * @return {@code true} if it can be found in treasure chests, otherwise {@code false}.
     */
    public boolean canBeFound() {
        return getChestWeight() > 0;
    }

    /**
     * Gets the weight of getting this cosmetic in its category.
     * The absolute chance of getting this cosmetic is also affected by the category weight.
     * 
     * @return its weight
     */
    public int getChestWeight() {
        return SettingsManager.getConfig().getInt(category.getConfigPath() + "." + getConfigName() + ".Treasure-Chest-Weight");
    }

    /**
     * Override toString method to show Cosmetic name.
     *
     * @return
     */
    @Override
    public String toString() {
        return getConfigName().toUpperCase();
    }
}
