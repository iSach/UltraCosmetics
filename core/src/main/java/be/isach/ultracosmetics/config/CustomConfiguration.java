package be.isach.ultracosmetics.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class CustomConfiguration extends YamlConfiguration {
    @Override
    public void addDefault(String path, Object defaultValue) {
        if (!contains(path)) {
            set(path, defaultValue);
        }
    }

    public void addDefault(String path, Object defaultValue, String... comments) {
        if (!contains(path)) {
            set(path, defaultValue, comments);
        }
    }

    public abstract ConfigurationSection createSection(String path, String... comments);
    public abstract void set(String path, Object value, String... comments);
}
