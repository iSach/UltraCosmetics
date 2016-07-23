package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetBlizzardBlaster extends Gadget {

    GadgetBlizzardBlaster instance;
    Random r = new Random();

    public GadgetBlizzardBlaster(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.BLIZZARDBLASTER, ultraCosmetics);
        instance = this;
    }

    @Override
    void onRightClick() {
        final Vector v = getPlayer().getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = getPlayer().getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimerAsynchronously(UltraCosmetics.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (UltraCosmetics.getCustomPlayer(getPlayer()).currentGadget != instance) {
                    cancel();
                    return;
                }
                if (loc.getBlock().getType() != Material.AIR
                        && loc.getBlock().getType().isSolid()) {
                    loc.add(0, 1, 0);
                }
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                    if (loc.clone().getBlock().getTypeId() != 43 && loc.clone().getBlock().getTypeId() != 44)
                        loc.add(0, -1, 0);
                }
                for (int i = 0; i < 3; i++) {
                    UltraCosmetics.getInstance().getEntityUtil().sendBlizzard(getPlayer(), loc, affectPlayers, v);
                }
                loc.add(v);
            }
        }, 0, 1).getTaskId();

        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(i);
            }
        }, 40);

    }

    @Override
    void onLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {
        UltraCosmetics.getInstance().getEntityUtil().clearBlizzard(getPlayer());
        HandlerList.unregisterAll(this);
    }
}
