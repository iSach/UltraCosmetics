package be.isach.ultracosmetics.config;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.profile.CosmeticsProfile;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * NPC cosmetics manager.
 *
 * @author SinfulMentality
 * @since 04-24-2020
 */
public class NPCManager {

    private UltraCosmetics plugin;
    private File file;
    private FileConfiguration conf;
    private boolean loaded = false;

    public NPCManager(UltraCosmetics uc) {
        plugin = uc;

        if (!UltraCosmeticsData.get().getPlugin().getDataFolder().exists()) {
            UltraCosmeticsData.get().getPlugin().getDataFolder().mkdir();
        }

        File f = new File(UltraCosmeticsData.get().getPlugin().getDataFolder(), "/data");
        if (!f.exists())
            f.mkdirs();

        file = new File(UltraCosmeticsData.get().getPlugin().getDataFolder(), "/data/_npc-uuids.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        conf = YamlConfiguration.loadConfiguration(file);
    }

    public void LoadNPCCosmetics() {
        if(!loaded) {
            for (String uuidString : getNPCList()) {
                UUID uuid = UUID.fromString(uuidString);
                Entity npcEntity = Bukkit.getEntity(uuid);
                Player npc;

                // Check if NPC exists and is a valid player type
                if (npcEntity instanceof Player) {
                    npc = (Player) npcEntity;
                } else continue;

                // Check if NPC is in a world with cosmetics enabled, and if so, load its cosmetics profile
                UltraPlayer ultraPlayer = plugin.getPlayerManager().getUltraPlayer(npc);
                if (SettingsManager.getConfig().getStringList("Enabled-Worlds").contains(npc.getWorld().getName())) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (UltraCosmeticsData.get().areCosmeticsProfilesEnabled()) {

                                CosmeticsProfile cp = plugin.getCosmeticsProfileManager().getProfile(npc.getUniqueId());
                                if (cp == null) {
                                    plugin.getCosmeticsProfileManager().initForPlayer(ultraPlayer);
                                } else {
                                    cp.loadToPlayer();
                                }
                            }
                        }
                    }.runTask(plugin);
                }
            }
            loaded = true; // Only load NPC cosmetics once, when first player joins
        }
    }

    // Update the list of all NPCs affected by cosmetics
    public void AddNPC(UUID uuid) {

        // Add NPC uuid to the npc-uuids list
        List<String> npcList = getNPCList();
        if(!npcList.contains(uuid.toString())) npcList.add(uuid.toString());
        conf.set("npcs-with-cosmetics", npcList);
        saveNPCList();

        // Initialize the cosmetics profile of the newly added NPC
        Entity npcEntity = Bukkit.getEntity(uuid);
        Player npc;
        if (npcEntity instanceof Player) {
            npc = (Player) npcEntity;
        } else return;
        UltraPlayer ultraPlayer = plugin.getPlayerManager().getUltraPlayer(npc);
        plugin.getCosmeticsProfileManager().initForPlayer(ultraPlayer);
    }

    public void RemoveNPC(UUID uuid) {

        // Remove NPC uuid from the npc-uuids list
        List<String> npcList = getNPCList();
        if(npcList.contains(uuid.toString())) npcList.remove(uuid.toString());
        conf.set("npcs-with-cosmetics", npcList);
        saveNPCList();

    }

    public List<String> getNPCList() {
        return conf.getStringList("npcs-with-cosmetics");
    }

    // Save NPC list to file
    private void saveNPCList() {
        try {
            conf.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to save NPC List");
        }
    }

}