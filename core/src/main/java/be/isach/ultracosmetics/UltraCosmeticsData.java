<<<<<<< HEAD
package be.isach.ultracosmetics;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 *
 * Description: This class is only about cleaning a bit main class.
 * here is stored almost all global data.
 */
public class UltraCosmeticsData {
    
    private static UltraCosmeticsData instance;

    public static UltraCosmeticsData get() {
        return instance;
    }

    /**
     * If true, the server is using Spigot and not CraftBukkit/Bukkit.
     */
    private boolean usingSpigot = false;

    /**
     * True -> should execute custom command when going back to main menu.
     */
    private boolean customCommandBackArrow;

    /**
     * Command to execute when going back to Main Menu.
     */
    private String customBackMenuCommand;

    /**
     * Determines if Ammo Use is enabled.
     */
    private boolean ammoEnabled;

    /**
     * Determines if File Storage is enabled.
     */
    private boolean fileStorage = true;

    /**
     * Determines if Treasure Chests are enabled.
     */
    private boolean treasureChests;

    /**
     * Determines if Treasure Chest Money Loot enabled.
     */
    private boolean moneyTreasureLoot;

    /**
     * Determines if Gadget Cooldown should be shown in action bar.
     */
    private boolean cooldownInBar;

    /**
     * Determines if Pet Renaming required Money.
     */
    private boolean petRenameMoney;

    /**
     * Should the GUI close after Cosmetic Selection?
     */
    private boolean closeAfterSelect;

    /**
     * If true, the color will be removed in placeholders.
     */
    private boolean placeHolderColor;

    /**
     * Server NMS version.
     */
    private ServerVersion serverVersion;

    /**
     * NMS Version Manager.
     */
    private VersionManager versionManager;

    private UltraCosmetics ultraCosmetics;

    public UltraCosmeticsData(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.usingSpigot = ultraCosmetics.getServer().getVersion().contains("Spigot");
    }

    public static void init(UltraCosmetics ultraCosmetics) {
        instance = new UltraCosmeticsData(ultraCosmetics);
    }

    /**
     * Check Treasure Chests requirements.
     */
    void checkTreasureChests() {
        moneyTreasureLoot = SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Enabled");
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Enabled")) {
            treasureChests = true;
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Enabled")) {
                Bukkit.getConsoleSender().sendMessage("§c§l-------------------------");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§lTreasure Chests' Money Loot requires Vault!");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§lMoney Loot is turned off!");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§l-------------------------");
                moneyTreasureLoot = false;
            }
        }
    }

    void initModule() {
        ultraCosmetics.getSmartLogger().write("Initializing module " + serverVersion);
        versionManager = new VersionManager(serverVersion);
        try {
            versionManager.load();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            ultraCosmetics.getSmartLogger().write("No module found for " + serverVersion + "! UC Disabling...");
        }
        versionManager.getModule().enable();
        ultraCosmetics.getSmartLogger().write("Module initialized");
    }

    boolean checkServerVersion() {
        String bukkVersion = Bukkit.getVersion();
        if (!bukkVersion.contains("1.8.8") && !bukkVersion.contains("1.9") && !bukkVersion.contains("1.10")) {
            System.out.println("----------------------------\n\nUltraCosmetics requires Spigot 1.8.8 or higher to work!\n\n----------------------------");
            Bukkit.getPluginManager().disablePlugin(ultraCosmetics);
            return false;
        }

        String mcVersion = "1.8.8";

        try {
            mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        ServerVersion serverVersion;

        if (mcVersion.startsWith("v")) {
            try {
                serverVersion = ServerVersion.valueOf(mcVersion);
            } catch (Exception exc) {
                ultraCosmetics.getSmartLogger().write("This NMS version isn't supported. (" + mcVersion + ")!");
                Bukkit.getPluginManager().disablePlugin(ultraCosmetics);
                return true;
            }
        } else serverVersion = ServerVersion.v1_8_R3;

        UltraCosmeticsData.get().setServerVersion(serverVersion);

        return true;
    }

    public void initConfigFields() {
        this.fileStorage = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.System").equalsIgnoreCase("file");
        this.placeHolderColor = SettingsManager.getConfig().getBoolean("Chat-Cosmetic-PlaceHolder-Color");
        this.ammoEnabled = SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Enabled");
        this.cooldownInBar = SettingsManager.getConfig().getBoolean("Categories.Gadgets.Cooldown-In-ActionBar");
        this.customCommandBackArrow = ultraCosmetics.getConfig().getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled");
        this.customBackMenuCommand = ultraCosmetics.getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "");
        this.closeAfterSelect = ultraCosmetics.getConfig().getBoolean("Categories.Close-GUI-After-Select");
    }

    public boolean isAmmoEnabled() {
        return ammoEnabled;
    }

    public boolean shouldCloseAfterSelect() {
        return closeAfterSelect;
    }

    public boolean displaysCooldownInBar() {
        return cooldownInBar;
    }

    public boolean usingCustomCommandBackArrow() {
        return customCommandBackArrow;
    }

    public boolean usingFileStorage() {
        return fileStorage;
    }

    public boolean useMoneyTreasureLoot() {
        return moneyTreasureLoot;
    }

    public boolean isPetRenameMoney() {
        return petRenameMoney;
    }

    public boolean arePlaceholdersColored() {
        return placeHolderColor;
    }

    public boolean areTreasureChestsEnabled() {
        return treasureChests;
    }

    public boolean isUsingSpigot() {
        return usingSpigot;
    }

    public String getCustomBackMenuCommand() {
        return customBackMenuCommand;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    /**
     * Should be only used for running Bukkit Runnables.
     * @return UltraCosmetics instance. (As Plugin)
     */
    public UltraCosmetics getPlugin() {
        return ultraCosmetics;
    }

    public void setServerVersion(ServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }
}
=======
package be.isach.ultracosmetics;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 *
 * Description: This class is only about cleaning a bit main class.
 * here is stored almost all global data.
 */
public class UltraCosmeticsData {
    
    private static UltraCosmeticsData instance;

    public static UltraCosmeticsData get() {
        return instance;
    }

    /**
     * If true, the server is using Spigot and not CraftBukkit/Bukkit.
     */
    private boolean usingSpigot = false;

    /**
     * True -> should execute custom command when going back to main menu.
     */
    private boolean customCommandBackArrow;

    /**
     * Command to execute when going back to Main Menu.
     */
    private String customBackMenuCommand;

    /**
     * Determines if Ammo Use is enabled.
     */
    private boolean ammoEnabled;

    /**
     * Determines if File Storage is enabled.
     */
    private boolean fileStorage = true;

    /**
     * Determines if Treasure Chests are enabled.
     */
    private boolean treasureChests;

    /**
     * Determines if Treasure Chest Money Loot enabled.
     */
    private boolean moneyTreasureLoot;

    /**
     * Determines if Gadget Cooldown should be shown in action bar.
     */
    private boolean cooldownInBar;

    /**
     * Determines if Pet Renaming required Money.
     */
    private boolean petRenameMoney;

    /**
     * Should the GUI close after Cosmetic Selection?
     */
    private boolean closeAfterSelect;

    /**
     * If true, the color will be removed in placeholders.
     */
    private boolean placeHolderColor;

    /**
     * Server NMS version.
     */
    private ServerVersion serverVersion;

    /**
     * NMS Version Manager.
     */
    private VersionManager versionManager;

    private UltraCosmetics ultraCosmetics;

    public UltraCosmeticsData(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.usingSpigot = ultraCosmetics.getServer().getVersion().contains("Spigot");
    }

    public static void init(UltraCosmetics ultraCosmetics) {
        instance = new UltraCosmeticsData(ultraCosmetics);
    }

    /**
     * Check Treasure Chests requirements.
     */
    void checkTreasureChests() {
        moneyTreasureLoot = SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Enabled");
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Enabled")) {
            treasureChests = true;
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Enabled")) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-------------------------");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Treasure Chests' Money Loot requires Vault!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Money Loot is turned off!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-------------------------");
                moneyTreasureLoot = false;
            }
        }
    }

    void initModule() {
        ultraCosmetics.getSmartLogger().write("Initializing module " + serverVersion);
        versionManager = new VersionManager(serverVersion);
        try {
            versionManager.load();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            ultraCosmetics.getSmartLogger().write("No module found for " + serverVersion + "! UC Disabling...");
        }
        versionManager.getModule().enable();
        ultraCosmetics.getSmartLogger().write("Module initialized");
    }

    boolean checkServerVersion() {
        String bukkVersion = Bukkit.getVersion();
        if (!bukkVersion.contains("1.8.8") && !bukkVersion.contains("1.9") && !bukkVersion.contains("1.10") && !bukkVersion.contains("1.11")) {
            System.out.println("----------------------------\n\nUltraCosmetics requires Spigot 1.8.8 or higher to work!\n\n----------------------------");
            Bukkit.getPluginManager().disablePlugin(ultraCosmetics);
            return false;
        }

        String mcVersion = "1.8.8";

        try {
            mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        ServerVersion serverVersion;

        if (mcVersion.startsWith("v")) {
            try {
                serverVersion = ServerVersion.valueOf(mcVersion);
            } catch (Exception exc) {
                ultraCosmetics.getSmartLogger().write("This NMS version isn't supported. (" + mcVersion + ")!");
                Bukkit.getPluginManager().disablePlugin(ultraCosmetics);
                return true;
            }
        } else serverVersion = ServerVersion.v1_8_R3;

        UltraCosmeticsData.get().setServerVersion(serverVersion);

        return true;
    }

    public void initConfigFields() {
        this.fileStorage = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.System").equalsIgnoreCase("file");
        this.placeHolderColor = SettingsManager.getConfig().getBoolean("Chat-Cosmetic-PlaceHolder-Color");
        this.ammoEnabled = SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Enabled");
        this.cooldownInBar = SettingsManager.getConfig().getBoolean("Categories.Gadgets.Cooldown-In-ActionBar");
        this.customCommandBackArrow = ultraCosmetics.getConfig().getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled");
        this.customBackMenuCommand = ultraCosmetics.getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "");
        this.closeAfterSelect = ultraCosmetics.getConfig().getBoolean("Categories.Close-GUI-After-Select");
    }

    public boolean isAmmoEnabled() {
        return ammoEnabled;
    }

    public boolean shouldCloseAfterSelect() {
        return closeAfterSelect;
    }

    public boolean displaysCooldownInBar() {
        return cooldownInBar;
    }

    public boolean usingCustomCommandBackArrow() {
        return customCommandBackArrow;
    }

    public boolean usingFileStorage() {
        return fileStorage;
    }

    public boolean useMoneyTreasureLoot() {
        return moneyTreasureLoot;
    }

    public boolean isPetRenameMoney() {
        return petRenameMoney;
    }

    public boolean arePlaceholdersColored() {
        return placeHolderColor;
    }

    public boolean areTreasureChestsEnabled() {
        return treasureChests;
    }

    public boolean isUsingSpigot() {
        return usingSpigot;
    }

    public String getCustomBackMenuCommand() {
        return customBackMenuCommand;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    /**
     * Should be only used for running Bukkit Runnables.
     * @return UltraCosmetics instance. (As Plugin)
     */
    public UltraCosmetics getPlugin() {
        return ultraCosmetics;
    }

    public void setServerVersion(ServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }
}
>>>>>>> refs/remotes/origin/master
