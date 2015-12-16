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

    @Override
    public void run() {
        Iterator<Entity> iter = noFallDamage.iterator();
        while (iter.hasNext()) {
            Entity ent = iter.next();
            if (ent.isOnGround())
                iter.remove();
        }
        noFallDamage.addAll(queue);
        queue.clear();
    }

    public static void addNoFall(Entity entity) {
        if (!queue.contains(entity)
                && !noFallDamage.contains(entity))
            queue.add(entity);
    }

    public static boolean shouldBeProtected(Entity entity) {
        return noFallDamage.contains(entity)
                || queue.contains(entity);
    }
}
