package be.isach.ultracosmetics.cosmetics.mounts;

import org.bukkit.entity.Entity;

/**
 * Custom mount entity interface.
 * 
 * @author 	iSach
 * @since 	03-15-2016
 */
public interface IMountCustomEntity {

    Entity getEntity();

    void removeAi();
}
