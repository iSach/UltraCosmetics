package me.isach.ultracosmetics;

import me.isach.ultracosmetics.commands.UltraCosmeticsCommand;
import me.isach.ultracosmetics.commands.UltraCosmeticsTabCompleter;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.*;
import me.isach.ultracosmetics.cosmetics.mounts.*;
import me.isach.ultracosmetics.cosmetics.particleeffects.*;
import me.isach.ultracosmetics.cosmetics.pets.*;
import me.isach.ultracosmetics.listeners.MenuListener;
import me.isach.ultracosmetics.listeners.PlayerListener;
import me.isach.ultracosmetics.mysql.MySQLConnection;
import me.isach.ultracosmetics.mysql.Table;
import me.isach.ultracosmetics.util.BlockUtils;
import me.isach.ultracosmetics.util.ItemFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
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

    public static List<Gadget> gadgetList = new ArrayList<>();
    public static List<ParticleEffect> particleEffectList = new ArrayList<>();
    public static List<Mount> mountList = new ArrayList<>();
    public static List<Pet> petList = new ArrayList<>();
    public static List<CustomPlayer> customPlayers = new ArrayList<>();
    public static HashMap<Player, HashMap<Gadget.GadgetType, Double>> countdownMap = new HashMap<>();

    public static ArrayList<GadgetDiscoBall> discoBalls = new ArrayList<>();
    public static ArrayList<GadgetExplosiveSheep> explosiveSheep = new ArrayList<>();

    public static boolean nbsapiEnabled = false;
    public static boolean ammoEnabled = false;
    public static boolean ammoFileStorage = true;

    public static Economy economy = null;

    private MySQLConnection sql;
    private Connection co; // SQL Connection.
    public Table table; // SQL Table.
    public static SQLUtils sqlUtils; // SQL Utils.

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("NoteBlockAPI") != null) {
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("  §4§lNoteBlockAPI found! Using it!");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
            nbsapiEnabled = true;
        }

        new MessageManager();
        registerListener(new MenuListener());
        registerListener(new PlayerListener());

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

        // Set config things.
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.Enabled", false);
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.System", "file");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.hostname", "localhost");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.username", "root");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.password", "password");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.port", "3306");
        SettingsManager.getConfig().addDefault("Ammo-System-For-Gadgets.MySQL.database", "UltraCosmetics");
        SettingsManager.getConfig().addDefault("Menu-Item.Give-On-Join", true);
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

        SettingsManager.getConfig().addDefault("Gadget-Slot", 4);
        SettingsManager.getConfig().addDefault("Remove-Gadget-With-Drop", false);

        ammoEnabled = SettingsManager.getConfig().get("Ammo-System-For-Gadgets.Enabled");

        ammoFileStorage = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.System")).equalsIgnoreCase("file");

        for (Gadget gadget : gadgetList) {
            SettingsManager.getConfig().addDefault("Gadgets." + gadget.getType().configName + ".Enabled", true);
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
            setupEconomy();
            if (!ammoFileStorage) {
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
                    sqlUtils = new SQLUtils(this);

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
                String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§");
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
                byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
                p.getInventory().setItem(slot, ItemFactory.create(material, data, name));
            }
        }


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
                        while (iter.hasNext()) {
                            CustomPlayer customPlayer = customPlayerIterator.next();
                            if (customPlayer.getPlayer() == null)
                                customPlayerIterator.remove();
                        }
                        for (Player p : countdownMap.keySet()) {
                            if (((List<String>) SettingsManager.getConfig().get("Disabled-Worlds")).contains(p.getWorld().getName())) {
                                Core.getCustomPlayer(p).clear();
                            }
                            if (countdownMap.get(p) != null) {
                                for (Gadget.GadgetType gt : countdownMap.get(p).keySet()) {
                                    double timeLeft = countdownMap.get(p).get(gt);
                                    if (timeLeft > 0.05f) {
                                        timeLeft -= 0.05f;
                                        countdownMap.get(p).put(gt, timeLeft);
                                    }
                                }
                                Iterator it = countdownMap.get(p).entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry) it.next();
                                    if ((double) pair.getValue() < 0.1) {
                                        it.remove();
                                    }
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
                    if (outdated()) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.isOp())
                                p.sendMessage("§l§oUltraCosmetics > §c§lAn update is available: " + getLastVersion());
                        }
                    }
                }
            }, 20);

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
        BlockUtils.forceRestore();
        for (CustomPlayer cp : customPlayers) {
            cp.clear();
            int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
            if (cp.getPlayer().getInventory().getItem(slot) != null
                    && cp.getPlayer().getInventory().getItem(slot).hasItemMeta()
                    && cp.getPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                    && cp.getPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§"))) {
                cp.getPlayer().getInventory().setItem(slot, null);
            }
        }
        Core.customPlayers.clear();
    }

    /**
     * Gets the UltraCosmetics Plugin Object.
     *
     * @return
     */
    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("UltraCosmetics");
    }

    public static void registerListener(Listener listenerClass) {
        Bukkit.getPluginManager().registerEvents(listenerClass, getPlugin());
    }

    public static CustomPlayer getCustomPlayer(Player player) {
        for (CustomPlayer cp : customPlayers)
            if (cp.getPlayer().getName().equals(player.getName()))
                return cp;
        return new CustomPlayer(player.getUniqueId());
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
            return version.replaceAll("Beta ", "").replaceAll("Release ", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Version cannot be verified!";
    }

    public static boolean outdated() {
        String currentVersion = Core.getPlugin().getDescription().getVersion().replaceAll("Beta ", "");
        int i = new Version(currentVersion).compareTo(new Version(getLastVersion()));
        return i == -1;
    }

}
