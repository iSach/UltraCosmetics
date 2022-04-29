package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestParticleRunnable extends BukkitRunnable {
    private final TreasureChest chest;
    private final UltraCosmetics uc;
    private int i;
    private PlaceChestRunnable chestRunnable = null;
    public ChestParticleRunnable(TreasureChest chest) {
        this.chest = chest;
        this.uc = UltraCosmeticsData.get().getPlugin();
        i = chest.getChestsLeft();
    }

    @Override
    public void run() {
        if (i <= 0) {
            cancel();
            return;
        }
        Player player = chest.getPlayer();
        if (player == null || uc.getPlayerManager().getUltraPlayer(player).getCurrentTreasureChest() != chest) {
            cancel();
            return;
        }
        int animationTime = 0;
        Particles particleEffect = chest.getParticleEffect();
        if (particleEffect != null) {
            particleEffect.playHelix(getChestLocation(), 0.0F);
            particleEffect.playHelix(getChestLocation(), 3.5F);
            animationTime = 30;
        }
        chestRunnable = new PlaceChestRunnable(chest, getChestLocation(), i--);
        chestRunnable.runTaskLater(uc, animationTime);
    }

    private Location getChestLocation() {
        Location chestLocation = chest.getCenter();
        chestLocation.setX(chestLocation.getBlockX() + 0.5D);
        chestLocation.setY(chestLocation.getBlockY());
        chestLocation.setZ(chestLocation.getBlockZ() + 0.5D);
        switch (i) {
            case 1:
                chestLocation.add(2.0D, 0.0D, 0.0D);
                break;
            case 2:
                chestLocation.add(-2.0D, 0.0D, 0.0D);
                break;
            case 3:
                chestLocation.add(0.0D, 0.0D, 2.0D);
                break;
            case 4:
                chestLocation.add(0.0D, 0.0D, -2.0D);
        }

        return chestLocation;
    }

    public void propogateCancel() {
        cancel();
        if (chestRunnable != null) {
            chestRunnable.cancel();;
        }
    }
}
