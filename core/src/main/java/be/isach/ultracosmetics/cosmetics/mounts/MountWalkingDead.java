package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.entity.Horse;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountWalkingDead extends Mount {

    public MountWalkingDead(UUID owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.WALKINGDEAD, ultraCosmetics);
    }

    @Override
    protected void onEquip() {
        Horse horse = (Horse) entity;
        horse.setVariant(Horse.Variant.UNDEAD_HORSE);
        variant = Horse.Variant.UNDEAD_HORSE;
        horse.setJumpStrength(0.7);
        UltraCosmetics.getInstance().getEntityUtil().setHorseSpeed(horse, 0.4d);
    }

    @Override
    protected void onUpdate() {
        UtilParticles.display(Particles.CRIT_MAGIC, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
        UtilParticles.display(Particles.SPELL_MOB_AMBIENT, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }
}
