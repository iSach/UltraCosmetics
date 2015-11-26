package be.isach.ultracosmetics;

import be.isach.ultracosmetics.commands.UltraCosmeticsCommand;
import be.isach.ultracosmetics.commands.UltraCosmeticsTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.*;
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
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MetricsLite;
import be.isach.ultracosmetics.util.SQLUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
public class Core extends JavaPlugin {

    public static ArrayList<Entity> noFallDamageEntities = new ArrayList<>();
    public static ArrayList<GadgetDiscoBall> discoBalls = new ArrayList<>();
    public static ArrayList<GadgetExplosiveSheep> explosiveSheep = new ArrayList<>();
    public static HashMap<Player, HashMap<Gadget.GadgetType, Double>> countdownMap = new HashMap<>();

    private static List<CustomPlayer> customPlayers = new ArrayList<>();

    private static List<Gadget> gadgetList = new ArrayList<>();
    private static List<ParticleEffect> particleEffectList = new ArrayList<>();
    private static List<Mount> mountList = new ArrayList<>();
    private static List<Pet> petList = new ArrayList<>();
    private static List<TreasureChest> treasureChestList = new ArrayList<>();
    private static List<Morph> morphList = new ArrayList<>();
    private static List<Hat> hatList = new ArrayList<>();

    public static Boolean placeHolderColor;

    private static boolean nbsapiEnabled = false;
    private static boolean ammoEnabled = false;
    private static boolean fileStorage = true;
    private static boolean treasureChests = false;

    static boolean debug = false;

    public static List<Category> enabledCategories = new ArrayList<>();

    public static Economy economy = null;

    private MySQLConnection sql;
    public Connection co; // SQL Connection.
    public Table table; // SQL Table.
    public static SQLUtils sqlUtils; // SQL Utils.

    private static Core core;

    public static boolean outdated;
    public static String lastVersion;

    @Override
    public void onEnable() {
        if (!getServer().getVersion().contains("1.8.8")) {
            System.out.println("----------------------------\n\nUltraCosmetics requires Spigot 1.8.8 to work!\n\n----------------------------");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        core = this;

        CustomEntities.registerEntities();

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            System.out.println("Couldn't send data to Metrics :(");
        }

        if (getDescription().getVersion().startsWith("Pre")) {
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("  §4§lUNSTABLE VERSION!");
            getServer().getConsoleSender().sendMessage("  §4§lNo support accepted for this version!");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
            debug = true;
        }

        if (Bukkit.getPluginManager().getPlugin("NoteBlockAPI") != null) {
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("  §4§lNoteBlockAPI found! Using it!");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
            nbsapiEnabled = true;
        }

        new MessageManager();
        registerListener(new PlayerListener());

        setupDefaultConfig();

        registerPets();
        registerMounts();
        registerParticleEffects();

        hatList.addAll(Arrays.asList(Hat.values()));

        // Register the command
        getCommand("ultracosmetics").setExecutor(new UltraCosmeticsCommand());
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("uc");
        getCommand("ultracosmetics").setAliases(arrayList);
        getCommand("ultracosmetics").setTabCompleter(new UltraCosmeticsTabCompleter());

        String s = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.System"));
        fileStorage = s.equalsIgnoreCase("file");
        placeHolderColor = SettingsManager.getConfig().get("Chat-Cosmetic-PlaceHolder-Color");
        registerGadgets();
        ammoEnabled = SettingsManager.getConfig().get("Ammo-System-For-Gadgets.Enabled");

        for (Category c : Category.values()) {
            if (c == Category.MORPHS)
                if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
                    continue;
            if (c.isEnabled())
                enabledCategories.add(c);
        }

        tryRegisterMorphs();

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

        setupGadgetsConfig();

        if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            Bukkit.getLogger().info("");
            Bukkit.getConsoleSender().sendMessage("§c§lMorphs require Lib's Disguises!");
            Bukkit.getLogger().info("");
            Bukkit.getConsoleSender().sendMessage("§c§lMorphs are disabling..");
            Bukkit.getLogger().info("");

        }

        if (ammoEnabled) {
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                Bukkit.getLogger().info("");
                Bukkit.getConsoleSender().sendMessage("§c§lVault not found!");
                Bukkit.getLogger().info("");
                Bukkit.getConsoleSender().sendMessage("§c§lServer shutting down, please install Vault to use Ammo System!");
                Bukkit.getLogger().info("");
                Bukkit.shutdown();
                return;
            }
        }

        if (ammoEnabled
                || SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled"))
            setupEconomy();

        startMySQL();
        initPlayers();

        initCooldownManager();

        if (nbsapiEnabled) {
            File folder = new File(getDataFolder().getPath() + "/songs/");
            if ((!folder.exists()) || (folder.listFiles().length <= 0)) {
                saveResource("songs/GetLucky.nbs", true);
            }
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

    }

    private void initCooldownManager() {
        final BukkitRunnable countdownRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Iterator<Entity> iter = noFallDamageEntities.iterator();
                    while (iter.hasNext()) {
                        Entity ent = iter.next();
                        if (ent.isOnGround())
                            iter.remove();
                    }
                    Iterator<CustomPlayer> customPlayerIterator = customPlayers.iterator();
                    while (customPlayerIterator.hasNext()) {
                        CustomPlayer customPlayer = customPlayerIterator.next();
                        if (customPlayer.getPlayer() == null)
                            customPlayerIterator.remove();
                    }
                    Iterator<Player> playerIterator = countdownMap.keySet().iterator();
                    while (playerIterator.hasNext()) {
                        Player p = playerIterator.next();
                        try {
                            if (!((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(p.getWorld().getName())) {
                                CustomPlayer customPlayer = Core.getCustomPlayer(p);
                                customPlayer.clear();
                                customPlayer.removeChest();
                                customPlayer = null;
                            }
                        } catch (Exception exc) {
                        }
                        if (countdownMap.get(p) != null) {
                            Iterator it = countdownMap.get(p).entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                double timeLeft = (double) pair.getValue();
                                Gadget.GadgetType type = (Gadget.GadgetType) pair.getKey();
                                if (timeLeft > 0.1)
                                    pair.setValue(timeLeft - 0.05);
                                else
                                    it.remove();

                            }
                        }
                    }
                } catch (Exception exc) {
                }
            }
        };
        countdownRunnable.runTaskTimerAsynchronously(Core.getPlugin(), 0, 1);
    }

    private void initPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            customPlayers.add(new CustomPlayer(p.getUniqueId()));
            if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(p.getWorld().getName())) {
                int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
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
                        for (Gadget gadget : gadgetList) {
                            DatabaseMetaData md = co.getMetaData();
                            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", gadget.getType().toString().toLowerCase());
                            if (!rs.next()) {
                                PreparedStatement statement = co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD " + gadget.getType().toString().toLowerCase() + " INTEGER DEFAULT 0 not NULL");
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

    private void setupGadgetsConfig() {
        for (Gadget gadget : gadgetList) {
            addDefault("Gadgets." + gadget.getType().configName + ".Enabled", true);
            addDefault("Gadgets." + gadget.getType().configName + ".Show-Description", true);
            addDefault("Gadgets." + gadget.getType().configName + ".Can-Be-Found-In-Treasure-Chests", true);
            if (gadget.getType() == Gadget.GadgetType.PAINTBALLGUN) {
                addDefault("Gadgets." + gadget.getType().configName + ".Block-Type", "STAINED_CLAY");
                addDefault("Gadgets." + gadget.getType().configName + ".Particle.Enabled", false);
                addDefault("Gadgets." + gadget.getType().configName + ".Particle.Effect", "fireworksSpark");
                addDefault("Gadgets." + gadget.getType().configName + ".Radius", 2);
                List<String> blackListedBlocks = new ArrayList<>();
                blackListedBlocks.add("REDSTONE_BLOCK");
                addDefault("Gadgets." + gadget.getType().configName + ".BlackList", blackListedBlocks);
            }
            if (ammoEnabled) {
                addDefault("Gadgets." + gadget.getType().configName + ".Ammo.Enabled", true);
                addDefault("Gadgets." + gadget.getType().configName + ".Ammo.Price", 500);
                addDefault("Gadgets." + gadget.getType().configName + ".Ammo.Result-Amount", 20);
            }
        }

        for (Mount mount : mountList) {
            addDefault("Mounts." + mount.getConfigName() + ".Enabled", true);
            addDefault("Mounts." + mount.getConfigName() + ".Show-Description", true);
            addDefault("Mounts." + mount.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true);
        }

        for (ParticleEffect particleEffect : particleEffectList) {
            addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Enabled", true);
            addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Show-Description", true);
            addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true);
        }

        for (Pet pet : petList) {
            addDefault("Pets." + pet.getConfigName() + ".Enabled", true);
            addDefault("Pets." + pet.getConfigName() + ".Show-Description", true);
            addDefault("Pets." + pet.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true);
        }

        for (Morph morph : morphList) {
            addDefault("Morphs." + morph.getConfigName() + ".Enabled", true);
            addDefault("Morphs." + morph.getConfigName() + ".Show-Description", true);
            addDefault("Morphs." + morph.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true);
        }

        for (Hat hat : hatList) {
            addDefault("Hats." + hat.getConfigName() + ".Enabled", true);
            addDefault("Hats." + hat.getConfigName() + ".Show-Description", true);
            addDefault("Hats." + hat.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true);
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
    }

    private void tryRegisterMorphs() {
        if (Category.MORPHS.isEnabled() && Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            morphList.add(new MorphBat(null));
            morphList.add(new MorphBlaze(null));
            morphList.add(new MorphSlime(null));
            morphList.add(new MorphEnderman(null));
            morphList.add(new MorphChicken(null));
            morphList.add(new MorphPig(null));
            morphList.add(new MorphCreeper(null));
            morphList.add(new MorphWitherSkeleton(null));
        }
    }

    private void checkTreasureChests() {
        if (SettingsManager.getConfig().get("TreasureChests.Enabled")) {
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

    private void registerGadgets() {
        for (Gadget.GadgetType gadgetType : Gadget.GadgetType.values())
            addDefault("Gadgets." + gadgetType.getConfigName() + ".Affect-Players", true);

        // Add gadgets.
        gadgetList.add(new GadgetPaintballGun(null));
        gadgetList.add(new GadgetBatBlaster(null));
        gadgetList.add(new GadgetChickenator(null));
        gadgetList.add(new GadgetMelonThrower(null));
        gadgetList.add(new GadgetEtherealPearl(null));
        gadgetList.add(new GadgetDiscoBall(null));
        gadgetList.add(new GadgetColorBomb(null));
        gadgetList.add(new GadgetFleshHook(null));
        gadgetList.add(new GadgetPortalGun(null));
        gadgetList.add(new GadgetBlizzardBlaster(null));
        gadgetList.add(new GadgetThorHammer(null));
        gadgetList.add(new GadgetSmashDown(null));
        gadgetList.add(new GadgetExplosiveSheep(null));
        gadgetList.add(new GadgetAntiGravity(null));
        gadgetList.add(new GadgetTsunami(null));
        gadgetList.add(new GadgetRocket(null));
        gadgetList.add(new GadgetBlackHole(null));
        gadgetList.add(new GadgetTNT(null));
        gadgetList.add(new GadgetFunGun(null));
        gadgetList.add(new GadgetQuakeGun(null));
        gadgetList.add(new GadgetParachute(null));
        gadgetList.add(new GadgetGhostParty(null));
        gadgetList.add(new GadgetFirework(null));
    }

    private void addDefault(String path, Object value) {
        SettingsManager.getConfig().addDefault(path, value);
    }

    private void setupDefaultConfig() {
        List<String> enabledWorlds = new ArrayList<>();

        for (World world : Bukkit.getWorlds())
            enabledWorlds.add(world.getName());

        addDefault("Enabled-Worlds", enabledWorlds);

        addDefault("Check-For-Updates", true);

        addDefault("Categories-Enabled.Gadgets", true);
        addDefault("Categories-Enabled.Particle-Effects", true);
        addDefault("Categories-Enabled.Mounts", true);
        addDefault("Categories-Enabled.Pets", true);
        addDefault("Categories-Enabled.Morphs", true);
        addDefault("Categories-Enabled.Hats", true);

        addDefault("Categories.Gadgets.Main-Menu-Item", "341:0");
        addDefault("Categories.Gadgets.Go-Back-Arrow", true);
        addDefault("Categories.Particle-Effects.Main-Menu-Item", "362:0");
        addDefault("Categories.Particle-Effects.Go-Back-Arrow", true);
        addDefault("Categories.Mounts.Main-Menu-Item", "329:0");
        addDefault("Categories.Mounts.Go-Back-Arrow", true);
        addDefault("Categories.Pets.Main-Menu-Item", "383:0");
        addDefault("Categories.Pets.Go-Back-Arrow", true);
        addDefault("Categories.Morphs.Main-Menu-Item", "397:4");
        addDefault("Categories.Morphs.Go-Back-Arrow", true);
        addDefault("Categories.Hats.Main-Menu-Item", "310:0");
        addDefault("Categories.Hats.Go-Back-Arrow", true);

        addDefault("TreasureChests.Enabled", false);
        addDefault("TreasureChests.Key-Price", 1000);

        addDefault("TreasureChests.Loots.Money.Enabled", true);
        addDefault("TreasureChests.Loots.Money.Max", 100);
        addDefault("TreasureChests.Loots.Money.Chance", 20);
        addDefault("TreasureChests.Loots.Money.Message.enabled", false);
        addDefault("TreasureChests.Loots.Money.Message.message", "%prefix% §6§l%name% found %money%$");
        addDefault("TreasureChests.Loots.Gadgets-Ammo.Enabled", true);
        addDefault("TreasureChests.Loots.Gadgets-Ammo.Min", 20);
        addDefault("TreasureChests.Loots.Gadgets-Ammo.Max", 100);
        addDefault("TreasureChests.Loots.Gadgets-Ammo.Chance", 60);
        addDefault("TreasureChests.Loots.Gadgets-Ammo.Message.enabled", false);
        addDefault("TreasureChests.Loots.Gadgets-Ammo.Message.message", "%prefix% §6§l%name% found %ammo% %gadget% ammo");
        addDefault("TreasureChests.Loots.Mounts.Enabled", true);
        addDefault("TreasureChests.Loots.Mounts.Chance", 10);
        addDefault("TreasureChests.Loots.Mounts.Message.enabled", false);
        addDefault("TreasureChests.Loots.Mounts.Message.message", "%prefix% §6§l%name% found rare %mount%");
        addDefault("TreasureChests.Loots.Pets.Enabled", true);
        addDefault("TreasureChests.Loots.Pets.Chance", 10);
        addDefault("TreasureChests.Loots.Pets.Message.enabled", false);
        addDefault("TreasureChests.Loots.Pets.Message.message", "%prefix% §6§l%name% found rare %pet%");
        addDefault("TreasureChests.Loots.Morphs.Enabled", true);
        addDefault("TreasureChests.Loots.Morphs.Chance", 4);
        addDefault("TreasureChests.Loots.Morphs.Message.enabled", true);
        addDefault("TreasureChests.Loots.Morphs.Message.message", "%prefix% §6§l%name% found legendary %morph%");
        addDefault("TreasureChests.Loots.Effects.Enabled", true);
        addDefault("TreasureChests.Loots.Effects.Chance", 4);
        addDefault("TreasureChests.Loots.Effects.Message.enabled", true);
        addDefault("TreasureChests.Loots.Effects.Message.message", "%prefix% §6§l%name% found legendary %effect%");
        addDefault("TreasureChests.Loots.Hats.Enabled", true);
        addDefault("TreasureChests.Loots.Hats.Chance", 30);
        addDefault("TreasureChests.Loots.Hats.Message.enabled", false);
        addDefault("TreasureChests.Loots.Hats.Message.message", "%prefix% §6§l%name% found rare %hat%");

        if (!SettingsManager.getConfig().fileConfiguration.contains("TreasureChests.Designs.Classic")) {
            addDefault("TreasureChests.Designs.Classic.center-block", "169:0");
            addDefault("TreasureChests.Designs.Classic.around-center", "5:0");
            addDefault("TreasureChests.Designs.Classic.third-blocks", "5:1");
            addDefault("TreasureChests.Designs.Classic.below-chests", "17:0");
            addDefault("TreasureChests.Designs.Classic.barriers", "85:0");
            addDefault("TreasureChests.Designs.Classic.chest-type", "NORMAL");
            addDefault("TreasureChests.Designs.Classic.effect", "FLAME");
            addDefault("TreasureChests.Designs.Modern.center-block", "169:0");
            addDefault("TreasureChests.Designs.Modern.around-center", "159:11");
            addDefault("TreasureChests.Designs.Modern.third-blocks", "155:0");
            addDefault("TreasureChests.Designs.Modern.below-chests", "159:11");
            addDefault("TreasureChests.Designs.Modern.barriers", "160:3");
            addDefault("TreasureChests.Designs.Modern.chest-type", "ENDER");
            addDefault("TreasureChests.Designs.Modern.effect", "COLOURED_DUST");
            addDefault("TreasureChests.Designs.Nether.center-block", "89:0");
            addDefault("TreasureChests.Designs.Nether.around-center", "88:0");
            addDefault("TreasureChests.Designs.Nether.third-blocks", "87:0");
            addDefault("TreasureChests.Designs.Nether.below-chests", "112:0");
            addDefault("TreasureChests.Designs.Nether.barriers", "113:0");
            addDefault("TreasureChests.Designs.Nether.chest-type", "TRAPPED");
            addDefault("TreasureChests.Designs.Nether.effect", "SMOKE");
        }

        addDefault("TreasureChests.Permission-Add-Command", "pex user %name% add %permission%");

        addDefault("Fill-Blank-Slots-With-Item.Enabled", false);
        addDefault("Fill-Blank-Slots-With-Item.Item", "160:15");

        addDefault("Pets-Rename.Enabled", false);
        addDefault("Pets-Rename.Permission-Required", false);
        addDefault("Pets-Rename.Requires-Money.Enabled", true);
        addDefault("Pets-Rename.Requires-Money.Price", 100);

        addDefault("Pets-Drop-Items", true);
        addDefault("Pets-Are-Babies", true);
        addDefault("Mounts-Block-Trails", true);

        // Set config things.
        addDefault("Ammo-System-For-Gadgets.Enabled", false);
        addDefault("Ammo-System-For-Gadgets.System", "file");
        addDefault("Ammo-System-For-Gadgets.MySQL.hostname", "localhost");
        addDefault("Ammo-System-For-Gadgets.MySQL.username", "root");
        addDefault("Ammo-System-For-Gadgets.MySQL.password", "password");
        addDefault("Ammo-System-For-Gadgets.MySQL.port", "3306");
        addDefault("Ammo-System-For-Gadgets.MySQL.database", "UltraCosmetics");
        addDefault("Menu-Item.Give-On-Join", true);
        addDefault("Menu-Item.Give-On-Respawn", true);
        addDefault("Menu-Item.Slot", 3);
        addDefault("Menu-Item.Type", "ENDER_CHEST");
        addDefault("Menu-Item.Data", 0);
        addDefault("Menu-Item.Displayname", "&6&lCosmetics");
        addDefault("No-Permission.Show-In-Lore", true);
        addDefault("No-Permission.Lore-Message-Yes", "&o&7Permission: &a&lYes!");
        addDefault("No-Permission.Lore-Message-No", "&o&7Permission: &4&lNo!");
        addDefault("No-Permission.Dont-Show-Item", false);
        addDefault("No-Permission.Custom-Item.enabled", false);
        addDefault("No-Permission.Custom-Item.Type", "INK_SACK");
        addDefault("No-Permission.Custom-Item.Data", 8);
        addDefault("No-Permission.Custom-Item.Name", "&c&lNo Permission");
        addDefault("Disabled-Items.Show-Custom-Disabled-Item", false);
        addDefault("Disabled-Items.Custom-Disabled-Item.Type", "INK_SACK");
        addDefault("Disabled-Items.Custom-Disabled-Item.Data", 8);
        addDefault("Disabled-Items.Custom-Disabled-Item.Name", "&c&lDisabled");

        addDefault("Chat-Cosmetic-PlaceHolder-Color", true);

        addDefault("Gadget-Slot", 4);
        addDefault("Remove-Gadget-With-Drop", false);
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
    }

    @Override
    public void onDisable() {
        for (CustomPlayer cp : customPlayers) {
            if (cp.currentTreasureChest != null)
                cp.currentTreasureChest.forceOpen(0);
            cp.clear();
            int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
            if (cp.getPlayer().getInventory().getItem(slot) != null
                    && cp.getPlayer().getInventory().getItem(slot).hasItemMeta()
                    && cp.getPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                    && cp.getPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
                cp.getPlayer().getInventory().setItem(slot, null);
            }
        }
        Core.customPlayers.clear();
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

    public static List<Gadget> getGadgets() {
        return gadgetList;
    }

    public static List<CustomPlayer> getCustomPlayers() {
        return customPlayers;
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
        try {
            for (CustomPlayer cp : customPlayers)
                if (cp.getPlayer().getName().equals(player.getName()))
                    return cp;
            return new CustomPlayer(player.getUniqueId());
        } catch (NullPointerException exception) {
            CustomPlayer p = new CustomPlayer(player.getUniqueId());
            customPlayers.add(p);
            return p;
        }
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
        String filtered = menuName;
        Character[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'o', 'n', 'm', 'r', 'k'};
        for (Character character : chars)
            menuName = menuName.replace("§" + character, "");
        return menuName;
    }

}
