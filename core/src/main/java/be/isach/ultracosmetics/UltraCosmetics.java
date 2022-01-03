package be.isach.ultracosmetics;

import be.isach.ultracosmetics.command.CommandManager;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.economy.EconomyHandler;
import be.isach.ultracosmetics.listeners.MainListener;
import be.isach.ultracosmetics.listeners.PlayerListener;
import be.isach.ultracosmetics.listeners.v1_9.PlayerSwapItemListener;
import be.isach.ultracosmetics.log.SmartLogger;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.manager.ArmorStandManager;
import be.isach.ultracosmetics.manager.TreasureChestManager;
import be.isach.ultracosmetics.menu.Menus;
import be.isach.ultracosmetics.mysql.MySqlConnectionManager;
import be.isach.ultracosmetics.placeholderapi.PlaceholderHook;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.player.profile.CosmeticsProfile;
import be.isach.ultracosmetics.player.profile.CosmeticsProfileManager;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.run.InvalidWorldChecker;
import be.isach.ultracosmetics.run.MovingChecker;
import be.isach.ultracosmetics.util.*;
import be.isach.ultracosmetics.version.AFlagManager;
import be.isach.ultracosmetics.version.VersionManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class of the plugin.
 *
 * @author iSach
 * @since 08-03-2015
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
     * Update Manager.
     */
    private UpdateManager updateChecker;

    /**
     * Treasure Chests Manager;
     */
    private TreasureChestManager treasureChestManager;

    /**
     * Menus.
     */
    private Menus menus;

    /**
     * Manages armor stands.
     */
    private ArmorStandManager armorStandManager;

    private EconomyHandler economyHandler;

    /**
     * Manages cosmetics profiles.
     */
    private CosmeticsProfileManager cosmeticsProfileManager;
    
    /**
     * Manages WorldGuard flags.
     */
    private AFlagManager flagManager = null;
    
    /**
     * Called when plugin is loaded.
     * Used for registering WorldGuard flags
     * as recommended in API documentation.
     */
    @Override
    public void onLoad() {
        // moved to onLoad so it's ready for WorldGuard support
        this.smartLogger = new SmartLogger(getLogger());

        UltraCosmeticsData.init(this);

        if (!UltraCosmeticsData.get().checkServerVersion()) {
            return;
        }
        
        // Not using isPluginEnabled() because WorldGuard should be
        // loaded but not yet enabled when registering flags
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            // does reflect-y things but isn't in VersionManager because of the load timing
            // and because it should only happen if WorldGuard is present
            String wgVersionPackage = VersionManager.IS_VERSION_1_13 ? "v1_13_R2" : "v1_12_R1";
            try {
                flagManager = (AFlagManager) ReflectionUtils.instantiateObject(Class.forName(VersionManager.PACKAGE + "." + wgVersionPackage + ".worldguard.FlagManager"));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoClassDefFoundError | NoSuchMethodError | NoSuchMethodException | ClassNotFoundException e) {
                getSmartLogger().write(LogLevel.WARNING, "Couldn't find required classes for WorldGuard integration.");
                getSmartLogger().write(LogLevel.WARNING, "Please make sure you are using the latest version of WorldGuard");
                getSmartLogger().write(LogLevel.WARNING, "for your version of Minecraft. Debug info:");
                e.printStackTrace();
                getSmartLogger().write("WorldGuard support is disabled.");
            }
        }
    }

    /**
     * Called when plugin is enabled.
     */
    @Override
    public void onEnable() {
        // if loading failed...
        if (UltraCosmeticsData.get().getServerVersion() == null) {
            getSmartLogger().write(LogLevel.ERROR, "Plugin load has failed, please check earlier in the log for details.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // Create UltraPlayer Manager.
        this.playerManager = new UltraPlayerManager(this);

        this.armorStandManager = new ArmorStandManager(this);

        // Beginning of boot log. basic informations.
        getSmartLogger().write("-------------------------------------------------------------------");
        getSmartLogger().write("UltraCosmetics v" + getDescription().getVersion() + " is loading... (server: " + UltraCosmeticsData.get().getServerVersion().getName() + ")");
        getSmartLogger().write("Thanks for downloading it!");
        getSmartLogger().write("Plugin by iSach.");
        getSmartLogger().write("Link: http://bit.ly/UltraCosmetics");

        // Set up config.
        setUpConfig();

        // Initialize NMS Module
        UltraCosmeticsData.get().initModule();

        // Set up bStats.
        // this.metrics = new Metrics(this, getSmartLogger());

        // Init Message manager.
        new MessageManager();

        // reward.yml & design.yml
        new TreasureManager(this);

        // Register Listeners.
        registerListeners();
        // Register the command

        commandManager = new CommandManager(this);
        commandManager.registerCommands(this);

        UltraCosmeticsData.get().initConfigFields();

        // Set up Cosmetics config.
        new CosmeticManager(this).setupCosmeticsConfigs();

        if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            getSmartLogger().write();
            getSmartLogger().write("Morphs require Lib's Disguises!");
            getSmartLogger().write();
            getSmartLogger().write("Morphs disabled.");
            getSmartLogger().write();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getSmartLogger().write();
            new PlaceholderHook(this).register();
            getSmartLogger().write("Hooked into PlaceholderAPI");
            getSmartLogger().write();
        }

        if (flagManager != null) {
            flagManager.registerPhase2();
            getSmartLogger().write();
            getSmartLogger().write("WorldGuard custom flags enabled");
            getSmartLogger().write();
        }

        // Set up economy if needed.
        setupEconomy();

        if (!UltraCosmeticsData.get().usingFileStorage()) {
            getSmartLogger().write();
            getSmartLogger().write("Connecting to MySQL database...");

            // Start MySQL.
            this.mySqlConnectionManager = new MySqlConnectionManager(this);
            mySqlConnectionManager.start();

            getSmartLogger().write("Connected to MySQL database.");
            getSmartLogger().write();
        }

        // Initialize UltraPlayers and give chest (if needed).

        playerManager.initPlayers();

        // Start the Fall Damage and Invalid World Check Runnables.

        new FallDamageManager().runTaskTimerAsynchronously(this, 0, 1);
        new InvalidWorldChecker(this).runTaskTimerAsynchronously(this, 0, 5);
        new MovingChecker(this).runTaskTimerAsynchronously(this, 0, 1);

        this.menus = new Menus(this);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (SettingsManager.getConfig().getBoolean("Check-For-Updates")) {
            this.updateChecker = new UpdateManager(this);
            updateChecker.start();
            updateChecker.checkForUpdate();
        }

        if (UltraCosmeticsData.get().areCosmeticsProfilesEnabled()) {
            this.cosmeticsProfileManager = new CosmeticsProfileManager(this);
            /**
             * TODO Fix this.
             * For some reason, the mount disappears without this kind of delay.
             */
            new BukkitRunnable() {
                @Override
                public void run() {
                    cosmeticsProfileManager.initPlayers();
                }
            }.runTaskLater(this, 20L);
        }

        GeneralUtil.printPermissions(this, SettingsManager.getConfig().getBoolean("Check-For-Updates"));

        // Ended well :v
        getSmartLogger().write("UltraCosmetics successfully finished loading and is now enabled!");
        getSmartLogger().write("-------------------------------------------------------------------");
    }

    /**
     * Called when plugin disables.
     */
    @Override
    public void onDisable() {
        // when the plugin is disabled from onEnable, skip cleanup
        if (UltraCosmeticsData.get().getServerVersion() == null) {
            return;
        }
        // TODO Purge Pet Names. (and Treasure Chests bugged holograms).
        // TODO Use Metadatas for that!

        if (cosmeticsProfileManager != null) {
            for (CosmeticsProfile cp : cosmeticsProfileManager.getCosmeticsProfiles().values()) {
                cp.save();
            }
        }

        playerManager.dispose();

        UltraCosmeticsData.get().getVersionManager().getModule().disable();
    }

    /**
     * Registers Listeners.
     */
    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new MainListener(), this);
        pluginManager.registerEvents(new EntitySpawningManager(), this);

        if (UltraCosmeticsData.get().getServerVersion().offhandAvailable()) {
            pluginManager.registerEvents(new PlayerSwapItemListener(this), this);
        }

        this.treasureChestManager = new TreasureChestManager(this);
        pluginManager.registerEvents(treasureChestManager, this);
    }

    /**
     * Sets the economy up.
     */
    private void setupEconomy() {
        economyHandler = new EconomyHandler(this, getConfig().getString("Economy"));
        UltraCosmeticsData.get().checkTreasureChests();
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

        List<String> disabledCommands = new ArrayList<>();
        disabledCommands.add("hat");
        config.addDefault("Disabled-Commands", disabledCommands, "List of commands that won't work when cosmetics are equipped.", "Command arguments are ignored, commands are blocked when base command matches.");

        // do not use Stream#map() for this, it won't work, for example:
        // Bukkit.getWorlds().stream().map(World::getName)
        // In 1.17 it's WorldInfo#getName but in all lower versions
        // the method is World#getName. Referencing the method
        // specifically using ::'s breaks it on said lower versions.
        List<String> enabledWorlds = new ArrayList<>();
        Bukkit.getWorlds().forEach(k -> enabledWorlds.add(k.getName()));
        config.addDefault("Enabled-Worlds", enabledWorlds, "List of the worlds", "where cosmetics are enabled!");

        config.set("Disabled-Items", null);
        config.addDefault("Economy", "Vault");

        // Add default values people could not have because of an old version of UC.
        if (!config.contains("TreasureChests.Loots.Money.Min")) {
            int min = 15;
            int max = config.getInt("TreasureChests.Loots.Money.Max");
            if (max < 5)
                min = 0;
            else if (max < 15)
                min = 5;
            config.set("TreasureChests.Loots.Money.Min", min);
        }

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

        if (!config.contains("TreasureChests.Loots.Commands")) {
            config.createSection("TreasureChests.Loots.Commands");
            String section = "TreasureChests.Loots.Commands.shoutout";
            config.set(section + ".Name", "&d&lShoutout");
            config.set(section + ".Material", "NETHER_STAR");
            config.set(section + ".Enabled", false);
            config.set(section + ".Chance", 100);
            config.set(section + ".Message.enabled", false);
            config.set(section + ".Message.message", "%prefix% &6&l%name% found a rare shoutout!");
            config.set(section + ".Cancel-If-Permission", "no");
            config.set(section + ".Commands", Collections.singletonList("say %name% is awesome!"));
            section = "TreasureChests.Loots.Commands.flower";
            config.set(section + ".Name", "&e&lFlower");
            config.set(section + ".Material", "YELLOW_FLOWER");
            config.set(section + ".Enabled", false);
            config.set(section + ".Chance", 100);
            config.set(section + ".Message.enabled", false);
            config.set(section + ".Message.message", "%prefix% &6&l%name% found a flower!");
            config.set(section + ".Cancel-If-Permission", "example.yellowflower");
            config.set(section + ".Commands", Arrays.asList("give %name% yellow_flower 1", "pex user %name% add example.yellowflower"));
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

        // Remove enabled field, replace by is-enabled (to replace to false by def)
        if (config.contains("Auto-Equip-Cosmetics.enabled")) {
            config.set("Auto-Equip-Cosmetics.enabled", null);
            config.set("Auto-Equip-Cosmetics.is-enabled", false);
        }

        if (!config.contains("Auto-Equip-Cosmetics")) {
            config.createSection("Auto-Equip-Cosmetics", "[WARNING: ALPHA!]",
                    "Allows for players to auto-equip on join cosmetics they had before disconnecting.",
                    "At the moment, only works while the server is up. Upon shutdown, the cosmetics saved states",
                    "are reset! Doesn't support MySQL yet.");
            config.set("Auto-Equip-Cosmetics.is-enabled", false);
            //config.set("Auto-Equip-Cosmetics.on-join", true);
        }

        if (!config.contains("allow-damage-to-players-on-mounts")) {
            config.set("allow-damage-to-players-on-mounts", false);
        }

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
     * @return The Update CheckerC.
     */
    public UpdateManager getUpdateChecker() {
        return updateChecker;
    }

    /**
     * @return The Treasure Chest Manager.
     */
    public TreasureChestManager getTreasureChestManager() {
        return treasureChestManager;
    }

    /**
     * @return The menus.
     */
    public Menus getMenus() {
        return menus;
    }

    /**
     * @return MySql Manager.
     */
    public MySqlConnectionManager getMySqlConnectionManager() {
        return mySqlConnectionManager;
    }

    public ArmorStandManager getArmorStandManager() {
        return armorStandManager;
    }

    public void openMainMenu(UltraPlayer ultraPlayer) {
        if (getConfig().getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled")) {
            String command = getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "").replace("{player}", ultraPlayer.getBukkitPlayer().getName()).replace("{playeruuid}", ultraPlayer.getUuid().toString());
            getServer().dispatchCommand(getServer().getConsoleSender(), command);
        } else {
            getMenus().getMainMenu().open(ultraPlayer);
        }
    }

    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }

    public CosmeticsProfileManager getCosmeticsProfileManager() {
        return cosmeticsProfileManager;
    }

    public AFlagManager getFlagManager() {
        return flagManager;
    }

    public boolean worldGuardHooked() {
        return flagManager != null;
    }

    public boolean areCosmeticsAllowedInRegion(Player player) {
        return !worldGuardHooked() || flagManager.areCosmeticsAllowedHere(player);
    }

    public boolean areChestsAllowedInRegion(Player player) {
        return !worldGuardHooked() || flagManager.areChestsAllowedHere(player);
    }
}
