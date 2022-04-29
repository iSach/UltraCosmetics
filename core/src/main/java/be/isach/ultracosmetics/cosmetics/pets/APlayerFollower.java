package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmeticsData;

import org.bukkit.entity.Player;

/**
 * Player follower interface.
 *
 * @author iSach
 * @since 03-07-2016
 */
public abstract class APlayerFollower {
    protected final Pet pet;
    protected final Player player;
    public APlayerFollower(Pet pet, Player player) {
        this.pet = pet;
        this.player = player;
    }

    public void run() {
        if (player == null || !player.isOnline()) {
            return;
        }

        if (UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).getCurrentTreasureChest() != null) {
            return;
        }
        follow();
    }

    protected abstract void follow();
}
