package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.Version;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Predicate;

/**
 * Manages update checking.
 * <p>
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class UpdateManager extends Thread {

    /**
     * Current UC version.
     */
    private String currentVersion;

    /**
     * Whether the plugin is outdated or not.
     */
    private boolean outdated;

    /**
     * Last Version published on spigotmc.org.
     */
    private String lastVersion;

    public UltraCosmetics ultraCosmetics;

    public UpdateManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.currentVersion = ultraCosmetics.getDescription().getVersion()
                .replace("Beta ", "")
                .replace("Pre-", "")
                .replace("Release ", "")
                .replace("Hype Update (", "")
                .replace(")", "");
    }

    /**
     * Checks for new update.
     */
    public void checkForUpdate() {
        lastVersion = getLastVersion();
        if (lastVersion != null) {
            int i = new Version(currentVersion).compareTo(new Version(lastVersion));
            outdated = i == -1;
            if (lastVersion.equalsIgnoreCase("1.7.1") && currentVersion.startsWith("1.1"))
                outdated = false;
        } else
            outdated = false;


        if (outdated) {
            Bukkit.getOnlinePlayers().stream().filter((Predicate<Player>) ServerOperator::isOp).forEachOrdered(p -> p.sendMessage(ChatColor.BOLD + "" + ChatColor.ITALIC + "UltraCosmetics >" + ChatColor.RED + "" + ChatColor.BOLD + "An update is available: " + lastVersion));
        }
    }

    /**
     * Gets last version published on Spigot.
     *
     * @return last version published on Spigot.
     */
    public synchronized String getLastVersion() {
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/10905/versions?size=1&sort=-id");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "UltraCosmetics Update Checker"); // Sets the user-agent

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JSONArray value = (JSONArray) JSONValue.parseWithException(reader);

            String version = ((JSONObject) value.get(value.size() - 1)).get("name").toString();

            if (version.length() <= 7) {
                return version;
            }
        } catch (Exception ex) {
            System.out.print("[UltraCosmetics] Failed to check for an update on spigot. ");
        }
        return null;
    }

    public synchronized boolean isOutdated() {
        return outdated;
    }

    public synchronized String getCurrentVersion() {
        return currentVersion;
    }
}
