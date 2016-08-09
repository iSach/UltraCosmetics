package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * Package: be.isach.ultracosmetics.cosmetics.type
 * Created by: sachalewin
 * Date: 4/08/16
 * Project: UltraCosmetics
 *
 * Represents a Cosmetic Type with a Material, a Data, and an Entity Type.
 */
public class CosmeticEntType<T extends Cosmetic> extends CosmeticMatType<T> {

    private EntityType entityType;

    public CosmeticEntType(Category category, String configName, String permission,
                           String description, Material material, byte data,
                           EntityType entityType, Class clazz) {
        super(category, configName, permission, description, material, data, clazz);
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
