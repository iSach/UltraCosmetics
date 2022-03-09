package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.cosmetics.type.SuitCategory;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.profile.CosmeticsProfile;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sacha
 * Date: 15/08/15
 * Project: UltraCosmetics
 */
public class SqlCache extends CosmeticsProfile {
    private final Table table;
    private Map<String,Optional<Object>> updateQueue = new ConcurrentHashMap<>();
    private BukkitTask updateTask = null;

    public SqlCache(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, ultraCosmetics);
        this.table = ultraCosmetics.getMySqlConnectionManager().getTable();
    }

    @Override
    protected void load() {
        // update table with UUID. If it's already there, ignore
        table.insertIgnore().insert("uuid", uuid.toString()).execute();
        Map<String,Object> properties = table.select("*").uuid(uuid).getAll();
        gadgetsEnabled = (boolean) properties.get("gadgetsEnabled");
        morphSelfView = (boolean) properties.get("selfmorphview");
        for (PetType type : PetType.enabled()) {
            petNames.put(type, (String) properties.get(cleanCosmeticName(type)));
        }
        for (GadgetType type : GadgetType.enabled()) {
            ammo.put(type, (int) properties.get(cleanCosmeticName(type)));
        }
        keys = (int) properties.get("treasureKeys");
        for (Category cat : Category.enabled()) {
            if (cat == Category.SUITS) {
                for (ArmorSlot slot : ArmorSlot.values()) {
                    String suitCategory = (String) properties.get(Category.SUITS.toString().toLowerCase() + "_" + slot.toString().toLowerCase());
                    if (suitCategory == null) continue;
                    enabledSuitParts.put(slot, SuitCategory.valueOf(suitCategory.toUpperCase()).getPiece(slot));
                }
                continue;
            }
            ultraCosmetics.getSmartLogger().write("Loading category " + cat.toString() + " with value " + properties.get(cleanCategoryName(cat)) + " which has type " + cat.valueOfType((String) properties.get(cleanCategoryName(cat))));
            enabled.put(cat, cat.valueOfType((String) properties.get(cleanCategoryName(cat))));
        }
    }

    @Override
    public void setEnabledCosmetic(Category cat, CosmeticType<?> type) {
        super.setEnabledCosmetic(cat, type);
        if (cat == Category.SUITS) return; // handled by setEnabledSuitPart
        queueUpdate(cleanCategoryName(cat), type == null ? null : cleanCosmeticName(type));
    }

    @Override
    public void setEnabledSuitPart(ArmorSlot slot, SuitType type) {
        super.setEnabledSuitPart(slot, type);
        queueUpdate(cleanCategoryName(Category.SUITS) + "_" + slot.toString().toLowerCase(), cleanCosmeticName(type));
    }

    @Override
    public void setAmmo(GadgetType type, int amount) {
        super.setAmmo(type, amount);
        queueUpdate(cleanCosmeticName(type), amount);
    }

    @Override
    public void setPetName(PetType type, String name) {
        super.setPetName(type, name);
        queueUpdate(cleanCosmeticName(type), name);
    }

    @Override
    public void setKeys(int amount) {
        super.setKeys(amount);
        queueUpdate("treasureKeys", amount);
    }

    @Override
    public void setGadgetsEnabled(boolean gadgetsEnabled) {
        super.setGadgetsEnabled(gadgetsEnabled);
        queueUpdate("gadgetsEnabled", gadgetsEnabled);
    }

    @Override
    public void setSeeSelfMorph(boolean seeSelfMorph) {
        super.setSeeSelfMorph(seeSelfMorph);
        queueUpdate("selfmorphview", seeSelfMorph);
    }

    @Override
    public void setTreasureNotifications(boolean treasureNotifications) {
        super.setTreasureNotifications(treasureNotifications);
        queueUpdate("treasureNotifications", treasureNotifications);
    }

    @Override
    public void setFilterByOwned(boolean filterByOwned) {
        super.setFilterByOwned(filterByOwned);
        queueUpdate("filterByOwned", filterByOwned);
    }

    private String cleanCosmeticName(CosmeticType<?> cosmetic) {
        return cosmetic == null ? null : cosmetic.getConfigName().toLowerCase().replace("_", "");
    }

    private String cleanCategoryName(Category cat) {
        return cat.toString().toLowerCase();
    }

    /**
     * This function optimizes multiple separate queries into
     * a single update query, as well as properly handling any
     * conflicting queries in the order they were actually
     * received.
     * @param key
     * @param value
     */
    private void queueUpdate(String key, Object value) {
        updateQueue.put(key, Optional.ofNullable(value));
        if (updateTask == null || !Bukkit.getScheduler().isQueued(updateTask.getTaskId())) {
            updateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    StandardQuery query = table.update().uuid(uuid);
                    updateQueue.forEach((k,v) -> query.set(k, v.orElse(null)));
                    query.execute();
                    updateQueue.clear();
                }
            }.runTaskAsynchronously(ultraCosmetics);
        }
    }

    // Saved on write
    @Override
    public void save() {
    }
}
