package be.isach.ultracosmetics.version;

import org.bukkit.entity.EntityType;

/**
 * Gets version specific things (e.g. entity types), if valid.
 *
 * @author RadBuilder
 * @since 10-20-2017
 */
public class GetForVersion {
    /**
     * Gets the {@link org.bukkit.entity.EntityType EntityType} for the server version, if valid.
     *
     * @param name The name of the entity type.
     * @return The {@link org.bukkit.entity.EntityType EntityType} for the server version, if valid.
     */
    public static EntityType entityType(String name) {
        for (EntityType type : EntityType.values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return EntityType.values()[0];
    }
}
