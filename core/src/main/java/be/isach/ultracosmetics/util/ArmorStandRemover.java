package be.isach.ultracosmetics.util;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class ArmorStandRemover {

    private List<ArmorStand> toRemove = new ArrayList<>();

    public void clear() {
        toRemove.stream().filter(Entity::isValid).forEach(ArmorStand::remove);
    }

}
