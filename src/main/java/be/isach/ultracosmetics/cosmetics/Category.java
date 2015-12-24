package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * Created by Sacha on 11/11/15.
 */
public enum Category {

    PETS("Pets"),
    GADGETS("Gadgets"),
    EFFECTS("Particle-Effects"),
    MOUNTS("Mounts"),
    MORPHS("Morphs"),
    HATS("Hats"),
    SUITS("Suits");

    /**
     * The config path name.
     */
    String configPath;

    /**
     * The ItemStack in Main Menu.
     */
    ItemStack is;

    /**
     * Category of Cosmetic.
     *
     * @param configPath The config path name.
     */
    Category(String configPath) {
        this.configPath = configPath;
        this.is = initMData((String) (SettingsManager.getConfig().get("Categories." + configPath + ".Main-Menu-Item"))).toItemStack(1);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(MessageManager.getMessage("Menu." + configPath));
        is.setItemMeta(itemMeta);
    }

    /**
     * Gets the ItemStack in Main Menu.
     *
     * @return The ItemStack in Main Menu.
     */
    public ItemStack getItemStack() {
        return is;
    }

    /**
     * Checks if the category is enabled.
     *
     * @return {@code true} if enabled, otherwise {@code false}.
     */
    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Categories-Enabled." + configPath);
    }

    /**
     * Checks if the category should have a back arrow in its menu.
     *
     * @return {@code true} if has arrow, otherwise {@code false}
     */
    public boolean hasGoBackArrow() {
        if (!Core.treasureChestsEnabled()
                && Core.enabledCategories.size() == 1)
            return false;
        return (boolean) (SettingsManager.getConfig().get("Categories." + configPath + ".Go-Back-Arrow"));
    }

    /**
     * Creates Material data from a text.
     * {id}:{data}
     *
     * @param name The text as {id}:{data}.
     * @return The material data from the text.
     */
    private MaterialData initMData(String name) {
        return new MaterialData(Integer.parseInt(name.split(":")[0]),
                (name.split(":").length > 1 ? (byte) Integer.parseInt(name.split(":")[1]) : (byte) 0));
    }

}
