package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.Horse;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountWalkingDead extends Mount {

    public MountWalkingDead(UUID owner) {
        super(owner, MountType.WALKINGDEAD);
        if (owner != null) {
            Horse horse = (Horse) ent;
            horse.setVariant(Horse.Variant.UNDEAD_HORSE);
            variant = Horse.Variant.UNDEAD_HORSE;
            horse.setJumpStrength(0.7);
            EntityHorse entityHorse = ((CraftHorse) horse).getHandle();
            entityHorse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4);
        }
    }

    @Override
    void onUpdate() {
        UtilParticles.display(Particles.CRIT_MAGIC, 0.4f, 0.2f, 0.4f, ent.getLocation().clone().add(0, 1, 0), 5);
        UtilParticles.display(Particles.SPELL_MOB_AMBIENT, 0.4f, 0.2f, 0.4f, ent.getLocation().clone().add(0, 1, 0), 5);
    }
}
