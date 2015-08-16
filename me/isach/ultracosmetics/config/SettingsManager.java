package me.isach.ultracosmetics.config;

import me.isach.ultracosmetics.Core;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * Created by sacha on 21/07/15.
 */
public class SettingsManager {

    // Config file.
    private static SettingsManager config = new SettingsManager("config");
    // Translation config file.
    private static SettingsManager messages = new SettingsManager("messages");

    private File file;
    private FileConfiguration fileConfiguration;

    /**
     * Creates a new file and defines fileConfiguration and file.
     *
     * @param fileName
     */
    private SettingsManager(String fileName) {

        if (!Core.getPlugin().getDataFolder().exists()) {
            Core.getPlugin().getDataFolder().mkdir();
        }

        File f = new File(Core.getPlugin().getDataFolder(), "/data");
        if(!f.exists())
            f.mkdirs();

        file = new File(Core.getPlugin().getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Gets the messages SettingsManager.
     *
     * @return the messages SettingsManager.
     */
    public static SettingsManager getMessages() {
        return messages;
    }

    /**
     * Gets the messages SettingsManager.
     *
     * @return the messages SettingsManager.
     */
    public static SettingsManager getConfig() {
        return config;
    }

    /**
     * Gets the data settings manager of a player.
     *
     * @param p The player.
     * @return the data settings manager of a player.
     */
    public static SettingsManager getData(Player p) {
        return new SettingsManager("/data/" + p.getUniqueId().toString());
    }

    public void reload() {
        try {
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the value of a given path.
     *
     * @param path
     * @param value
     */
    public void set(String path, Object value) {
        fileConfiguration.set(path, value);
        try {
            fileConfiguration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a value if the fileConfiguration doesn't contain the path.
     *
     * @param path  The fileConfiguration path.
     * @param value The value for this path.
     */
    public void addDefault(String path, Object value) {
        if (!fileConfiguration.contains(path))
            set(path, value);
    }

    /**
     * Create and get a configuration section for a given path.
     *
     * @param path
     * @return the configuration section created for the given path.
     */
    public ConfigurationSection createConfigurationSection(String path) {
        ConfigurationSection cs = fileConfiguration.createSection(path);
        try {
            fileConfiguration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cs;
    }


    @SuppressWarnings("unchecked")
    public <T> T get(String path) {
        return (T) fileConfiguration.get(path);
    }

    /**
     * @param path
     * @return {@code true} if the fileConfiguration contains the path, {@code false} otherwise.
     */
    public boolean contains(String path) {
        return fileConfiguration.contains(path);
    }

}
