package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.entity.Horse;

/**
 * Represents an instance of a mount of water mount.
 *
 * @author iSach
 * @since 08-10-2015
 */
public class MountOfWater extends MountHorse {
    public MountOfWater(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("mountofwater"), ultraCosmetics);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        entity.setJumpStrength(0.7);
    }

    @Override
    public void onUpdate() {
        UtilParticles.display(Particles.DRIP_WATER, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }

    @Override
    protected Horse.Color getColor() {
        return Horse.Color.BLACK;
    }
}