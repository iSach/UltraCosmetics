package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountDruggedHorse extends Mount {

    public MountDruggedHorse(UUID owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.DRUGGEDHORSE, ultraCosmetics);
    }

    @Override
    protected void onEquip() {
        if (entity instanceof Horse) {
            Horse horse = (Horse) entity;

            horse.setColor(Horse.Color.CHESTNUT);
            color = Horse.Color.CHESTNUT;
            variant = Horse.Variant.HORSE;
            horse.setVariant(Horse.Variant.HORSE);
            UltraCosmetics.getInstance().getEntityUtil().setHorseSpeed(horse, 1.1d);
            horse.setJumpStrength(1.3);
        }
        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000000, 1));
                } catch (Exception exc) {

                }
            }
        }, 1);
    }

    @Override
    protected void onUpdate() {
        Location loc = entity.getLocation().add(0, 1, 0);
        UtilParticles.display(Particles.FIREWORKS_SPARK, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.display(Particles.SPELL, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.display(Particles.SPELL_MOB_AMBIENT, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.display(Particles.SPELL_MOB, 5, 255, 0, loc);
    }
}
