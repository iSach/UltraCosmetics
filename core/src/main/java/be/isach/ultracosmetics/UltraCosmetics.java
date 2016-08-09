package be.isach.ultracosmetics;

import be.isach.ultracosmetics.command.CommandManager;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.listeners.PlayerListener;
import be.isach.ultracosmetics.listeners.v1_9.PlayerSwapItemListener;
import be.isach.ultracosmetics.log.SmartLogger;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.mysql.MySqlConnectionManager;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.run.InvalidWorldChecker;
import be.isach.ultracosmetics.util.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Package: be.isach.ultracosmetics
 * Created by: sacha
 * Date: 03/08/15
 * Project: UltraCosmetics
 *
 * Description: Main Class of the plugin.
 */
public class UltraCosmetics extends JavaPlugin {

    /**
     * Manages sub commands.
     */
    private CommandManager commandManager;

    /**
     * The Configuration. (config.yml)
     */
    private CustomConfiguration config;

    /**
     * Config File.
     */
    private File file;

    /**
     * Economy, used only if Vault is enabled.
     */
    private Economy economy = null;

    /**
     * Player Manager instance.
     */
    private UltraPlayerManager playerManager;

    /**
     * Smart Logger Instance.
     */
    private SmartLogger smartLogger;

    /**
     * MySql Manager.
     */
    private MySqlConnectionManager mySqlConnectionManager;

    /**
     * Called when plugin is enabled.
     */
    @Override
    public void onEnable() {
        this.smartLogger = new SmartLogger();

        if (!UltraCosmeticsData.get().checkServerVersion()) {
            return;
        }

        // Create UltraPlayer Manager.
        this.playerManager = new UltraPlayerManager(this);

        // Beginning of boot log. basic informations.
        getSmartLogger().write("[------------------------------------------]");
        getSmartLogger().write("UltraCosmetics v" + getDescription().getVersion() + " is loading... (server: " + UltraCosmeticsData.get().getServerVersion().getName() + ")");
        getSmartLogger().write("Thanks for having downloaded it!");
        getSmartLogger().write("Plugin by iSach.");
        getSmartLogger().write("Link: http://bit.ly/UltraCosmetics");

        // Set up config.
        setUpConfig();

        // Initialize NMS Module
        UltraCosmeticsData.get().initModule();

        // Set up Metrics.
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            System.out.println("Couldn't send data to Metrics :(");
        }

        // Init Message manager.
        new MessageManager();

        // Register Listeners.
        registerListeners();

        getSmartLogger().write("");
        getSmartLogger().write("Registering commands...");
        // Register the command

        commandManager = new CommandManager(this);
        commandManager.registerCommands();

        // Set up Cosmetics config.
        new CosmeticManager(this).setupCosmeticsConfigs();

        if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            getSmartLogger().write("");
            getSmartLogger().write("Morphs require Lib's Disguises!");
            getSmartLogger().write("");
            getSmartLogger().write("Morphs disabled.");
            getSmartLogger().write("");
        }

        // Set up economy if needed.
        setupEconomy();

        if (!UltraCosmeticsData.get().usingFileStorage()) {
            getSmartLogger().write("");
            getSmartLogger().write("Connecting to MySQL database...");

            // Start MySQL.
            this.mySqlConnectionManager = new MySqlConnectionManager(this);
            mySqlConnectionManager.start();

            getSmartLogger().write("Connected to MySQL database.");
            getSmartLogger().write("");
        }

        // Initialize UltraPlayers and give chest (if needed).
        playerManager.initPlayers();

        // Start the Fall Damage and Invalid World Check Runnables.
        new FallDamageManager().runTaskTimerAsynchronously(this, 0, 1);
        new InvalidWorldChecker(this).runTaskTimerAsynchronously(this, 0, 5);


        // TODO Register Menus.
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (SettingsManager.getConfig().getBoolean("Check-For-Updates")) {
            UpdateManager updateChecker = new UpdateManager(this);
            updateChecker.start();
            updateChecker.checkForUpdate();
        }

        // Ended well :v
        getSmartLogger().write("UltraCosmetics successfully finished loading and is now enabled!");
        getSmartLogger().write("[------------------------------------------]");
    }

    /**
     * Called when plugin disables.
     */
    @Override
    public void onDisable() {
        // TODO Purge Pet Names. (and Treasure Chests bugged holograms).
        // TODO Use Metadatas for that!

        if (playerManager != null) {
            playerManager.dispose();
        }

        UltraCosmeticsData.get().getVersionManager().getModule().disable();

        BlockUtils.forceRestore();
    }

    /**
     * Registers Listeners.
     */
    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new PlayerListener(), this);

        if (UltraCosmeticsData.get().getServerVersion().compareTo(ServerVersion.v1_9_R1) >= 0) {
            pluginManager.registerEvents(new PlayerSwapItemListener(), this);
        }
    }

    /**
     * Sets Vault up.
     */
    private void setupEconomy() {
        if (!((UltraCosmeticsData.get().isAmmoEnabled()
                || (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")
                && SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled"))
                || (UltraCosmeticsData.get().areTreasureChestsEnabled()
                && SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Enabled")))
                && Bukkit.getPluginManager().isPluginEnabled("Vault"))) {
            return;
        }

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
            economy = economyProvider.getProvider();

//        vaultLoaded = economy != null;
    }

    private void setUpConfig() {
        file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            FileUtils.copy(getResource("config.yml"), file);
            getSmartLogger().write("Config file doesn't exist yet.");
            getSmartLogger().write("Creating Config File and loading it.");
        }

        config = CustomConfiguration.loadConfiguration(file);

        List<String> enabledWorlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        config.addDefault("Enabled-Worlds", enabledWorlds, "List of the worlds", "where cosmetics are enabled!");

        config.set("Disabled-Items", null);

        if (!config.contains("TreasureChests.Loots.Gadgets")) {
            config.createSection("TreasureChests.Loots.Gadgets", "Chance of getting a GADGET", "This is different from ammo!");
            config.set("TreasureChests.Loots.Gadgets.Enabled", true);
            config.set("TreasureChests.Loots.Gadgets.Chance", 20);
            config.set("TreasureChests.Loots.Gadgets.Message.enabled", false);
            config.set("TreasureChests.Loots.Gadgets.Message.message", "%prefix% &6&l%name% found gadget %gadget%");
        }
        if (!config.contains("TreasureChests.Loots.Suits")) {
            config.createSection("TreasureChests.Loots.Suits");
            config.set("TreasureChests.Loots.Suits.Enabled", true);
            config.set("TreasureChests.Loots.Suits.Chance", 10);
            config.set("TreasureChests.Loots.Suits.Message.enabled", false);
            config.set("TreasureChests.Loots.Suits.Message.message", "%prefix% &6&l%name% found suit part: %suitw%");
        }

        if (!config.contains("Categories.Suits")) {
            config.createSection("Categories.Suits");
            config.set("Categories.Suits.Main-Menu-Item", "299:0");
            config.set("Categories.Suits.Go-Back-Arrow", true);
        }

        config.addDefault("Categories.Clear-Cosmetic-Item", "152:0", "Item where user click to clear a cosmetic.");
        config.addDefault("Categories.Previous-Page-Item", "368:0", "Previous Page Item");
        config.addDefault("Categories.Next-Page-Item", "381:0", "Next Page Item");
        config.addDefault("Categories.Back-Main-Menu-Item", "262:0", "Back to Main Menu Item");
        config.addDefault("Categories.Self-View-Item.When-Enabled", "381:0", "Item in Morphs Menu when Self View enabled.");
        config.addDefault("Categories.Self-View-Item.When-Disabled", "368:0", "Item in Morphs Menu when Self View disabled.");
        config.addDefault("Categories.Gadgets-Item.When-Enabled", "351:10", "Item in Gadgets Menu when Gadgets enabled.");
        config.addDefault("Categories.Gadgets-Item.When-Disabled", "351:8", "Item in Gadgets Menu when Gadgets disabled.");
        config.addDefault("Categories.Rename-Pet-Item", "421:0", "Item in Pets Menu to rename current pet.");
        config.addDefault("Categories.Close-GUI-After-Select", true, "Should GUI close after selecting a cosmetic?");
        config.addDefault("No-Permission.Custom-Item.Lore", Arrays.asList("", "&c&lYou do not have permission for this!", ""));
        config.addDefault("Categories.Back-To-Main-Menu-Custom-Command.Enabled", false);
        config.addDefault("Categories.Back-To-Main-Menu-Custom-Command.Command", "cc open custommenu.yml {player}");

        config.addDefault("Categories-Enabled.Suits", true, "Do you want to enable Suits category?");

        config.addDefault("Categories.Gadgets.Cooldown-In-ActionBar", true, "You wanna show the cooldown of", "current gadget in action bar?");

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Custom Player Manager.
     *
     * @return the Custom Player Manager.
     */
    public UltraPlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * @return Command Manager.
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return Overwrites getFile to return our own File.
     */
    @Override
    public File getFile() {
        return file;
    }

    /**
     * @return Overwrites getConfig to return our own Custom Configuration.
     */
    @Override
    public CustomConfiguration getConfig() {
        return config;
    }

    /**
     * @return Smart Logger Instance.
     */
    public SmartLogger getSmartLogger() {
        return smartLogger;
    }

    /**
     * @return Vault Economy.
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * @return MySql Manager.
     */
    public MySqlConnectionManager getMySqlConnectionManager() {
        return mySqlConnectionManager;
    }
}
