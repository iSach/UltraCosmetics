package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountInfernalHorror extends Mount {

    public MountInfernalHorror(UUID owner) {
        super(EntityType.HORSE, Material.BONE, (byte) 0, "InfernalHorror", "ultracosmetics.mounts.infernalhorror", owner, MountType.INFERNALHORROR, "&7&oThis mount directly comes from hell!");
        if (ent instanceof Horse) {
            Horse horse = (Horse) ent;
            horse.setVariant(Horse.Variant.SKELETON_HORSE);
            variant = Horse.Variant.SKELETON_HORSE;
            horse.setJumpStrength(0.7);
            EntityHorse entityHorse = ((CraftHorse) horse).getHandle();
            entityHorse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4);
        }
    }

    @Override
    void onUpdate() {
        UtilParticles.display(Particles.FLAME, 0.4f, 0.2f, 0.4f, ent.getLocation().clone().add(0, 1, 0), 5);
    }
}
