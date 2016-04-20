package be.isach.ultracosmetics.run;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Sacha on 15/12/15.
 */
public class FallDamageManager implements Runnable {

    public static List<Entity> noFallDamage = Collections.synchronizedList(new ArrayList<Entity>());
    public static List<Entity> queue = Collections.synchronizedList(new ArrayList<Entity>());

    public static void addNoFall(Entity entity) {
        if (!queue.contains(entity)
                && !noFallDamage.contains(entity))
            queue.add(entity);
    }

    public static boolean shouldBeProtected(Entity entity) {
        return noFallDamage.contains(entity)
                || queue.contains(entity);
    }

    @Override
    public void run() {
        for (Iterator<Entity> iterator = noFallDamage.iterator(); iterator.hasNext(); ) {
            Entity ent = iterator.next();
            if (ent.isOnGround())
                iterator.remove();
        }
        noFallDamage.addAll(queue);
        queue.clear();
    }
}
