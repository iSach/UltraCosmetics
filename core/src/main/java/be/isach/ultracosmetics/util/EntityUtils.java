package be.isach.ultracosmetics.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 19/12/15.
 */
public class EntityUtils {

    public static List<Entity> getEntitiesInRadius(Location center, double radius) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : center.getWorld().getEntities())
            if (entity.getLocation().distance(center) <= radius)
                entities.add(entity);
        return entities;
    }

}
