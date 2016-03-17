package be.isach.ultracosmetics.cosmetics.mounts;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 11/10/15.
 */
public class MountSkySquid_1_9_R1 extends MountCustomEntity_1_8_R3 {

    public MountSkySquid_1_9_R1(UUID owner) {
        super(owner, MountType.SKYSQUID);

    }

    @Override
    void onUpdate() {
        Random random = new Random();
//        for (int i = 0; i < 5; i++)
//            if (UltraCosmetics.usingSpigot())
//                getPlayer().getWorld().spigot().playEffect(customEnt.getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
//                        MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)), Effect.POTION_BREAK, 0, 0, random.nextFloat(),
//                        random.nextFloat(), random.nextFloat(), 1, 20, 64);
//            else
//                UtilParticles.display(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255),
//                        customEnt.getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
//                                MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)));
    }
}
