package be.isach.ultracosmetics.cosmetics;

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
    EFFECTS("Particle-Effects"),
    GADGETS("Gadgets"),
    MOUNTS("Mounts"),
    MORPHS("Morphs"),
    HATS("Hats");

    String configPath;
    ItemStack is;

    Category(String configPath) {
        this.configPath = configPath;
        this.is = initMData((String) (SettingsManager.getConfig().get("Categories." + configPath + ".Main-Menu-Item"))).toItemStack(1);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(MessageManager.getMessage("Menu." + configPath));
        is.setItemMeta(itemMeta);
    }

    public ItemStack getItemStack() {
        return is;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Categories-Enabled." + configPath);
    }

    public boolean hasGoBackArrow() {
        return (boolean) (SettingsManager.getConfig().get("Categories." + configPath + ".Go-Back-Arrow"));
    }

    private MaterialData initMData(String name) {
        return new MaterialData(Integer.parseInt(name.split(":")[0]),
                (name.split(":").length > 1 ? (byte) Integer.parseInt(name.split(":")[1]) : (byte) 0));
    }

}
