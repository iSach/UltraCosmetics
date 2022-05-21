package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.Version;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;

/**
 * Manages update checking.
 * <p>
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class UpdateManager extends BukkitRunnable {

    private static final String RESOURCE_URL = "https://api.spiget.org/v2/resources/10905/";
    private static final String VERSIONS_PREFIX = " Supported versions: ";
    /**
     * Current UC version.
     */
    private final Version currentVersion;

    private final UltraCosmetics ultraCosmetics;

    /**
     * Whether the plugin is outdated or not.
     */
    private boolean outdated = false;

    /**
     * Last Version published on spigotmc.org.
     */
    private Version spigotVersion;

    public UpdateManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.currentVersion = new Version(ultraCosmetics.getDescription().getVersion());
    }

    /**
     * Checks for new update.
     */
    @Override
    public void run() {
        String spigotVersionString = getLastVersion();
        if (spigotVersionString == null) {
            return;
        }
        spigotVersion = new Version(spigotVersionString);
        if (currentVersion.compareTo(spigotVersion) >= 0) {
            ultraCosmetics.getSmartLogger().write("No new version available.");
            return;
        }
        if (!checkMinecraftVersion()) {
            ultraCosmetics.getSmartLogger().write("A new version is available, but it doesn't support this server version.");
            return;
        }

        outdated = true;
        ultraCosmetics.getSmartLogger().write("New version available on Spigot: " + spigotVersion.get());
        if (!SettingsManager.getConfig().getBoolean("Auto-Update")) {
            return;
        }
        if (!download()) {
            ultraCosmetics.getSmartLogger().write("Failed to download update");
            return;
        }
        ultraCosmetics.getSmartLogger().write("Successfully downloaded new version, restart server to apply update.");
    }

    /**
     * Gets last version published on Spigot.
     *
     * @return last version published on Spigot.
     */
    public String getLastVersion() {
        JsonObject jsonVersion = (JsonObject) apiRequest("versions/latest");
        if (jsonVersion == null) {
            return null;
        }
        String version = jsonVersion.get("name").toString();
        return version;
    }

    private JsonElement apiRequest(String suffix) {
        InputStreamReader reader = null;
        try {
            URL url = new URL(RESOURCE_URL + suffix);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "UltraCosmetics Update Checker"); // Sets the user-agent

            InputStream inputStream = connection.getInputStream();
            reader = new InputStreamReader(inputStream);

            // Earlier versions of GSON don't have the static
            // parsing methods present in recent versions.
            @SuppressWarnings("deprecation")
            JsonElement response = new JsonParser().parse(reader);
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "Failed to check for an update on spigot.");
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

    /**
     * Downloads the file
     *
     * Borrowed from https://github.com/Stipess1/AutoUpdater/blob/master/src/main/java/com/stipess1/updater/Updater.java
     */
    private boolean download()
    {
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try {
            URL url = new URL(RESOURCE_URL + "download");
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(new File(Bukkit.getUpdateFolderFile(), "UltraCosmetics-" + spigotVersion.get() + "-RELEASE.jar"));

            final byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                ultraCosmetics.getLogger().log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException e) {
                ultraCosmetics.getLogger().log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
        }
    }

    private boolean checkMinecraftVersion() {
        JsonObject update = (JsonObject) apiRequest("updates/latest");
        // Gets the property "description" of the returned JSON object,
        // base64-decodes it, and stores it in `description`.
        String description = new String(Base64.getDecoder().decode(update.get("description").getAsString()));

        // Basically the way this works, is each update description has a line at the end like this:
        // "Supported versions: 1.8.8, 1.12.2, 1.16.5, 1.17.1, 1.18.2"
        // So we need to parse it and find out if this server's MC version is in this list
        String[] lines = description.split("\\<br\\>");
        String supportedVersionsLine = lines[lines.length - 1];

        if (!supportedVersionsLine.startsWith(VERSIONS_PREFIX)) {
            ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "Skipping update because UC couldn't read supported versions line:");
            ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, supportedVersionsLine);
            return false;
        }
        supportedVersionsLine = supportedVersionsLine.substring(VERSIONS_PREFIX.length());
        String[] supportedVersions = supportedVersionsLine.split(", ");
        // Returns a string like "1.18.2-R0.1-SNAPSHOT"
        String thisMinecraftVersion = Bukkit.getBukkitVersion();
        // Cuts the string to something like "1.18.2"
        thisMinecraftVersion = thisMinecraftVersion.substring(0, thisMinecraftVersion.indexOf('-'));

        for (String supportedVersion : supportedVersions) {
            if (thisMinecraftVersion.equals(supportedVersion)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOutdated() {
        return outdated;
    }
}
