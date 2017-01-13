package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
* Represents an instance of a blizzard blaster gadget summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-08-2015
 */
public class GadgetBlizzardBlaster extends Gadget {

    GadgetBlizzardBlaster instance;
    Random r = new Random();

    public GadgetBlizzardBlaster(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.BLIZZARDBLASTER, ultraCosmetics);
        instance = this;
    }

    @SuppressWarnings("deprecation")
	@Override
    void onRightClick() {
        final Vector v = getPlayer().getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = getPlayer().getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimerAsynchronously(getUltraCosmetics(), new BukkitRunnable() {
            @Override
            public void run() {
                if (getOwner().getCurrentGadget() != instance) {
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
                    UltraCosmeticsData.get().getVersionManager().getEntityUtil().sendBlizzard(getPlayer(), loc, affectPlayers, v);
                }
                loc.add(v);
            }
        }, 0, 1).getTaskId();

        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
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
    public void onUpdate() {
    }

    @Override
    public void onClear() {
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearBlizzard(getPlayer());
        HandlerList.unregisterAll(this);
    }
}
