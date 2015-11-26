package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountDruggedHorse extends Mount {

    public MountDruggedHorse(UUID owner) {
        super(EntityType.HORSE, Material.SUGAR, (byte) 0, "DruggedHorse", "ultracosmetics.mounts.druggedhorse", owner, MountType.DRUGGEDHORSE,
                "&7&oThat is just too much!");

        if (owner != null) {

            if (ent instanceof Horse) {
                Horse horse = (Horse) ent;

                horse.setColor(Horse.Color.CHESTNUT);
                color = Horse.Color.CHESTNUT;
                variant = Horse.Variant.HORSE;
                horse.setVariant(Horse.Variant.HORSE);

                EntityHorse entityHorse = ((CraftHorse) horse).getHandle();

                entityHorse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.1D);
                horse.setJumpStrength(1.3);
            }
            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    try {
                        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000000, 1));
                    } catch (Exception exc) {

                    }
                }
            }, 1);
        }
    }

    @Override
    void onUpdate() {
        Location loc = ent.getLocation().add(0, 1, 0);
        UtilParticles.play(Particles.FIREWORKS_SPARK, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.play(Particles.SPELL, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.play(Particles.SPELL_MOB_AMBIENT, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.play(Particles.SPELL_MOB, 5, 255, 0, loc);
    }
}
