package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Represents an instance of crushed candy cane particles summoned by a player.
 *
 * @author iSach
 * @since 12-18-2015
 */
public class ParticleEffectCrushedCandyCane extends ParticleEffect {

    private int step;

    private static Random random = new Random();

    public ParticleEffectCrushedCandyCane(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.valueOf("crushedcandycane"));
    }

    @Override
    public void onUpdate() {
        if (step > 360)
            step = 0;
        Location center = getPlayer().getEyeLocation().add(0, 0.6, 0);
        double inc = (2 * Math.PI) / 20;
        double angle = step * inc;
        double x = Math.cos(angle) * 1.1f;
        double z = Math.sin(angle) * 1.1f;
        center.add(x, 0, z);
        if (UltraCosmeticsData.get().getServerVersion().compareTo(ServerVersion.v1_14_R1) >= 0) {
            for (int i = 0; i < 15; i++) {
                ItemStack randomDye = UCMaterial.DYES.get(MathUtils.random(0, 14)).parseItem();
				getPlayer().getWorld().spawnParticle(Particle.ITEM_CRACK, getPlayer().getEyeLocation(), 1, 0.2d, 0.2d, 0.2d,
						0, randomDye);
            }
        } else {
            if (VersionManager.IS_VERSION_1_13) {
                for (int i = 0; i < 15; i++)
                    Particles.ITEM_CRACK.display(new Particles.ItemData(BlockUtils.getDyeByColor(getRandomColor()), getRandomColor()), 0.2f, 0.2f, 0.2f, 0, 1, center, 128);
            } else {
                for (int i = 0; i < 15; i++)
                    Particles.ITEM_CRACK.display(new Particles.ItemData(BlockUtils.getOldMaterial("INK_SACK"), getRandomColor()), 0.2f, 0.2f, 0.2f, 0, 1, center, 128);
            }
        }
        step++;
    }

    public static byte getRandomColor() {
        float f = random.nextFloat();
        if (f > 0.98)
            return (byte) 2;
        else if (f > 0.49)
            return (byte) 1;
        else
            return (byte) 15;
    }
}
