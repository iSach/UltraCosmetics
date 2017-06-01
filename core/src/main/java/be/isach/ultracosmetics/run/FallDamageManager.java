package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.UltraCosmeticsData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sacha on 15/12/15.
 */
public class FallDamageManager extends BukkitRunnable {

    public static List<Entity> noFallDamage = Collections.synchronizedList(new ArrayList<Entity>());
    public static List<Entity> queue = Collections.synchronizedList(new ArrayList<Entity>());

    public static void addNoFall(Entity entity) {
        if (!queue.contains(entity)
                && !noFallDamage.contains(entity)) {
            queue.add(entity);
        }
    }

    public static boolean shouldBeProtected(Entity entity) {
        return noFallDamage.contains(entity) || queue.contains(entity);
    }

    @Override
    public void run() {
        List<Entity> toRemove = new ArrayList<>();
        synchronized (noFallDamage) {
	        for (Entity ent : noFallDamage) {
		        if (ent.isOnGround()) {
			        toRemove.add(ent);
		        }
	        }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmeticsData.get().getPlugin(), () -> {
            noFallDamage.removeAll(toRemove);
        }, 5);
        noFallDamage.addAll(queue);
        queue.clear();
    }
}
