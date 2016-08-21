package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics
 * Created by: Sacha
 * Created on: 20th June, 2016
 * at 22:35
 */
public enum Category {

    PETS("Pets", "Spawn", "Despawn", "Clear-Pet"),
    GADGETS("Gadgets", "Activate", "Deactivate", "Clear-Gadget"),
    EFFECTS("Particle-Effects", "Summon", "Unsummon", "Clear-Effect"),
    MOUNTS("Mounts", "Spawn", "Despawn", "Clear-Mount"),
    MORPHS("Morphs", "Morph", "Unmorph", "Clear-Morph"),
    HATS("Hats", "Equip", "Unequip", "Clear-Hat"),
    SUITS("Suits", "Equip", "Unequip", "Clear-Suit"),
    EMOTES("Emotes", "Equip", "Unequip", "Clear-Emote");

    public static int enabledSize() {
        final int[] i = {0};
        Arrays.stream(values()).filter(Category::isEnabled).forEach(category -> i[0]++);
        return i[0];
    }

    /**
     * The config path name.
     */
    private String configPath;

    /**
     * The ItemStack in Main Menu.
     */
    private ItemStack is;

    /**
     * Message on menu to activate a cosmetic of this category.
     */
    private String activateMenu;

    /**
     * Message on menu to deactivate a cosmetic of this category.
     */
    private String deactivateMenu;

    /**
     * Path of the clear message.
     */
    private String clearConfigPath;

    /**
     * Category of Cosmetic.
     *  @param configPath The config path name.
     * @param activateMenu Message on menu to activate a cosmetic of this category.
     * @param deactivateMenu Message on menu to deactivate a cosmetic of this category.
     * @param clearConfigPath
     */
    Category(String configPath, String activateMenu, String deactivateMenu, String clearConfigPath) {
        this.configPath = configPath;
        this.activateMenu = activateMenu;
        this.deactivateMenu = deactivateMenu;
        this.clearConfigPath = clearConfigPath;
        if (SettingsManager.getConfig().contains("Categories." + configPath + ".Main-Menu-Item")) {
            this.is = initMaterialData((String) (SettingsManager.getConfig().get("Categories." + configPath + ".Main-Menu-Item"))).toItemStack(1);
        } else {
            this.is = ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA" +
                    "6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA1OWQ1OWViNGU1OWM" +
                    "zMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzE" +
                    "wIn19fQ==", "§8§o");
        }
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
        return !(this == MORPHS && !Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
                && SettingsManager.getConfig().getBoolean("Categories-Enabled." + configPath);
    }

    /**
     * Checks if the category should have a back arrow in its menu.
     *
     * @return {@code true} if has arrow, otherwise {@code false}
     */
    public boolean hasGoBackArrow() {
        return !(!UltraCosmeticsData.get().areTreasureChestsEnabled()
                && enabledSize() == 1)
                && (boolean) (SettingsManager.getConfig().get("Categories." + configPath + ".Go-Back-Arrow"));
    }

    /**
     * Creates Material data from a text.
     * {id}:{data}
     *
     * @param name The text as {id}:{data}.
     * @return The material data from the text.
     */
    private MaterialData initMaterialData(String name) {
        return new MaterialData(Integer.parseInt(name.split(":")[0]),
                (name.split(":").length > 1 ? (byte) Integer.parseInt(name.split(":")[1]) : (byte) 0));
    }

    /**
     * @return Config Path.
     */
    public String getConfigPath() {
        return configPath;
    }

    public String getActivateMenu() {
        return MessageManager.getMessage("Menu." + activateMenu);
    }

    public String getClearConfigPath() {
        return clearConfigPath;
    }

    public String getDeactivateMenu() {
        return MessageManager.getMessage("Menu." + deactivateMenu);
    }
}
