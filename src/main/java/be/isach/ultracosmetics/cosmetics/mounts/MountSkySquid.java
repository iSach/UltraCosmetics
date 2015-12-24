package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Effect;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 11/10/15.
 */
public class MountSkySquid extends Mount {

    public MountSkySquid(UUID owner) {
        super(owner, MountType.SKYSQUID);

    }

    @Override
    void onUpdate() {
        Random random = new Random();
        for (int i = 0; i < 5; i++)
            getPlayer().getWorld().spigot().playEffect(customEnt.getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
                            MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)), Effect.POTION_SWIRL, 0, 0, random.nextFloat(),
                    random.nextFloat(), random.nextFloat(), 1, 20, 64);
    }
}
