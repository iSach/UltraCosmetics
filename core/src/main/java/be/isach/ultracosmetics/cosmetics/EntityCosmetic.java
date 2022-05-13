package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.CosmeticEntType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.entity.Entity;

public abstract class EntityCosmetic<T extends CosmeticEntType<?>> extends Cosmetic<T> {
    /**
     * The Entity, if it isn't a Custom Entity.
     */
    protected Entity entity;
    public EntityCosmetic(UltraCosmetics ultraCosmetics, Category category, UltraPlayer owner, T type) {
        super(ultraCosmetics, category, owner, type);
    }

    public Entity getEntity() {
        return entity;
    }

    protected Entity spawnEntity() {
        return getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
    }

    protected void removeEntity() {
        if (entity != null) {
            entity.remove();
        }
    }

    protected void setupEntity() {
    }
}
