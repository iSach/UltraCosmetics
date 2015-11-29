package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 11/10/15.
 */
public class MountSkySquid extends Mount {

    public MountSkySquid(UUID owner) {
        super(EntityType.SQUID, Material.INK_SACK, (byte) 0, "SkySquid", "ultracosmetics.mounts.skysquid", owner, MountType.SKYSQUID, "&7&oWat.");

    }

    @Override
    void onUpdate() {
        Random random = new Random();
        for (int i = 0; i < 5; i++)
            UtilParticles.display(Particles.SPELL_MOB_AMBIENT,
                    random.nextInt(256), random.nextInt(256),
                    random.nextInt(256),
                    customEnt.getBukkitEntity().getLocation()
                            .add(MathUtils.randomDouble(-2, 2),
                                    MathUtils.randomDouble(-1, 1.3),
                                    MathUtils.randomDouble(-2, 2)), 20);
    }
}
