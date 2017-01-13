package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by sacha on 10/08/15.
 */
public class MountDruggedHorse extends Mount<Horse> {

    public MountDruggedHorse(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.DRUGGEDHORSE, ultraCosmetics);
    }

    @Override
    public void onEquip() {
        super.onEquip();

        getEntity().setColor(Horse.Color.CHESTNUT);
        color = Horse.Color.CHESTNUT;
        variant = Horse.Variant.HORSE;
        getEntity().setVariant(Horse.Variant.HORSE);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().setHorseSpeed(getEntity(), 1.1d);
        getEntity().setJumpStrength(1.3);

        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            try {
                getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000000, 1));
            } catch (Exception ignored) {
            }
        }, 1);
    }

    @Override
    public void onUpdate() {
        Location loc = entity.getLocation().add(0, 1, 0);
        UtilParticles.display(Particles.FIREWORKS_SPARK, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.display(Particles.SPELL, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.display(Particles.SPELL_MOB_AMBIENT, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.display(Particles.SPELL_MOB, 5, 255, 0, loc);
    }

    @Override
    protected void onClear() {
        super.onClear();
        getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
    }
}
