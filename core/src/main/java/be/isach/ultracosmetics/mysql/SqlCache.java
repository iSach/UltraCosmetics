package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.profile.CosmeticsProfile;
import be.isach.ultracosmetics.player.profile.ProfileKey;

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
    public void load() {
        data.loadFromSQL();
    }

    // Saved on write
    @Override
    public void save() {
    }

    @Override
    public void setEnabledCosmetic(Category cat, CosmeticType<?> type) {
        super.setEnabledCosmetic(cat, type);
        if (cat == Category.SUITS) return; // handled by setEnabledSuitPart
        queueUpdate(Table.cleanCategoryName(cat), type == null ? null : Table.cleanCosmeticName(type));
    }

    @Override
    public void setEnabledSuitPart(ArmorSlot slot, SuitType type) {
        super.setEnabledSuitPart(slot, type);
        queueUpdate(Table.cleanCategoryName(Category.SUITS) + "_" + slot.toString().toLowerCase(), Table.cleanCosmeticName(type));
    }

    @Override
    public void setAmmo(GadgetType type, int amount) {
        super.setAmmo(type, amount);
        queueUpdate(Table.cleanCosmeticName(type), amount);
    }

    @Override
    public void setPetName(PetType type, String name) {
        super.setPetName(type, name);
        queueUpdate(Table.cleanCosmeticName(type), name);
    }

    @Override
    public void setKeys(int amount) {
        super.setKeys(amount);
        queueUpdate(ProfileKey.KEYS, amount);
    }

    @Override
    public void setGadgetsEnabled(boolean gadgetsEnabled) {
        super.setGadgetsEnabled(gadgetsEnabled);
        queueUpdate(ProfileKey.GADGETS_ENABLED, gadgetsEnabled);
    }

    @Override
    public void setSeeSelfMorph(boolean seeSelfMorph) {
        super.setSeeSelfMorph(seeSelfMorph);
        queueUpdate(ProfileKey.MORPH_VIEW, seeSelfMorph);
    }

    @Override
    public void setTreasureNotifications(boolean treasureNotifications) {
        super.setTreasureNotifications(treasureNotifications);
        queueUpdate(ProfileKey.TREASURE_NOTIFICATION, treasureNotifications);
    }

    @Override
    public void setFilterByOwned(boolean filterByOwned) {
        super.setFilterByOwned(filterByOwned);
        queueUpdate(ProfileKey.FILTER_OWNED, filterByOwned);
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
        // use Optionals because ConcurrentHashMap doesn't support null values
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

    private void queueUpdate(ProfileKey key, Object value) {
        queueUpdate(key.getSqlKey(), value);
    }
}
