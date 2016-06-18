package be.isach.ultracosmetics.v1_9_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 11/10/15.
 */
public class MountSkySquid extends MountCustomEntity {

    public MountSkySquid(UUID owner) {
        super(owner, MountType.SKYSQUID);

    }

    @Override
    protected void onUpdate() {
        Random random = new Random();
        for (int i = 0; i < 5; i++)
            if (UltraCosmetics.usingSpigot())
                getPlayer().getWorld().spigot().playEffect(getCustomEntity().getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
                        MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)), Effect.POTION_BREAK, 0, 0, random.nextFloat(),
                        random.nextFloat(), random.nextFloat(), 1, 20, 64);
            else
                UtilParticles.display(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255),
                        getCustomEntity().getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
                                MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)));
    }
}
