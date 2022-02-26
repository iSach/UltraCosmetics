package be.isach.ultracosmetics;

import be.isach.ultracosmetics.command.CommandManager;
import be.isach.ultracosmetics.config.AutoCommentConfiguration;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.ManualCommentConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.economy.EconomyHandler;
import be.isach.ultracosmetics.listeners.Listener19;
import be.isach.ultracosmetics.listeners.MainListener;
import be.isach.ultracosmetics.listeners.PlayerListener;
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
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private boolean legacyMessagePrinted = false;
    
    /**
     * Called when plugin is loaded.
     * Used for registering WorldGuard flags
     * as recommended in API documentation.
     */
    @Override
    public void onLoad() {
        // moved to onLoad so it's ready for WorldGuard support
        this.smartLogger = new SmartLogger(getLogger());

        // trigger legacy material loading during startup so we don't hang the server at some point when players are online.
        // TODO: completely remove the parts that require legacy materials
        smartLogger.write("UltraCosmetics still contains some legacy material code, so 'Legacy Material Support' is still required.");
        Material.getMaterial("", true);

        UltraCosmeticsData.init(this);

        if (!UltraCosmeticsData.get().checkServerVersion()) {
            return;
        }

        // Use super.getConfig() because CustomConfiguration doesn't load until onEnable
        boolean worldGuardIntegration = super.getConfig().getBoolean("WorldGuard-Integration", true);
        // Not using isPluginEnabled() because WorldGuard should be
        // loaded but not yet enabled when registering flags
        if (worldGuardIntegration && getServer().getPluginManager().getPlugin("WorldGuard") != null) {
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
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getSmartLogger().write();
            new PlaceholderHook(this).register();
            getSmartLogger().write("Hooked into PlaceholderAPI");
        }

        // Set up WorldGuard if needed.
        setupWorldGuard();

        // Set up economy if needed.
        setupEconomy();

        if (!UltraCosmeticsData.get().usingFileStorage()) {
            getSmartLogger().write();
            getSmartLogger().write("Connecting to MySQL database...");

            // Start MySQL.
            this.mySqlConnectionManager = new MySqlConnectionManager(this);
            mySqlConnectionManager.start();

            getSmartLogger().write("Connected to MySQL database.");
        }

        // Initialize UltraPlayers and give chest (if needed).

        playerManager.initPlayers();

        // Start the Fall Damage and Invalid World Check Runnables.

        new FallDamageManager().runTaskTimerAsynchronously(this, 0, 1);
        new MovingChecker(this).runTaskTimerAsynchronously(this, 0, 1);
        // No need to worry about the invalid world checker if all worlds are allowed
        if (!config.getStringList("Enabled-Worlds").contains("*")) {
            new InvalidWorldChecker(this).runTaskTimerAsynchronously(this, 0, 5);
        }

        // Start up bStats
        new Metrics(this, 2629);

        this.menus = new Menus(this);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (SettingsManager.getConfig().getBoolean("Check-For-Updates")) {
            updateChecker = new UpdateManager(this);
            updateChecker.runTaskAsynchronously(this);
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

        GeneralUtil.printPermissions(this);

        // Ended well :v
        getSmartLogger().write();
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
            pluginManager.registerEvents(new Listener19(this), this);
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

    private void setupWorldGuard() {
        if (flagManager != null) {
            if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                getSmartLogger().write(LogLevel.ERROR, "WorldGuard is not enabled yet! Is WorldGuard up to date? Is another plugin interfering with the load order?");
                getSmartLogger().write(LogLevel.ERROR, "WorldGuard support will be disabled.");
                flagManager = null;
                return;
            }
            flagManager.registerPhase2();
            getSmartLogger().write();
            getSmartLogger().write("WorldGuard custom flags enabled");
        }
    }

    private void setUpConfig() {
        file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource("config.yml", false);
            getSmartLogger().write("Config file doesn't exist yet.");
            getSmartLogger().write("Creating Config File and loading it.");
        }

        config = loadConfiguration(file);

        List<String> disabledCommands = new ArrayList<>();
        disabledCommands.add("hat");
        config.addDefault("Disabled-Commands", disabledCommands, "List of commands that won't work when cosmetics are equipped.", "Command arguments are ignored, commands are blocked when base command matches.");

        List<String> enabledWorlds = new ArrayList<>();
        enabledWorlds.add("*");
        config.addDefault("Enabled-Worlds", enabledWorlds, "List of the worlds", "where cosmetics are enabled!", "If list contains '*',", "all worlds will be allowed.");

        config.set("Disabled-Items", null);
        config.addDefault("Economy", "Vault");

        // getInt() defaults to 0 if not found
        if (config.getInt("TreasureChests.Count") < 1 || config.getInt("TreasureChests.Count") > 4) {
            config.set("TreasureChests.Count", 4, "How many treasure chests should be opened per key? Min 1, max 4");
        }
        // Add default values people could not have because of an old version of UC.
        if (!config.isConfigurationSection("TreasureChests.Location")) {
            ConfigurationSection section = config.createSection("TreasureChests.Location");
            config.set("TreasureChests.Location.Enabled", false, "Whether players should be moved to a certain", "location before opening a treasure chest.", "Does not override /uc treasure.");
            config.set("TreasureChests.Location.X", 0, "The location players should be moved to.", "Block coordinates only, like 104, not 103.63", "To use the world the player is in, set World to 'none'");
            section.set("Y", 63);
            section.set("Z", 0);
        }

        config.addDefault("TreasureChests.Location.World", Bukkit.getWorlds().get(0).getName());

        if (!config.isInt("TreasureChests.Loots.Money.Min")) {
            int min = 15;
            int max = config.getInt("TreasureChests.Loots.Money.Max");
            if (max < 5)
                min = 0;
            else if (max < 15)
                min = 5;
            config.set("TreasureChests.Loots.Money.Min", min);
        }

        if (!config.isConfigurationSection("TreasureChests.Loots.Gadgets")) {
            ConfigurationSection section = config.createSection("TreasureChests.Loots.Gadgets", "Chance of getting a GADGET", "This is different from ammo!");
            section.set("Enabled", true);
            section.set("Chance", 20);
            section.set("Message.enabled", false);
            section.set("Message.message", "%prefix% &6&l%name% found gadget %gadget%");
        }

        if (!config.isConfigurationSection("TreasureChests.Loots.Suits")) {
            config.createSection("TreasureChests.Loots.Suits");
            config.set("TreasureChests.Loots.Suits.Enabled", true);
            config.set("TreasureChests.Loots.Suits.Chance", 10);
            config.set("TreasureChests.Loots.Suits.Message.enabled", false);
            config.set("TreasureChests.Loots.Suits.Message.message", "%prefix% &6&l%name% found suit part: %suitw%");
        }

        if (!config.isConfigurationSection("Categories.Suits")) {
            ConfigurationSection suits = config.createSection("Categories.Suits");
            suits.set("Main-Menu-Item", XMaterial.LEATHER_CHESTPLATE.parseMaterial().toString());
            suits.set("Go-Back-Arrow", true);
        }

        if (!config.isConfigurationSection("TreasureChests.Loots.Commands")) {
            ConfigurationSection section = config.createSection("TreasureChests.Loots.Commands.shoutout");
            section.set("Name", "&d&lShoutout");
            section.set("Material", "NETHER_STAR");
            section.set("Enabled", false);
            section.set("Chance", 100);
            section.set("Message.enabled", false);
            section.set("Message.message", "%prefix% &6&l%name% found a rare shoutout!");
            section.set("Cancel-If-Permission", "no");
            section.set("Commands", Collections.singletonList("say %name% is awesome!"));

            section = config.createSection("TreasureChests.Loots.Commands.flower");
            section.set("Name", "&e&lFlower");
            section.set("Material", "YELLOW_FLOWER");
            section.set("Enabled", false);
            section.set("Chance", 100);
            section.set("Message.enabled", false);
            section.set("Message.message", "%prefix% &6&l%name% found a flower!");
            section.set("Cancel-If-Permission", "example.yellowflower");
            section.set("Commands", Arrays.asList("give %name% yellow_flower 1", "lp user %name% permission set example.yellowflower true"));
        }

        config.addDefault("Categories.Clear-Cosmetic-Item", XMaterial.REDSTONE_BLOCK.parseMaterial().toString(), "Item where user click to clear a cosmetic.");
        config.addDefault("Categories.Previous-Page-Item", XMaterial.ENDER_PEARL.parseMaterial().toString(), "Previous Page Item");
        config.addDefault("Categories.Next-Page-Item", XMaterial.ENDER_EYE.parseMaterial().toString(), "Next Page Item");
        config.addDefault("Categories.Back-Main-Menu-Item", XMaterial.ARROW.parseMaterial().toString(), "Back to Main Menu Item");
        config.addDefault("Categories.Self-View-Item.When-Enabled", XMaterial.ENDER_EYE.parseMaterial().toString(), "Item in Morphs Menu when Self View enabled.");
        config.addDefault("Categories.Self-View-Item.When-Disabled", XMaterial.ENDER_PEARL.parseMaterial().toString(), "Item in Morphs Menu when Self View disabled.");
        config.addDefault("Categories.Gadgets-Item.When-Enabled", XMaterial.LIGHT_GRAY_DYE.parseMaterial().toString(), "Item in Gadgets Menu when Gadgets enabled.");
        config.addDefault("Categories.Gadgets-Item.When-Disabled", XMaterial.GRAY_DYE.parseMaterial().toString(), "Item in Gadgets Menu when Gadgets disabled.");
        config.addDefault("Categories.Rename-Pet-Item", XMaterial.NAME_TAG.parseMaterial().toString(), "Item in Pets Menu to rename current pet.");
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

        config.addDefault("WorldGuard-Integration", true, "Whether WorldGuard should be hooked when loading UC", "Disable this if UC has trouble loading WorldGuard");
        config.addDefault("Pets-Are-Silent", false, "Are pets prevented from making sounds?");

        config.addDefault("Menu-Item.Custom-Model-Data", 0, "Custom model data for the menu item. Only supported on MC >= 1.14.4 (when it was added)");
        config.addDefault("Menu-Item.Open-Menu-On-Inventory-Click", false, "Whether to open cosmetics menu when the menu item is clicked from the player's inventory");
        config.set("Menu-Item.Data", null);

        upgradeIdsToMaterials();

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
     * @return The Update Checker.
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
            String command = getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "").replace("{player}", ultraPlayer.getBukkitPlayer().getName()).replace("{playeruuid}", ultraPlayer.getUUID().toString());
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

    public CustomConfiguration loadConfiguration(File file) {
        CustomConfiguration config;
        // In 1.18.1 and later, Spigot supports comment preservation and
        // writing comments programmatically, so use built-in methods if we can.
        // Check if the method exists before we load AutoCommentConfig
        try {
            ConfigurationSection.class.getDeclaredMethod("getComments", String.class);
            config = new AutoCommentConfiguration();
        } catch (NoSuchMethodException ignored) {
            // getComments() doesn't exist yet, load ManualCommentConfig
            config = new ManualCommentConfiguration();
        } catch (SecurityException e) {
            // ???
            e.printStackTrace();
            return null;
        }

        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException ex) {
            getSmartLogger().write(LogLevel.ERROR, "Cannot load " + file, ex);
        }
        return config;
    }

    private void upgradeIdsToMaterials() {
        upgradeKeyToMaterial("Categories.Gadgets.Main-Menu-Item", "409:0", XMaterial.PRISMARINE_SHARD);
        upgradeKeyToMaterial("Categories.Particle-Effects.Main-Menu-Item", "399:0", XMaterial.NETHER_STAR);
        upgradeKeyToMaterial("Categories.Mounts.Main-Menu-Item", "329:0", XMaterial.SADDLE);
        upgradeKeyToMaterial("Categories.Pets.Main-Menu-Item", "352:0", XMaterial.BONE);
        upgradeKeyToMaterial("Categories.Morphs.Main-Menu-Item", "334:0", XMaterial.LEATHER);
        upgradeKeyToMaterial("Categories.Hats.Main-Menu-Item", "314:0", XMaterial.GOLDEN_HELMET);
        upgradeKeyToMaterial("Categories.Suits.Main-Menu-Item", "299:0", XMaterial.LEATHER_CHESTPLATE);
        upgradeKeyToMaterial("Categories.Clear-Cosmetic-Item", "152:0", XMaterial.REDSTONE_BLOCK);

        upgradeKeyToMaterial("Categories.Previous-Page-Item", "368:0", XMaterial.ENDER_PEARL);
        upgradeKeyToMaterial("Categories.Next-Page-Item", "381:0", XMaterial.ENDER_EYE);
        upgradeKeyToMaterial("Categories.Back-Main-Menu-Item", "262:0", XMaterial.ARROW);
        upgradeKeyToMaterial("Categories.Self-View-Item.When-Enabled", "381:0", XMaterial.ENDER_EYE);
        upgradeKeyToMaterial("Categories.Self-View-Item.When-Disabled", "368:0", XMaterial.ENDER_PEARL);
        upgradeKeyToMaterial("Categories.Gadgets-Item.When-Enabled", "351:10", XMaterial.LIGHT_GRAY_DYE);
        upgradeKeyToMaterial("Categories.Gadgets-Item.When-Disabled", "351:8", XMaterial.GRAY_DYE);
        upgradeKeyToMaterial("Categories.Rename-Pet-Item", "421:0", XMaterial.NAME_TAG);

        upgradeKeyToMaterial("TreasureChests.Designs.Classic.center-block", "169:0", XMaterial.SEA_LANTERN);
        upgradeKeyToMaterial("TreasureChests.Designs.Classic.around-center", "5:0", XMaterial.OAK_PLANKS);
        upgradeKeyToMaterial("TreasureChests.Designs.Classic.third-blocks", "5:1", XMaterial.SPRUCE_PLANKS);
        upgradeKeyToMaterial("TreasureChests.Designs.Classic.below-chests", "17:0", XMaterial.OAK_LOG);
        upgradeKeyToMaterial("TreasureChests.Designs.Classic.barriers", "85:0", XMaterial.OAK_FENCE);

        upgradeKeyToMaterial("TreasureChests.Designs.Modern.center-block", "169:0", XMaterial.SEA_LANTERN);
        upgradeKeyToMaterial("TreasureChests.Designs.Modern.around-center", "159:11", XMaterial.BLUE_TERRACOTTA);
        upgradeKeyToMaterial("TreasureChests.Designs.Modern.third-blocks", "155:0", XMaterial.WHITE_TERRACOTTA);
        upgradeKeyToMaterial("TreasureChests.Designs.Modern.below-chests", "159:11", XMaterial.BLUE_TERRACOTTA);
        upgradeKeyToMaterial("TreasureChests.Designs.Modern.barriers", "160:3", XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE);

        upgradeKeyToMaterial("TreasureChests.Designs.Nether.center-block", "89:0", XMaterial.GLOWSTONE);
        upgradeKeyToMaterial("TreasureChests.Designs.Nether.around-center", "88:0", XMaterial.SOUL_SAND);
        upgradeKeyToMaterial("TreasureChests.Designs.Nether.third-blocks", "87:0", XMaterial.NETHERRACK);
        upgradeKeyToMaterial("TreasureChests.Designs.Nether.below-chests", "112:0", XMaterial.NETHER_BRICKS);
        upgradeKeyToMaterial("TreasureChests.Designs.Nether.barriers", "113:0", XMaterial.NETHER_BRICK_FENCE);
    }

    private void upgradeKeyToMaterial(String key, String oldValue, XMaterial newValue) {
        if (oldValue.equals(config.getString(key))) {
            if (!legacyMessagePrinted) {
                getSmartLogger().write(LogLevel.WARNING, "You seem to still have numeric IDs in your config, which UC no longer supports.");
                getSmartLogger().write(LogLevel.WARNING, "I'll attempt to upgrade them, but only if the values haven't been touched.");
                legacyMessagePrinted = true;
            }
            config.set(key, newValue.toString());
            getSmartLogger().write(LogLevel.INFO, "Successfully upgraded key '" + key + "' from '" + oldValue + "' to '" + newValue + "'!");
        // this code runs on every startup so don't print "failed to upgrade" message unless there's an actual issue
        } else if (legacyMessagePrinted) {
            getSmartLogger().write(LogLevel.WARNING, "Couldn't upgrade key '" + key + "' because it has been changed. Please upgrade it manually.");
        }
    }
}
