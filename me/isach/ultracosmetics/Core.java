package me.isach.ultracosmetics;

import me.isach.ultracosmetics.commands.UltraCosmeticsCommand;
import me.isach.ultracosmetics.commands.UltraCosmeticsTabCompleter;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.*;
import me.isach.ultracosmetics.cosmetics.morphs.*;
import me.isach.ultracosmetics.cosmetics.mounts.*;
import me.isach.ultracosmetics.cosmetics.particleeffects.*;
import me.isach.ultracosmetics.cosmetics.pets.*;
import me.isach.ultracosmetics.cosmetics.treasurechests.*;
import me.isach.ultracosmetics.listeners.MenuListener;
import me.isach.ultracosmetics.listeners.MorphMenuListener;
import me.isach.ultracosmetics.listeners.PlayerListener;
import me.isach.ultracosmetics.mysql.MySQLConnection;
import me.isach.ultracosmetics.mysql.Table;
import me.isach.ultracosmetics.util.BlockUtils;
import me.isach.ultracosmetics.util.ItemFactory;
import me.isach.ultracosmetics.util.MetricsLite;
import me.isach.ultracosmetics.util.SQLUtils;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.Block;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
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

    public static Boolean placeHolderColor;

    private static boolean nbsapiEnabled = false;
    private static boolean ammoEnabled = false;
    private static boolean fileStorage = true;
    private static boolean treasureChests = false;

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

        core = this;

        String currentVersion = Core.getPlugin().getDescription().getVersion().replace("Beta ", "").replace("Pre-", "").replace("Release ", "");
        lastVersion = getLastVersion();
        int i = new Version(currentVersion).compareTo(new Version(lastVersion));
        outdated = i == -1;

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
            nbsapiEnabled = true;
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


        // Register Mounts
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

        // Register Particle Effects
        particleEffectList.add(new ParticleEffectRainCloud(null));
        particleEffectList.add(new ParticleEffectSnowCloud(null));
        particleEffectList.add(new ParticleEffectBloodHelix(null));
        particleEffectList.add(new ParticleEffectFrostLord(null));
        particleEffectList.add(new ParticleEffectFlameRings(null));
        particleEffectList.add(new ParticleEffectInLove(null));
        particleEffectList.add(new ParticleEffectGreenSparks(null));

        // Register Particle Effects
        petList.add(new PetPiggy(null));
        petList.add(new PetSheep(null));
        petList.add(new PetKitty(null));
        petList.add(new PetDog(null));
        petList.add(new PetChick(null));
        petList.add(new PetCow(null));
        petList.add(new PetEasterBunny(null));

        // Register Treasure Chests
        treasureChestList.add(new TreasureChestClassic(null));
        treasureChestList.add(new TreasureChestIce(null));
        treasureChestList.add(new TreasureChestNether(null));
        treasureChestList.add(new TreasureChestSea(null));
        treasureChestList.add(new TreasureChestClay(null));
        treasureChestList.add(new TreasureChestDesert(null));
        treasureChestList.add(new TreasureChestDirt(null));
        treasureChestList.add(new TreasureChestEnd(null));
        treasureChestList.add(new TreasureChestGlass(null));

        // Register the command
        getCommand("ultracosmetics").setExecutor(new UltraCosmeticsCommand());
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("uc");
        getCommand("ultracosmetics").setAliases(arrayList);
        getCommand("ultracosmetics").setTabCompleter(new UltraCosmeticsTabCompleter());

        List<String> disabledWorlds = new ArrayList<>();

        disabledWorlds.add("worldDisabled1");
        disabledWorlds.add("worldDisabled2");
        disabledWorlds.add("worldDisabled3");

        SettingsManager.getConfig().addDefault("Disabled-Worlds", disabledWorlds);

        SettingsManager.getConfig().addDefault("Categories-Enabled.Gadgets", true);
        SettingsManager.getConfig().addDefault("Categories-Enabled.Particle-Effects", true);
        SettingsManager.getConfig().addDefault("Categories-Enabled.Mounts", true);
        SettingsManager.getConfig().addDefault("Categories-Enabled.Pets", true);
        SettingsManager.getConfig().addDefault("Categories-Enabled.Morphs", true);

        SettingsManager.getConfig().addDefault("TreasureChests.Enabled", false);
        SettingsManager.getConfig().addDefault("TreasureChests.Key-Price", 1000);

        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Money.Enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Money.Max", 100);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Money.Chance", 20);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Money.Message.enabled", false);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Money.Message.message", "%prefix% §6§l%name% found %money%$");
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Gadgets-Ammo.Enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Gadgets-Ammo.Max", 100);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Gadgets-Ammo.Chance", 60);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Gadgets-Ammo.Message.enabled", false);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Gadgets-Ammo.Message.message", "%prefix% §6§l%name% found %ammo% %gadget% ammo");
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Mounts.Enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Mounts.Chance", 10);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Mounts.Message.enabled", false);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Mounts.Message.message", "%prefix% §6§l%name% found rare %mount%");
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Pets.Enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Pets.Chance", 10);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Pets.Message.enabled", false);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Pets.Message.message", "%prefix% §6§l%name% found rare %pet%");
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Morphs.Enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Morphs.Chance", 4);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Morphs.Message.enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Morphs.Message.message", "%prefix% §6§l%name% found legendary %morph%");
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Effects.Enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Effects.Chance", 4);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Effects.Message.enabled", true);
        SettingsManager.getConfig().addDefault("TreasureChests.Loots.Effects.Message.message", "%prefix% §6§l%name% found legendary %effect%");

        SettingsManager.getConfig().addDefault("TreasureChests.Permission-Add-Command", "pex user %name% add %permission%");

        SettingsManager.getConfig().addDefault("Pets-Rename.Enabled", false);
        SettingsManager.getConfig().addDefault("Pets-Rename.Permission-Required", false);
        SettingsManager.getConfig().addDefault("Pets-Rename.Requires-Money.Enabled", true);
        SettingsManager.getConfig().addDefault("Pets-Rename.Requires-Money.Price", 100);

        SettingsManager.getConfig().addDefault("Pets-Drop-Items", true);
        SettingsManager.getConfig().addDefault("Pets-Are-Babies", true);
        SettingsManager.getConfig().addDefault("Mounts-Block-Trails", true);

        // Set config things.
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.Enabled", false);
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.System", "file");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.hostname", "localhost");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.username", "root");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.password", "password");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.port", "3306");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.database", "UltraCosmetics");
        SettingsManager.getConfig().addDefault("Menu-Item.Give-On-Join", true);
        SettingsManager.getConfig().addDefault("Menu-Item.Give-On-Respawn", true);
        SettingsManager.getConfig().addDefault("Menu-Item.Slot", 3);
        SettingsManager.getConfig().addDefault("Menu-Item.Type", "ENDER_CHEST");
        SettingsManager.getConfig().addDefault("Menu-Item.Data", 0);
        SettingsManager.getConfig().addDefault("Menu-Item.Displayname", "&6&lCosmetics");
        SettingsManager.getConfig().addDefault("No-Permission.Show-In-Lore", true);
        SettingsManager.getConfig().addDefault("No-Permission.Lore-Message-Yes", "&o&7Permission: &a&lYes!");
        SettingsManager.getConfig().addDefault("No-Permission.Lore-Message-No", "&o&7Permission: &4&lNo!");
        SettingsManager.getConfig().addDefault("No-Permission.Dont-Show-Item", false);
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.enabled", false);
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.Type", "INK_SACK");
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.Data", 8);
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.Name", "&c&lNo Permission");
        SettingsManager.getConfig().addDefault("Disabled-Items.Show-Custom-Disabled-Item", false);
        SettingsManager.getConfig().addDefault("Disabled-Items.Custom-Disabled-Item.Type", "INK_SACK");
        SettingsManager.getConfig().addDefault("Disabled-Items.Custom-Disabled-Item.Data", 8);
        SettingsManager.getConfig().addDefault("Disabled-Items.Custom-Disabled-Item.Name", "&c&lDisabled");

        SettingsManager.getConfig().addDefault("Chat-Cosmetic-PlaceHolder-Color", true);

        SettingsManager.getConfig().addDefault("Gadget-Slot", 4);
        SettingsManager.getConfig().addDefault("Remove-Gadget-With-Drop", false);

        fileStorage = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.System")).equalsIgnoreCase("file");

        placeHolderColor = SettingsManager.getConfig().get("Chat-Cosmetic-PlaceHolder-Color");

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

        ammoEnabled = SettingsManager.getConfig().get("Ammo-System-For-Gadgets.Enabled");

        for (Category c : Category.values()) {
            if (c == Category.MORPHS)
                if (!Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
                    continue;
            if (c.isEnabled())
                enabledCategories.add(c);
        }

        // Register Morphs
        if (Category.MORPHS.isEnabled() && Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            morphList.add(new MorphBat(null));
            morphList.add(new MorphBlaze(null));
            morphList.add(new MorphSlime(null));
            morphList.add(new MorphEnderman(null));
            morphList.add(new MorphChicken(null));
            morphList.add(new MorphPig(null));
            morphList.add(new MorphCreeper(null));
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        for (Gadget gadget : gadgetList) {
            SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Enabled", true);
            if (gadget.getType() == Gadget.GadgetType.PAINTBALLGUN) {
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Block-Type", "STAINED_CLAY");
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Particle.Enabled", false);
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Particle.Effect", "fireworksSpark");
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Radius", 2);
                List<String> blackListedBlocks = new ArrayList<>();
                blackListedBlocks.add("REDSTONE_BLOCK");
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".BlackList", blackListedBlocks);
            }
            if (ammoEnabled) {
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Ammo.Enabled", true);
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Ammo.Price", 500);
                SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Ammo.Result-Amount", 20);
            }
        }

        for (Mount m : mountList)
            SettingsManager.getConfig().addDefault("Mounts." + m.getConfigName() + ".Enabled", true);

        for (ParticleEffect particleEffect : particleEffectList)
            SettingsManager.getConfig().addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Enabled", true);

        for (Pet pet : petList)
            SettingsManager.getConfig().addDefault("Pets." + pet.getConfigName() + ".Enabled", true);

        for (Morph morph : morphList)
            SettingsManager.getConfig().addDefault("Morphs." + morph.getConfigName() + ".Enabled", true);


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
        setupEconomy();
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


        for (Player p : Bukkit.getOnlinePlayers()) {
            customPlayers.add(new CustomPlayer(p.getUniqueId()));
            if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && !((List<String>) SettingsManager.getConfig().get("Disabled-Worlds")).contains(p.getWorld().getName())) {
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

        ArrayList<Entity> ents = new ArrayList<Entity>();


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
                            if (((List<String>) SettingsManager.getConfig().get("Disabled-Worlds")).contains(p.getWorld().getName()))
                                Core.getCustomPlayer(p).clear();
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

        if (nbsapiEnabled) {
            File folder = new File(getDataFolder().getPath() + "/songs/");
            if ((!folder.exists()) || (folder.listFiles().length <= 0)) {
                saveResource("songs/GetLucky.nbs", true);
            }
            saveResource("songs/NyanCat.nbs", true);
        }


        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
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
        registerListener(new MenuListener(this));
        if (Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
            registerListener(new MorphMenuListener());

    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
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
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=10905").getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(
                    con.getInputStream())).readLine();
            return version.replace("Beta ", "").replace("Release ", "").replace("Pre-", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Can't confirm the latest version!");
        }
        return Core.getPlugin().getDescription().getVersion().replace("Beta ", "").replace("Pre-", "").replace("Release", "");
    }

    public static CharSequence filterColor(String menuName) {
        String filtered = menuName;
        Character[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'o', 'n', 'm', 'r', 'k'};
        for (Character character : chars)
            menuName = menuName.replace("§" + character, "");
        return menuName;
    }

    public enum Category {
        PETS("Pets", ItemFactory.create(Material.MONSTER_EGG, (byte) 0, MessageManager.getMessage("Menu.Pets"))),
        EFFECTS("Particle-Effects", ItemFactory.create(Material.MELON_SEEDS, (byte) 0, MessageManager.getMessage("Menu.Particle-Effects"))),
        GADGETS("Gadgets", ItemFactory.create(Material.SLIME_BALL, (byte) 0, MessageManager.getMessage("Menu.Gadgets"))),
        MOUNTS("Mounts", ItemFactory.create(Material.SADDLE, (byte) 0, MessageManager.getMessage("Menu.Mounts"))),
        MORPHS("Morphs", ItemFactory.create(Material.SKULL_ITEM, (byte) 4, MessageManager.getMessage("Menu.Morphs")));

        String configPath;
        ItemStack is;

        Category(String configPath, ItemStack is) {
            this.configPath = configPath;
            this.is = is;
        }

        public ItemStack getItemStack() {
            return is;
        }

        public boolean isEnabled() {
            return SettingsManager.getConfig().get("Categories-Enabled." + configPath);
        }
    }

}
