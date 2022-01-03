package be.isach.ultracosmetics.config;

import java.util.Arrays;

import org.bukkit.configuration.ConfigurationSection;

// to be used on 1.18.1 and above, where Spigot supports comment preservation
public class AutoCommentConfiguration extends CustomConfiguration {

    @Override
    public void set(String path, Object value, String... comments) {
        set(path, value);
        setComments(path, Arrays.asList(comments));
    }

    @Override
    public ConfigurationSection createSection(String path, String... comments) {
        ConfigurationSection section = createSection(path);
        setComments(path, Arrays.asList(comments));
        return section;
    }

}
