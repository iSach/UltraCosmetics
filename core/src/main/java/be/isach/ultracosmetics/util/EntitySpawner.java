package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EntitySpawner<T extends Entity> extends BukkitRunnable {
    private final int limit = SettingsManager.getConfig().getInt("Max-Entity-Spawns-Per-Tick");
    private final EntityType type;
    private final Consumer<T> func;
    private final Location loc;
    private final boolean spread;
    private int remaining;
    private Set<T> entities = new HashSet<>();

    public EntitySpawner(EntityType type, Location loc, int amount, boolean spread, Consumer<T> func, UltraCosmetics ultraCosmetics) {
        this.type = type;
        this.loc = loc;
        this.remaining = amount;
        this.spread = spread;
        this.func = func;
        if (limit < 1) {
            run();
            return;
        }
        this.runTaskTimer(ultraCosmetics, 0, 1);
    }

    public EntitySpawner(EntityType type, Location loc, int amount, Consumer<T> func, UltraCosmetics ultraCosmetics) {
        this(type, loc, amount, false, func, ultraCosmetics);
    }

    public EntitySpawner(EntityType type, Location loc, int amount, UltraCosmetics ultraCosmetics) {
        this(type, loc, amount, e -> {
        }, ultraCosmetics);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        for (int i = 0; i < limit; i++) {
            if (remaining < 1) {
                if (limit > 0) {
                    cancel();
                }
                return;
            }
            Location spawnLoc = loc.clone();
            if (spread) {
                spawnLoc.add(remaining % 5 - 2, 0, remaining / 4 - 2);
            }

            T entity = (T) loc.getWorld().spawnEntity(spawnLoc, type);
            func.accept(entity);
            entities.add(entity);

            remaining--;
        }
    }

    public Set<T> getEntities() {
        return entities;
    }

    public void removeEntity(Entity entity) {
        entity.remove();
        entities.remove(entity);
    }

    public void removeEntities() {
        for (T entity : entities) {
            entity.remove();
        }
        entities.clear();
        try {
            cancel();
        } catch (IllegalStateException ignored) {
        }
    }

    public boolean isEmpty() {
        return entities.isEmpty();
    }

    public boolean contains(Entity entity) {
        return entities.contains(entity);
    }

}
