package be.isach.ultracosmetics.cosmetics.mounts.abstracthorse;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;

/**
 * @author RadBuilder
 */
public class MountWalkingDead extends MountAbstractHorse {

    public MountWalkingDead(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("walkingdead"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
        Particles.CRIT_MAGIC.display(0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
        Particles.SPELL_MOB_AMBIENT.display(0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }
}
