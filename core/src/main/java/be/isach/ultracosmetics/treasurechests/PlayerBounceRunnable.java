package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.util.MathUtils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlayerBounceRunnable extends BukkitRunnable {
    private final TreasureChest chest;
    public PlayerBounceRunnable(TreasureChest chest) {
        this.chest = chest;
    }

    @Override
    public void run() {
        Player player = chest.getPlayer();
        UltraPlayerManager pm = UltraCosmeticsData.get().getPlugin().getPlayerManager();
        UltraPlayer ultraPlayer = pm.getUltraPlayer(player);
        if (ultraPlayer == null || ultraPlayer.getCurrentTreasureChest() != chest)  {
            cancel();
            return;
        }
        Location center = chest.getCenter();
        if (player.getWorld() != center.getWorld()) {
            player.teleport(center);
        }
        // distanceSquared is cheaper performance-wise than distance,
        // and for our use case it doesn't matter.
        //                equivalent to distance(center) > 1.5
        if (player.getLocation().distanceSquared(center) > 2.25) {
            player.teleport(center);
        }
        for (Entity ent : player.getNearbyEntities(2, 2, 2)) {
            if (ent == player) continue;
            if (chest.isSpecialEntity(ent)) continue;
            UltraPlayer up = pm.getUltraPlayer(player);
            // if player has a pet and the loop entity is either the pet or one of its items, skip it
            if (up.getCurrentPet() != null) {
                if (ent == up.getCurrentPet()) continue;
                if (up.getCurrentPet().getItems().contains(ent)) continue;
            }
            // Passed all checks, launch the player!
            Vector v = ent.getLocation().toVector().subtract(player.getLocation().toVector()).multiply(0.5).setY(1);
            MathUtils.applyVelocity(ent, v.add(MathUtils.getRandomCircleVector().multiply(0.2)));
        }
    }

}
