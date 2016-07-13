package be.isach.ultracosmetics;

import be.isach.ultracosmetics.command.CommandManager;
import be.isach.ultracosmetics.command.subcommands.*;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.CosmeticType;
import be.isach.ultracosmetics.cosmetics.emotes.EmoteType;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetDiscoBall;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetExplosiveSheep;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.MorphType;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffectType;
import be.isach.ultracosmetics.cosmetics.pets.IPlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
import be.isach.ultracosmetics.cosmetics.suits.SuitType;
import be.isach.ultracosmetics.listeners.MainListener;
import be.isach.ultracosmetics.listeners.PlayerListener;
import be.isach.ultracosmetics.listeners.v1_9.PlayerSwapItemListener;
import be.isach.ultracosmetics.manager.PlayerManager;
import be.isach.ultracosmetics.manager.SQLLoaderManager;
import be.isach.ultracosmetics.manager.TreasureChestManager;
import be.isach.ultracosmetics.menu.*;
import be.isach.ultracosmetics.mysql.MySQLConnection;
import be.isach.ultracosmetics.mysql.Table;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.run.InvalidWorldManager;
import be.isach.ultracosmetics.util.*;
import be.isach.ultracosmetics.version.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by sacha on 03/08/15.
 */
public class UltraCosmetics extends JavaPlugin {

    /**
     * Manages sub commands.
     */
    private CommandManager commandManager;

    /**
     * List containing all the active Disco Balls.
     */
    public List<GadgetDiscoBall> discoBalls = Collections.synchronizedList(new ArrayList<GadgetDiscoBall>());

    /**
     * List containing all the active Explosive Sheep.
     */
    public List<GadgetExplosiveSheep> explosiveSheep = Collections.synchronizedList(new ArrayList<GadgetExplosiveSheep>());

    /**
     * If true, the color will be removed in placeholders.
     */
    public boolean placeHolderColor;

    /**
     * If true, means vault is loaded and enabled.
     */
    private boolean vaultLoaded;

    /**
     * Menu Listeners.
     */
    private Listener

            /**
             * Main Menu Listener.
             */
            mainMenuListener,

    /**
     * Morph Menu Listener.
     */
    morphMenuListener;

    /**
     * Determines if Treasure Chest Money Loot enabled.
     */
    public static boolean moneyTreasureLoot,

    /**
     * Determines if Gadget Cooldown should be shown in action bar.
     */
    cooldownInBar,

    /**
     * Determines if Pet Renaming required Money.
     */
    petRenameMoney,

    /**
     * Should the GUI close after Cosmetic Selection?
     */
    closeAfterSelect;

    /**
     * Current UC version.
     */
    public static String currentVersion,

    /**
     * Command to execute when going back to Main Menu.
     */
    customBackMenuCommand;

    /**
     * List of enabled categories.
     */
    public static List<Category> enabledCategories = new ArrayList<>();

    /**
     * The Configuration. (config.yml)
     */
    public static CustomConfiguration config;

    /**
     * Config File.
     */
    public static File file;

    /**
     * Economy, used only if Vault is enabled.
     */
    public static Economy economy = null;

    public static SQLUtils sqlUtils;

    /**
     * If true, plugin is outdated.
     */
    public static boolean outdated;

    /**
     * Last Version published on spigotmc.org.
     */
    public static String lastVersion;

    /**
     * If true, debug messages will be shown.
     */
    static boolean debug = false;

    /**
     * If true, the server is using Spigot and not CraftBukkit/Bukkit.
     */
    private static boolean usingSpigot = false,

    /**
     * True -> should execute custom command when going back to main menu.
     */
    customCommandBackArrow;

    /**
     * {@code true} if NoteBlockAPI can be used, {@code false} otherwise.
     */
    private static boolean noteBlockAPIEnabled;

    /**
     * Determines if Ammo Use is enabled.
     */
    private static boolean ammoEnabled,

    /**
     * Determines if File Storage is enabled.
     */
    fileStorage = true,

    /**
     * Determines if Treasure Chests are enabled.
     */
    treasureChests;


    /**
     * Instance.
     */
    private static UltraCosmetics core;

    /**
     * Server NMS version.
     */
    private static ServerVersion serverVersion;

    /**
     * Player Manager instance.
     */
    private static PlayerManager playerManager;

    /**
     * MySQL Connection.
     */
    public Connection co;

    /**
     * MySQL Table.
     */
    public Table table;

    /**
     * SQLLoader Manager instance
     */
    private static SQLLoaderManager sqlloader;

    /**
     * MySQL Stuff.
     */
    private MySQLConnection sql;

    /**
     * Server version manager
     */
    private VersionManager versionManager;

    /**
     * Called when plugin is enabled.
     */
    @Override
    public void onEnable() {
        if (!getServer().getVersion().contains("1.8.8") && !getServer().getVersion().contains("1.9") && !getServer().getVersion().contains("1.10")) {
            System.out.println("----------------------------\n\nUltraCosmetics requires Spigot 1.8.8 or 1.9 to work!\n\n----------------------------");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String mcVersion = "1.8.8";

        try {
            mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
        }

        if (mcVersion.startsWith("v")) {
            try {
                serverVersion = ServerVersion.valueOf(mcVersion);
            } catch (Exception exc) {
                log("This NMS version isn't supported. (" + mcVersion + ")!");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        } else serverVersion = ServerVersion.v1_8_R3;

        if (getServer().getVersion().contains("Spigot"))
            usingSpigot = true;

        playerManager = new PlayerManager();
        currentVersion = getDescription().getVersion();

        log("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        log("UltraCosmetics v" + getDescription().getVersion() + " is being loaded... (server: " + serverVersion.getName() + ")");

        log("");
        log("Thanks for having downloaded it!");
        log("");
        log("Plugin by iSach.");
        log("Link: http://bit.ly/UltraCosmetics");

        log("");
        log("Loading configuration...");

        file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            FileUtils.copy(getResource("config.yml"), file);
            log("Config file doesn't exist yet.");
            log("Creating Config File and loading it.");
        }

        config = CustomConfiguration.loadConfiguration(file);

        List<String> enabledWorlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds())
            enabledWorlds.add(world.getName());
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

        saveConfig();

        customCommandBackArrow = config.getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled");
        customBackMenuCommand = config.getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "");

        closeAfterSelect = config.getBoolean("Categories.Close-GUI-After-Select");

        log("Configuration loaded.");
        log("");

        core = this;

        log("Initializing module " + serverVersion);
        versionManager = new VersionManager(serverVersion);
        try {
            versionManager.load();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            log("No module found for " + serverVersion + " disabling");
        }
        versionManager.getModule().enable();
        log("Module initialized");
        log("");

        log("");
        log("Preparing Metrics data.");
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
            log("Data sent to Metrics successfully.");
        } catch (IOException e) {
            System.out.println("Couldn't send data to Metrics :(");
        }
        log("");

        if (getDescription().getVersion().startsWith("Pre")) {
            log("");
            log("THIS IS AN UNSTABLE VERSION, NO SUPPORT FOR IT!");
            log("");
            debug = true;
        }

        if (Bukkit.getPluginManager().getPlugin("NoteBlockAPI") != null) {
            log("");
            log("NoteBlockAPI loaded and hooked.");
            log("");
            noteBlockAPIEnabled = true;
        }


        log("");
        log("Registering Messages...");
        new MessageManager();
        log("Messages registered.");
        log("");

        registerListener(new PlayerListener());
        if (serverVersion.compareTo(ServerVersion.v1_9_R1) >= 0)
            registerListener(new PlayerSwapItemListener());

        log("");
        log("Registering commands...");
        // Register the command

        commandManager = new CommandManager(this);
        commandManager.registerCommand(new SubCommandGadgets());
        commandManager.registerCommand(new SubCommandSelfView());
        commandManager.registerCommand(new SubCommandMenu());
        commandManager.registerCommand(new SubCommandGive());
        commandManager.registerCommand(new SubCommandToggle());
        commandManager.registerCommand(new SubCommandClear());
        commandManager.registerCommand(new SubCommandTreasure());

        log("Registered command: '/ultracosmetics'.");
        log("Registered command: '/uc'.");
        log("Registered commands.");
        log("");

        String s = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.System");
        fileStorage = s.equalsIgnoreCase("file");
        placeHolderColor = SettingsManager.getConfig().getBoolean("Chat-Cosmetic-PlaceHolder-Color");
        ammoEnabled = SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Enabled");
        cooldownInBar = SettingsManager.getConfig().getBoolean("Categories.Gadgets.Cooldown-In-ActionBar");

        for (Category c : Category.values()) {
            if (c == Category.MORPHS)
                if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
                    continue;
            if (c.isEnabled())
                enabledCategories.add(c);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkTreasureChests();

        new Thread() {

            @Override
            public void run() {
                if (SettingsManager.getConfig().getBoolean("Check-For-Updates"))
                    checkForUpdate();
                if (outdated)
                    for (Player p : Bukkit.getOnlinePlayers())
                        if (p.isOp())
                            p.sendMessage("§l§oUltraCosmetics > §c§lAn update is available: " + lastVersion);
            }
        }.run();

        log("Registering Cosmetics...");
        setupCosmeticsConfigs();

        enabledCategories.clear();
        for (Category c : Category.values()) {
            if (c == Category.MORPHS)
                if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
                    continue;
            if (c.isEnabled())
                enabledCategories.add(c);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Cosmetics Registered.");

        if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            log("");
            log("Morphs require Lib's Disguises!");
            log("");
            log("Morphs are disabling..");
            log("");
        }

        petRenameMoney = SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled");
        if ((ammoEnabled
                || (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled"))
                && SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled"))) {
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                log("");
                log("Vault not found!");
                if (petRenameMoney) {
                    log("  Pet renaming will not require Money.");
                }
                if (ammoEnabled) {
                    log("  Ammo Disabled.");
                }
                log("");
                petRenameMoney = false;
                ammoEnabled = false;
            }
        }

        if ((ammoEnabled
                || (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled") && SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled"))
                || (areTreasureChestsEnabled() && SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Enabled"))) && Bukkit.getPluginManager().isPluginEnabled("Vault"))
            setupEconomy();

        if (!fileStorage) {
            log("");
            log("Connecting to MySQL database...");
            startMySQL();
            log("Connected to MySQL database.");
            log("");
        }
        initPlayers();

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new FallDamageManager(), 0, 1);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new InvalidWorldManager(), 0, 5);

        if (noteBlockAPIEnabled) {
            File folder = new File(getDataFolder().getPath() + "/songs/");
            if ((!folder.exists()) || (folder.listFiles().length <= 0))
                saveResource("songs/GetLucky.nbs", true);
            saveResource("songs/NyanCat.nbs", true);
        }

        log("");
        log("Registering listeners...");
        mainMenuListener = new MainMenuManager();
        registerListener(mainMenuListener);
        registerListener(new GadgetManager());
        registerListener(new PetManager());
        registerListener(new MountManager());
        registerListener(new ParticleEffectManager());
        registerListener(new PetManager());
        registerListener(new HatManager());
        registerListener(new SuitManager());
        registerListener(new EmoteManager());
        registerListener(new TreasureChestManager());
        registerListener(new MainListener());
        if (Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            morphMenuListener = new MorphManager();
            registerListener(morphMenuListener);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Listeners registered.");
        log("");
        log("");
        log("UltraCosmetics successfully finished loading and is now enabled! (server: " + serverVersion.getName() + ")");

        log("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }

    /**
     * Logs a message in console.
     *
     * @param object The message to log.
     */
    public static void log(String object) {
        System.out.println("UltraCosmetics -> " + object.toString());
    }

    /**
     * Get a collection of all the CustomPlayers.
     *
     * @return
     */
    public static Collection<UltraPlayer> getCustomPlayers() {
        return playerManager.getPlayers();
    }

    /**
     * @return if ammo system is enabled, or not.
     */
    public boolean isAmmoEnabled() {
        return ammoEnabled;
    }

    /**
     * @return if NoteBlockAPI is loaded.
     */
    public boolean isNoteBlockAPIEnabled() {
        return noteBlockAPIEnabled;
    }

    /**
     * @return if file storage is used.
     */
    public boolean usingFileStorage() {
        return fileStorage;
    }

    public boolean areTreasureChestsEnabled() {
        return treasureChests;
    }

    /**
     * Debugs something.
     *
     * @param message The message to print.
     * @return if debug is turned on or off.
     */
    public static boolean debug(Object message) {
        if (debug) Bukkit.broadcastMessage("§c§lUC-DEBUG> §f" + message.toString());
        return debug;
    }

    /**
     * Gets the UltraCosmetics Plugin Object.
     *
     * @return
     */
    public static UltraCosmetics getInstance() {
        return core;
    }

    /**
     * Registers a listener.
     *
     * @param listenerClass The listener to register.
     */
    public void registerListener(Listener listenerClass) {
        Bukkit.getPluginManager().registerEvents(listenerClass, getInstance());
    }

    /**
     * Gets the custom player of a player.
     *
     * @param player The player.
     * @return The UltraPlayer of player.
     */
    public static UltraPlayer getCustomPlayer(Player player) {
        return playerManager.getCustomPlayer(player);
    }

    /**
     * Gets the Custom Player Manager.
     *
     * @return the Custom Player Manager.
     */
    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Gets the SQLloader Manager
     *
     * @return the SQLloader Manager
     */
    public static SQLLoaderManager getSQLLoader() {
        return sqlloader;
    }

    /**
     * Gets last version published on Spigot.
     *
     * @return last version published on Spigot.
     */
    public static String getLastVersion() {
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


    /**
     * Removes color in a text.
     *
     * @param toFilter The text to filter.
     * @return The filtered text.
     */
    public static CharSequence filterColor(String toFilter) {
        Character[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'o', 'n', 'm', 'r', 'k'};
        for (Character character : chars)
            toFilter = toFilter.replace("§" + character, "");
        return toFilter;
    }

    public static ServerVersion getServerVersion() {
        return serverVersion;
    }

    public static boolean usingSpigot() {
        return usingSpigot;
    }

    /**
     * Overrides config saving to keep comments.
     */
    @Override
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize players.
     */
    private void initPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerManager.create(p);
            if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join")
                    && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(p.getWorld().getName()))
                playerManager.getCustomPlayer(p).giveMenuItem();
        }
    }

    public IActionBar getActionBarUtil() {
        return versionManager.getActionBarUtil();
    }

    public IItemGlower getItemGlower() {
        return versionManager.getItemGlower();
    }

    public IFireworkFactory getFireworkFactory() {
        return versionManager.getFireworkFactory();
    }

    public IMounts getMounts() {
        return versionManager.getMounts();
    }

    public IPets getPets() {
        return versionManager.getPets();
    }

    public IMorphs getMorphs() {
        return versionManager.getMorphs();
    }

    public AAnvilGUI newAnvilGUI(Player player, AAnvilGUI.AnvilClickEventHandler handler) {
        return versionManager.newAnvilGUI(player, handler);
    }

    public IPathfinderUtil getPathfinderUtil() {
        return versionManager.getPathfinderUtil();
    }

    public IPlayerFollower newPlayerFollower(Pet pet, Player player) {
        return versionManager.newPlayerFollower(pet, player);
    }

    /**
     * Check if placeholders should be colored.
     *
     * @return
     */
    public boolean placeholdersHaveColor() {
        return placeHolderColor;
    }

    /**
     * @return {@code true} if vault is loaded, otherwise {@code false}
     */
    public boolean isVaultLoaded() {
        return vaultLoaded;
    }

    private int i = 0;

    /**
     * Starts MySQL loop.
     */
    private void startMySQL() {
        if (!fileStorage) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        String hostname = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.hostname"));
                        String portNumber = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.port"));
                        String database = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.database"));
                        String username = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.username"));
                        String password = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.password"));
                        sql = new MySQLConnection(hostname, portNumber, database, username, password);
                        co = sql.getConnection();
                        if (i == 0) {
                            Bukkit.getConsoleSender().sendMessage("§b§lUltraCosmetics -> Successfully connected to MySQL server! :)");
                            i++;
                        }
                        PreparedStatement sql = co.prepareStatement("CREATE TABLE IF NOT EXISTS UltraCosmeticsData(" +
                                "id INTEGER not NULL AUTO_INCREMENT," +
                                " uuid VARCHAR(255)," +
                                " username VARCHAR(255),"
                                + " PRIMARY KEY ( id ))");
                        sql.executeUpdate();
                        for (GadgetType gadgetType : GadgetType.values()) {
                            DatabaseMetaData md = co.getMetaData();
                            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", gadgetType.toString().replace("_", "").toLowerCase());
                            if (!rs.next()) {
                                PreparedStatement statement = co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD " + gadgetType.toString().replace("_", "").toLowerCase() + " INTEGER DEFAULT 0 not NULL");
                                statement.executeUpdate();
                            }
                        }
                        table = new Table(co, "UltraCosmeticsData");
                        sqlUtils = new SQLUtils(core);
                        DatabaseMetaData md = co.getMetaData();
                        ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "treasureKeys");
                        if (!rs.next()) {
                            PreparedStatement statement = co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD treasureKeys INTEGER DEFAULT 0 NOT NULL");
                            statement.executeUpdate();
                        }

                        log("initial SQLLoader to reduce lag when table is large");
                        sqlloader = new SQLLoaderManager();
                    } catch (Exception e) {

                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getConsoleSender().sendMessage("§c§lUltra Cosmetics >>> Could not connect to MySQL server!");
                        Bukkit.getLogger().info("");
                        Bukkit.getConsoleSender().sendMessage("§c§lError:");
                        e.printStackTrace();

                    }
                }
            }, 0, 24000);
        }
    }

    /**
     * Setup default Cosmetics config.
     */
    private void setupCosmeticsConfigs() {
        for (Category category : Category.values()) {
            config.addDefault("Categories-Enabled." + category.getConfigPath(), true);
            config.addDefault("Categories." + category.getConfigPath() + ".Go-Back-Arrow", true, "Want Go back To Menu Item in that menu?");
        }
        config.addDefault("TreasureChests.Loots.Emotes.Enabled", true);
        config.addDefault("TreasureChests.Loots.Emotes.Chance", 5);
        config.addDefault("TreasureChests.Loots.Emotes.Message.enabled", true);
        config.addDefault("TreasureChests.Loots.Emotes.Message.message", "%prefix% &6&l%name% found rare %emote%");

        // CALL STATIC BLOCK.
        // Gadget
        // Mount
        // Effect
        // Pet
        // Morph
        // Hat
        // Suit
        SuitType.ASTRONAUT.getConfigName();
        // Emote

        for (GadgetType gadgetType : GadgetType.values()) {
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Affect-Players", true, "Should it affect players? (Velocity, etc.)");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Enabled", true, "if true, the gadget will be enabled.");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Show-Description", true, "if true, the description of gadget will be showed.");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
            if (gadgetType == GadgetType.PAINTBALLGUN) {
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Block-Type", "STAINED_CLAY", "With what block will it paint?");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Particle.Enabled", false, "Should it display particles?");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Particle.Effect", "FIREWORKS_SPARK", "what particles? (List: http://pastebin.com/CVKkufck)");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Radius", 2, "The radius of painting.");
                List<String> blackListedBlocks = new ArrayList<>();
                blackListedBlocks.add("REDSTONE_BLOCK");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".BlackList", blackListedBlocks, "A list of the blocks that", "can't be painted.");
            }
            if (ammoEnabled) {
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Enabled", true, "You want this gadget to need ammo?");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Price", 500, "What price for the ammo?");
                config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Result-Amount", 20, "And how much ammo is given", "when bought?");
            }
        }

        for (MountType mountType : MountType.values()) {
            config.addDefault("Mounts." + mountType.getConfigName() + ".Enabled", true, "if true, the mount will be enabled.");
            config.addDefault("Mounts." + mountType.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Mounts." + mountType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (ParticleEffectType particleEffect : ParticleEffectType.values()) {
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Enabled", true, "if true, the effect will be enabled.");
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (PetType pet : PetType.values()) {
            config.addDefault("Pets." + pet.getConfigName() + ".Enabled", true, "if true, the pet will be enabled.");
            config.addDefault("Pets." + pet.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Pets." + pet.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }
        if (enabledCategories.contains(Category.MORPHS))
            for (MorphType morphType : MorphType.values()) {
                config.addDefault("Morphs." + morphType.getConfigName() + ".Enabled", true, "if true, the morph will be enabled.");
                config.addDefault("Morphs." + morphType.getConfigName() + ".Show-Description", true, "if true, the description of this morph will be showed.");
                config.addDefault("Morphs." + morphType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
            }
        for (Hat hat : Hat.values()) {
            config.addDefault("Hats." + hat.getConfigName() + ".Enabled", true, "if true, the hat will be enabled.");
            config.addDefault("Hats." + hat.getConfigName() + ".Show-Description", true, "if true, the description of this hat will be showed.");
            config.addDefault("Hats." + hat.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }
        for (CosmeticType cosmeticType : SuitType.values()) {
            SuitType suit = ((SuitType) cosmeticType);
            config.addDefault("Suits." + suit.getConfigName() + ".Enabled", true, "if true, the suit will be enabled.");
            config.addDefault("Suits." + suit.getConfigName() + ".Show-Description", true, "if true, the description of this suit will be showed.");
            config.addDefault("Suits." + suit.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (EmoteType emoteType : EmoteType.values()) {
            config.addDefault("Emotes." + emoteType.getConfigName() + ".Enabled", true, "if true, the mount will be enabled.");
            config.addDefault("Emotes." + emoteType.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Emotes." + emoteType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (GadgetType gadgetType : GadgetType.values())
            if (gadgetType.isEnabled())
                GadgetType.gadgetTypes.add(gadgetType);
        for (MountType mountType : MountType.values())
            if (mountType.isEnabled())
                MountType.mountTypes.add(mountType);
        for (ParticleEffectType particleEffectType : ParticleEffectType.values())
            if (particleEffectType.isEnabled())
                ParticleEffectType.enabled.add(particleEffectType);
        for (PetType petType : PetType.values())
            if (petType.isEnabled())
                PetType.enabled.add(petType);
        if (enabledCategories.contains(Category.MORPHS))
            for (MorphType morphType : MorphType.values())
                if (morphType.isEnabled())
                    MorphType.enabled.add(morphType);
        for (Hat hat : Hat.values())
            if (hat.isEnabled())
                Hat.enabled.add(hat);
        SuitType.checkEnabled();
        for (EmoteType emoteType : EmoteType.values())
            if (emoteType.isEnabled())
                EmoteType.ENABLED.add(emoteType);
    }

    /**
     * Check Treasure Chests requirements.
     */
    private void checkTreasureChests() {
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

    /**
     * Setups Vault.
     *
     * @return {@code true} if it could be set up, otherwise {@code false}.
     */
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
            economy = economyProvider.getProvider();

        vaultLoaded = economy != null;

        return (economy != null);
    }

    /**
     * @return Entity Util class.
     */
    public IEntityUtil getEntityUtil() {
        return versionManager.getEntityUtil();
    }

    /**
     * Called when plugin disables.
     */
    @Override
    public void onDisable() {
        Pet.purgeNames();

        if (morphMenuListener != null)
            ((MorphManager) morphMenuListener).dispose();

        if (playerManager != null)
            playerManager.dispose();
        try {
            BlockUtils.forceRestore();
            versionManager.getModule().disable();
        } catch (Exception e) {
        }
    }

    /**
     * @return Command Manager.
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Checks for new update.
     */
    private void checkForUpdate() {
        String currentVersion = UltraCosmetics.getInstance().getDescription().getVersion()
                .replace("Beta ", "")
                .replace("Pre-", "")
                .replace("Release ", "")
                .replace("Hype Update (", "")
                .replace(")", "");
        lastVersion = getLastVersion();
        if (lastVersion != null) {
            int i = new Version(currentVersion).compareTo(new Version(lastVersion));
            outdated = i == -1;
            if (lastVersion.equalsIgnoreCase("1.7.1") && currentVersion.startsWith("1.1"))
                outdated = false;
        } else
            outdated = false;
    }

    public static void openMainMenuFromOther(Player whoClicked) {
        if (customCommandBackArrow)
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), customBackMenuCommand.replace("{player}", whoClicked.getName()));
        else
            MainMenuManager.openMenu(whoClicked);
    }
}
