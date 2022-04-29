package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by sacha on 10/08/15.
 */
public class MountDruggedHorse extends MountHorse {

    private Player effectPlayer;

    public MountDruggedHorse(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("druggedhorse"), ultraCosmetics);
    }

    @Override
    public void setupEntity() {
        super.setupEntity();

        ((Horse)getEntity()).setJumpStrength(1.3);

        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000000, 1));
            effectPlayer = getPlayer();
        }, 1);
    }

    @Override
    public void onUpdate() {
        Location loc = entity.getLocation().add(0, 1, 0);
        Particles.FIREWORKS_SPARK.display(0.4f, 0.2f, 0.4f, loc, 5);
        Particles.SPELL.display(0.4f, 0.2f, 0.4f, loc, 5);
        Particles.SPELL_MOB_AMBIENT.display(0.4f, 0.2f, 0.4f, loc, 5);
        Particles.SPELL_MOB.display(5, 255, 0, loc);
    }

    @Override
    protected void onClear() {
        if (effectPlayer != null) {
            effectPlayer.removePotionEffect(PotionEffectType.CONFUSION);
        }
        super.onClear();
    }

    @Override
    protected Horse.Color getColor() {
        return Horse.Color.CHESTNUT;
    }
}
