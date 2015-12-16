package be.isach.ultracosmetics;

import be.isach.ultracosmetics.commands.UltraCosmeticsCommand;
import be.isach.ultracosmetics.commands.UltraCosmeticsTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetDiscoBall;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetExplosiveSheep;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.*;
import be.isach.ultracosmetics.cosmetics.mounts.*;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.CustomEntities;
import be.isach.ultracosmetics.cosmetics.particleeffects.*;
import be.isach.ultracosmetics.cosmetics.pets.*;
import be.isach.ultracosmetics.cosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.listeners.PlayerListener;
import be.isach.ultracosmetics.manager.*;
import be.isach.ultracosmetics.mysql.MySQLConnection;
import be.isach.ultracosmetics.mysql.Table;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.run.InvalidWorldManager;
import be.isach.ultracosmetics.util.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
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
public class Core extends JavaPlugin {

    public static List<GadgetDiscoBall> discoBalls = Collections.synchronizedList(new ArrayList<GadgetDiscoBall>());
    public static List<GadgetExplosiveSheep> explosiveSheep = Collections.synchronizedList(new ArrayList<GadgetExplosiveSheep>());

    private static List<ParticleEffect> particleEffectList = Collections.synchronizedList(new ArrayList<ParticleEffect>());
    private static List<Mount> mountList = Collections.synchronizedList(new ArrayList<Mount>());
    private static List<Pet> petList = Collections.synchronizedList(new ArrayList<Pet>());
    private static List<TreasureChest> treasureChestList = Collections.synchronizedList(new ArrayList<TreasureChest>());
    private static List<Morph> morphList = Collections.synchronizedList(new ArrayList<Morph>());
    private static List<Hat> hatList = Collections.synchronizedList(new ArrayList<Hat>());

    public static Boolean placeHolderColor;

    private static boolean nbsapiEnabled;
    private static boolean ammoEnabled;
    private static boolean fileStorage = true;
    private static boolean treasureChests;
    public static boolean cooldownInBar;

    static boolean debug = false;

    public static List<Category> enabledCategories = new ArrayList<>();

    public static CustomConfiguration config;
    public static File file;

    public static Economy economy = null;

    private MySQLConnection sql;
    public Connection co; // SQL Connection.
    public Table table; // SQL Table.
    public static SQLUtils sqlUtils; // SQL Utils.

    private static Core core;

    public static boolean outdated;
    public static String lastVersion;

    private static PlayerManager playerManager;

    @Override
    public void onEnable() {
        if (!getServer().getVersion().contains("1.8.8")) {
            System.out.println("----------------------------\n\nUltraCosmetics requires Spigot 1.8.8 to work!\n\n----------------------------");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerManager = new PlayerManager();

        log("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        log("UltraCosmetics v" + getDescription().getVersion() + " is being loaded...");

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
            copy(getResource("config.yml"), file);
            log("Config file doesn't exist yet.");
            log("Creating Config File and loading it.");
        }

        config = CustomConfiguration.loadConfiguration(file);

        List<String> enabledWorlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds())
            enabledWorlds.add(world.getName());
        config.addDefault("Enabled-Worlds", enabledWorlds, "List of the worlds", "where cosmetics are enabled!");

        config.addDefault("Categories.Gadgets.Cooldown-In-ActionBar", true, "You wanna show the cooldown of", "current gadget in action bar?");

        saveConfig();

        log("Configuration loaded.");
        log("");

        core = this;

        log("Registering Custom Entities...");
        CustomEntities.registerEntities();
        log("Custom Entities registered.");
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
            nbsapiEnabled = true;
        }


        log("");
        log("Registering Messages...");
        new MessageManager();
        log("Messages registered.");
        log("");

        registerListener(new PlayerListener());

        log("Registering pets...");
        registerPets();
        log("Pets registered.");
        log("");
        log("Registering Mounts...");
        registerMounts();
        log("Mounts registered.");
        log("");
        log("Registering Particle Effects...");
        registerParticleEffects();
        log("Particle Effects registered.");
        log("");

        log("Loading hats...");
        hatList.addAll(Arrays.asList(Hat.values()));
        log("Hats loaded.");

        log("");
        log("Registering commands...");
        // Register the command
        getCommand("ultracosmetics").setExecutor(new UltraCosmeticsCommand());
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("uc");
        getCommand("ultracosmetics").setAliases(arrayList);
        getCommand("ultracosmetics").setTabCompleter(new UltraCosmeticsTabCompleter());
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

        log("Trying to register Morphs...");
        tryToRegisterMorphs();

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
            }
        }.run();

        log("Registering Gadgets...");
        setupCosmeticsConfigs();
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Gadgets Registered.");

        if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            log("");
            log("§c§lMorphs require Lib's Disguises!");
            log("");
            log("§c§lMorphs are disabling..");
            log("");
        }
        if (ammoEnabled
                || (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled") && SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled"))) {
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                log("");
                log("§c§lVault not found!");
                log("");
                log("§c§lServer shutting down, please install Vault to use Ammo System!");
                log("");
                Bukkit.shutdown();
                return;
            }
        }

        if (ammoEnabled
                || SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled"))
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

        if (nbsapiEnabled) {
            File folder = new File(getDataFolder().getPath() + "/songs/");
            if ((!folder.exists()) || (folder.listFiles().length <= 0))
                saveResource("songs/GetLucky.nbs", true);
            saveResource("songs/NyanCat.nbs", true);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                if (outdated) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.isOp())
                            p.sendMessage("§l§oUltraCosmetics > §c§lAn update is available: " + lastVersion);
                    }
                }
            }
        }, 20);

        log("");
        log("Registering listeners...");
        registerListener(new MainMenuManager());
        registerListener(new GadgetManager());
        registerListener(new PetManager());
        registerListener(new MountManager());
        registerListener(new ParticleEffectManager());
        registerListener(new PetManager());
        registerListener(new HatManager());
        registerListener(new TreasureChestManager());
        if (Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
            registerListener(new MorphManager());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Listeners registered.");
        log("");
        log("");
        log("UltraCosmetics finished loading and is now enabled!");
        log("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }

    private void log(Object object) {
        System.out.println("UltraCosmetics -> " + object.toString());
    }

    @Override
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerManager.create(p);
            if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(p.getWorld().getName())) {
                int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
                if (p.getInventory().getItem(slot) != null) {
                    if (p.getInventory().getItem(slot).hasItemMeta()
                            && p.getInventory().getItem(slot).getItemMeta().hasDisplayName()
                            && p.getInventory().getItem(slot).getItemMeta().getDisplayName().equalsIgnoreCase((String) SettingsManager.getConfig().get("Menu-Item.Displayname"))) {
                        p.getInventory().remove(slot);
                        p.getInventory().setItem(slot, null);
                    }
                    p.getWorld().dropItemNaturally(p.getLocation(), p.getInventory().getItem(slot));
                    p.getInventory().remove(slot);
                }
                String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
                byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
                p.getInventory().setItem(slot, ItemFactory.create(material, data, name));
            }
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);

            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getConsoleSender().sendMessage("§b§lUltraCosmetics >>> Successfully connected to MySQL server! :)");
                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
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

                    } catch (Exception e) {

                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getConsoleSender().sendMessage("§c§lUltra Cosmetics >>> Could not connect to MySQL server!");
                        Bukkit.getLogger().info("");
                        Bukkit.getConsoleSender().sendMessage("§c§lError:");
                        e.printStackTrace();
                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.getConsoleSender().sendMessage("§c§lServer shutting down, please check the MySQL info!");
                        Bukkit.getLogger().info("");
                        Bukkit.getLogger().info("");
                        Bukkit.shutdown();

                    }
                }
            }, 0, 24000);
        }
    }

    private void setupCosmeticsConfigs() {
        for (GadgetType gadgetType : GadgetType.values()) {
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Affect-Players", true, "Should it affect players? (Velocity, etc.)");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Enabled", true, "if true, the gadget will be enabled.");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Show-Description", true, "if true, the description of gadget will be showed.");
            config.addDefault("Gadgets." + gadgetType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
            if (gadgetType == GadgetType.PAINTBALL_GUN) {
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

        for (Mount mount : mountList) {
            config.addDefault("Mounts." + mount.getConfigName() + ".Enabled", true, "if true, the mount will be enabled.");
            config.addDefault("Mounts." + mount.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Mounts." + mount.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (ParticleEffect particleEffect : particleEffectList) {
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Enabled", true, "if true, the effect will be enabled.");
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (Pet pet : petList) {
            config.addDefault("Pets." + pet.getConfigName() + ".Enabled", true, "if true, the pet will be enabled.");
            config.addDefault("Pets." + pet.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
            config.addDefault("Pets." + pet.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (Morph morph : morphList) {
            config.addDefault("Morphs." + morph.getConfigName() + ".Enabled", true, "if true, the morph will be enabled.");
            config.addDefault("Morphs." + morph.getConfigName() + ".Show-Description", true, "if true, the description of this morph will be showed.");
            config.addDefault("Morphs." + morph.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }

        for (Hat hat : hatList) {
            config.addDefault("Hats." + hat.getConfigName() + ".Enabled", true, "if true, the hat will be enabled.");
            config.addDefault("Hats." + hat.getConfigName() + ".Show-Description", true, "if true, the description of this hat will be showed.");
            config.addDefault("Hats." + hat.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerParticleEffects() {
        particleEffectList.add(new ParticleEffectRainCloud(null));
        particleEffectList.add(new ParticleEffectSnowCloud(null));
        particleEffectList.add(new ParticleEffectBloodHelix(null));
        particleEffectList.add(new ParticleEffectFrostLord(null));
        particleEffectList.add(new ParticleEffectFlameRings(null));
        particleEffectList.add(new ParticleEffectInLove(null));
        particleEffectList.add(new ParticleEffectGreenSparks(null));
        particleEffectList.add(new ParticleEffectFrozenWalk(null));
        particleEffectList.add(new ParticleEffectMusic(null));
        particleEffectList.add(new ParticleEffectEnchanted(null));
        particleEffectList.add(new ParticleEffectInferno(null));
        particleEffectList.add(new ParticleEffectAngelWings(null));
        particleEffectList.add(new ParticleEffectSuperHero(null));
        particleEffectList.add(new ParticleEffectSantaHat(null));
    }

    private void tryToRegisterMorphs() {
        if (Category.MORPHS.isEnabled() && Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            morphList.add(new MorphBat(null));
            morphList.add(new MorphBlaze(null));
            morphList.add(new MorphSlime(null));
            morphList.add(new MorphEnderman(null));
            morphList.add(new MorphChicken(null));
            morphList.add(new MorphPig(null));
            morphList.add(new MorphCreeper(null));
            morphList.add(new MorphWitherSkeleton(null));
            morphList.add(new MorphSnowman(null));
            log("Morphs successfully registered.");
            log("");
        } else {
            log("Morphs couldn't be registered.");
            log("Lib's Disguises must not be installed.");
            log("");
        }
    }

    private void checkTreasureChests() {
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Enabled")) {
            treasureChests = true;
            if (!ammoEnabled || !Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                Bukkit.getConsoleSender().sendMessage("§c§l-------------------------");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§lTreasure Chests require Vault and Ammo System Enabled!");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§lTreasure Chests are turning off...");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§l");
                Bukkit.getConsoleSender().sendMessage("§c§l-------------------------");
                treasureChests = false;
            }
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    private void registerPets() {
        petList.add(new PetPiggy(null));
        petList.add(new PetSheep(null));
        petList.add(new PetKitty(null));
        petList.add(new PetDog(null));
        petList.add(new PetChick(null));
        petList.add(new PetCow(null));
        petList.add(new PetEasterBunny(null));
        petList.add(new PetWither(null));
        petList.add(new PetPumpling(null));
        petList.add(new PetChristmasElf(null));
    }

    private void registerMounts() {
        mountList.add(new MountDruggedHorse(null));
        mountList.add(new MountEcologistHorse(null));
        mountList.add(new MountGlacialSteed(null));
        mountList.add(new MountInfernalHorror(null));
        mountList.add(new MountMountOfFire(null));
        mountList.add(new MountMountOfWater(null));
        mountList.add(new MountWalkingDead(null));
        mountList.add(new MountSnake(null));
        mountList.add(new MountNyanSheep(null));
        mountList.add(new MountDragon(null));
        mountList.add(new MountSkySquid(null));
        mountList.add(new MountSlime(null));
        mountList.add(new MountHypeCart(null));
        mountList.add(new MountSpider(null));
        mountList.add(new MountRudolph(null));
    }

    @Override
    public void onDisable() {
        playerManager.dispose();
        try {
            BlockUtils.forceRestore();
        } catch (Exception e) {
        }
        CustomEntities.unregisterEntities();
    }

    private void checkForUpdate() {
        String currentVersion = Core.getPlugin().getDescription().getVersion()
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

    public static Collection<CustomPlayer> getCustomPlayers() {
        return playerManager.getPlayers();
    }

    public static List<Pet> getPets() {
        return petList;
    }

    public static List<Mount> getMounts() {
        return mountList;
    }

    public static List<Hat> getHats() {
        return hatList;
    }

    public static List<Morph> getMorphs() {
        return morphList;
    }

    public static List<TreasureChest> getTreasureChests() {
        return treasureChestList;
    }

    public static List<ParticleEffect> getParticleEffects() {
        return particleEffectList;
    }

    public static boolean isAmmoEnabled() {
        return ammoEnabled;
    }

    public static boolean isNoteBlockAPIEnabled() {
        return nbsapiEnabled;
    }

    public static boolean usingFileStorage() {
        return fileStorage;
    }

    public static boolean treasureChestsEnabled() {
        return treasureChests;
    }

    public static boolean debug(Object message) {
        if (debug) Bukkit.broadcastMessage("§c§lUC-DEBUG> §f" + message.toString());
        return debug;
    }

    /**
     * Gets the UltraCosmetics Plugin Object.
     *
     * @return
     */
    public static Plugin getPlugin() {
        return core;
    }

    public static void registerListener(Listener listenerClass) {
        Bukkit.getPluginManager().registerEvents(listenerClass, getPlugin());
    }

    public static CustomPlayer getCustomPlayer(Player player) {
        return playerManager.getCustomPlayer(player);
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static String getLastVersion() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
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

    public static CharSequence filterColor(String menuName) {
        Character[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'o', 'n', 'm', 'r', 'k'};
        for (Character character : chars)
            menuName = menuName.replace("§" + character, "");
        return menuName;
    }

}
