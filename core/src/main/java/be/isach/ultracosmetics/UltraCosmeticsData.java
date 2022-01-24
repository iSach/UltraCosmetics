package be.isach.ultracosmetics;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * This class is only for cleaning main class a bit.
 *
 * @author iSach
 * @since 08-05-2016
 */
public class UltraCosmeticsData {

    private static UltraCosmeticsData instance;
    /**
     * A String that items that shouldn't be picked up are given. Randomly generated each time the server starts.
     */
    private final String itemNoPickupString;

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

    private boolean cosmeticsProfilesEnabled;

    public UltraCosmeticsData(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.itemNoPickupString = UUID.randomUUID().toString();
    }

    public static UltraCosmeticsData get() {
        return instance;
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
            if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Enabled")) {
                moneyTreasureLoot = true;
            }
        }
    }

    void initModule() {
        ultraCosmetics.getSmartLogger().write("Initializing module " + serverVersion);

        // mappings check is here so it's grouped with other NMS log messages
        // bigger message so server owners might see it
        if (!checkMappingsVersion(serverVersion)) {
            ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!! HEY YOU !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "Server internals seem to have changed since this build was created.");
            ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "Please check for a server update and an UltraCosmetics update.");
            ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "UltraCosmetics will continue running but you will likely experience issues!");
            ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

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
        String versionString = Bukkit.getServer().getClass().getPackage().getName(); 
        String mcVersion;
        try {
            mcVersion = versionString.split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "Unable to determine server version. Please report this error.");
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "Version string: " + versionString);
            return false;
        }

        ServerVersion serverVersion;

        try {
            serverVersion = ServerVersion.valueOf(mcVersion);
        } catch (IllegalArgumentException exc) {
            ultraCosmetics.getSmartLogger().write("This NMS version isn't supported. (" + mcVersion + ")!");
            StringJoiner sj = new StringJoiner(", ");
            for (ServerVersion version : ServerVersion.values()) {
                if (version == ServerVersion.latest()) continue;
                sj.add(version.getName());
            }
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "----------------------------");
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "");
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "ULTRACOSMETICS CAN ONLY RUN ON " + sj.toString() + ", OR " + ServerVersion.latest().getName() + "!");
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "");
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "----------------------------");
            // plugin can't be disabled at load time since it hasn't been enabled yet?
            return false;
        }

        setServerVersion(serverVersion);

        return true;
    }

    boolean checkMappingsVersion(ServerVersion version) {
        String currentMappingsVersion = null;
        @SuppressWarnings("deprecation")
        UnsafeValues magicNumbers = Bukkit.getUnsafe();
        Class<?> magicNumbersClass = magicNumbers.getClass();
        try {
            Method mappingsVersionMethod = magicNumbersClass.getDeclaredMethod("getMappingsVersion");
            currentMappingsVersion = (String) mappingsVersionMethod.invoke(magicNumbers);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException 
                | IllegalArgumentException | InvocationTargetException ignored) {}
        if (currentMappingsVersion == null) {
            return version.getMappingsVersion() == null;
        }
        return currentMappingsVersion.equals(version.getMappingsVersion());
    }

    public void initConfigFields() {
        this.fileStorage = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.System").equalsIgnoreCase("file");
        this.placeHolderColor = SettingsManager.getConfig().getBoolean("Chat-Cosmetic-PlaceHolder-Color");
        this.ammoEnabled = SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Enabled");
        this.cooldownInBar = SettingsManager.getConfig().getBoolean("Categories.Gadgets.Cooldown-In-ActionBar");
        this.customCommandBackArrow = ultraCosmetics.getConfig().getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled");
        this.customBackMenuCommand = ultraCosmetics.getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "");
        this.closeAfterSelect = ultraCosmetics.getConfig().getBoolean("Categories.Close-GUI-After-Select");
        this.cosmeticsProfilesEnabled = ultraCosmetics.getConfig().getBoolean("Auto-Equip-Cosmetics.is-enabled");
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

    public boolean arePlaceholdersColored() {
        return placeHolderColor;
    }

    public boolean areTreasureChestsEnabled() {
        return treasureChests;
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

    public void setServerVersion(ServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }

    /**
     * Should be only used for running Bukkit Runnables.
     *
     * @return UltraCosmetics instance. (As Plugin)
     */
    public UltraCosmetics getPlugin() {
        return ultraCosmetics;
    }

    public final String getItemNoPickupString() {
        return this.itemNoPickupString;
    }

    public boolean areCosmeticsProfilesEnabled() {
        return cosmeticsProfilesEnabled;
    }
}
