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

    private boolean active;
    private Location location;
    private Vector vector;

    public GadgetBlizzardBlaster(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.BLIZZARDBLASTER, ultraCosmetics);
    }

	@Override
    void onRightClick() {
        this.vector = getPlayer().getLocation().getDirection().normalize().multiply(0.3);
        this.vector.setY(0);

        this.location = getPlayer().getLocation().subtract(0, 1, 0).add(vector);
        this.active = true;

        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), this::clean, 40);

    }
    @Override
    public void onUpdate() {
        if(!active) {
            return;
        }

        if (location.getBlock().getType() != Material.AIR
                && location.getBlock().getType().isSolid()) {
            location.add(0, 1, 0);
        }

        if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            if (location.clone().getBlock().getTypeId() != 43 && location.clone().getBlock().getTypeId() != 44)
                location.add(0, -1, 0);
        }

        for (int i = 0; i < 3; i++) {
            UltraCosmeticsData.get().getVersionManager().getEntityUtil().sendBlizzard(getPlayer(), location, affectPlayers, vector);
        }

        location.add(vector);
    }

    @Override
    void onLeftClick() {
    }


    @Override
    public void onClear() {
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearBlizzard(getPlayer());
    }

    private void clean() {
        active = false;
        location = null;
        vector = null;
    }
}
