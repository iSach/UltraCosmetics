package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.Version;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;

import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Manages update checking.
 * <p>
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class UpdateManager extends BukkitRunnable {

    /**
     * Current UC version.
     */
    private final String currentVersion;

    private final UltraCosmetics ultraCosmetics;

    /**
     * Whether the plugin is outdated or not.
     */
    private boolean outdated = false;

    /**
     * Last Version published on spigotmc.org.
     */
    private String spigotVersion;

    public UpdateManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.currentVersion = ultraCosmetics.getDescription().getVersion();
    }

    /**
     * Checks for new update.
     */
    @Override
    public void run() {
        spigotVersion = getLastVersion();
        if (spigotVersion != null) {
            if (new Version(currentVersion).compareTo(new Version(spigotVersion)) < 0) {
                outdated = true;
                ultraCosmetics.getSmartLogger().write("New version available on Spigot: " + spigotVersion);
            } else {
                ultraCosmetics.getSmartLogger().write("No new version available.");
            }
        }
    }

    /**
     * Gets last version published on Spigot.
     *
     * @return last version published on Spigot.
     */
    public String getLastVersion() {
        InputStreamReader reader = null;
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/10905/versions?size=1&sort=-id");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "UltraCosmetics Update Checker"); // Sets the user-agent

            InputStream inputStream = connection.getInputStream();
            reader = new InputStreamReader(inputStream);

            JSONArray value = (JSONArray) JSONValue.parseWithException(reader);

            String version = ((JSONObject) value.get(value.size() - 1)).get("name").toString();

            // why are we limiting the length?
            if (version.length() <= 7) {
                return version;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "[UltraCosmetics] Failed to check for an update on spigot.");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }
}
