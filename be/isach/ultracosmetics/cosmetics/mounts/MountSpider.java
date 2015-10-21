package be.isach.ultracosmetics.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class MountSpider extends Mount {
    public MountSpider(UUID owner) {
        super(EntityType.SPIDER, Material.WEB, (byte) 0, "Spider", "ultracosmetics.mounts.spider", owner, MountType.SPIDER);
    }

    @Override
    void onUpdate() {
    }
}
