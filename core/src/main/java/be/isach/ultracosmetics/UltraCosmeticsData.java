package be.isach.ultracosmetics;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

/**
 * This class is only for cleaning main class a bit.
 *
 * @author iSach
 * @since 08-05-2016
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
	 * If a Vault economy is being used.
	 */
	private boolean usingVaultEconomy;
	
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

	/**
	 * A list of worlds where cosmetics are enabled.
	 */
	private List<String> enabledWorlds;
	
	/**
	 * A String that items that shouldn't be picked up are given. Randomly generated each time the server starts.
	 */
	private final String itemNoPickupString;
	
	public UltraCosmeticsData(UltraCosmetics ultraCosmetics) {
		this.ultraCosmetics = ultraCosmetics;
		this.usingSpigot = ultraCosmetics.getServer().getVersion().contains("Spigot");
		this.itemNoPickupString = UUID.randomUUID().toString();
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
				ultraCosmetics.getSmartLogger().write("-------------------------");
				ultraCosmetics.getSmartLogger().write("Treasure Chests' Money Loot requires Vault!");
				ultraCosmetics.getSmartLogger().write("Money Loot is turned off!");
				ultraCosmetics.getSmartLogger().write("-------------------------");
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
		if (!bukkVersion.contains("1.8")
		    && !bukkVersion.contains("1.9")
		    && !bukkVersion.contains("1.10")
		    && !bukkVersion.contains("1.11")
		    && !bukkVersion.contains("1.12")) {
			System.out.println("----------------------------\n\nULTRACOSMETICS CAN ONLY RUN ON 1.8 OR HIGHER\n\n----------------------------");
			Bukkit.getPluginManager().disablePlugin(ultraCosmetics);
			return false;
		}
		
		String mcVersion = "1.8.0";
		
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
		} else serverVersion = ServerVersion.v1_8_R1;
		
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
		this.enabledWorlds = ultraCosmetics.getConfig().getStringList("Enabled-Worlds");
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
	 *
	 * @return UltraCosmetics instance. (As Plugin)
	 */
	public UltraCosmetics getPlugin() {
		return ultraCosmetics;
	}
	
	public void setServerVersion(ServerVersion serverVersion) {
		this.serverVersion = serverVersion;
	}
	
	public void setUsingVaultEconomy(boolean usingVaultEconomy) {
		this.usingVaultEconomy = usingVaultEconomy;
	}
	
	public boolean isUsingVaultEconomy() {
		return this.usingVaultEconomy;
	}
	
	public final String getItemNoPickupString() {
		return this.itemNoPickupString;
	}

	public List<String> getEnabledWorlds() {
		return this.enabledWorlds;
	}
}
