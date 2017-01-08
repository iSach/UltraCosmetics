<<<<<<< HEAD
package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Predicate;

/**
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
            Bukkit.getOnlinePlayers().stream().filter((Predicate<Player>) ServerOperator::isOp).forEachOrdered(p -> p.sendMessage("§l§oUltraCosmetics > §c§lAn update is available: " + lastVersion));
        }
    }

    /**
     * Gets last version published on Spigot.
     *
     * @return last version published on Spigot.
     */
    public synchronized String getLastVersion() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setConnectTimeout(2000);
            con.setRequestMethod("POST");
            con.getOutputStream().write(
                    ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=10905").getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine().replace("Beta ", "").replace("Pre-", "").replace("Release ", "").replace("Hype Update (", "").replace(")", "");
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
=======
package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Predicate;

/**
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
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setConnectTimeout(2000);
            con.setRequestMethod("POST");
            con.getOutputStream().write(
                    ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=10905").getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine().replace("Beta ", "").replace("Pre-", "").replace("Release ", "").replace("Hype Update (", "").replace(")", "");
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
>>>>>>> refs/remotes/origin/master
