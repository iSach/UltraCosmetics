package be.isach.ultracosmetics.v1_8_R2.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * @author RadBuilder
 */
public class MountSkySquid extends MountCustomEntity {

    public MountSkySquid(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.SKYSQUID, ultraCosmetics);

    }

    @Override
    public void onUpdate() {
//        Random random = new Random();
//        for (int i = 0; i < 5; i++)
//            if (UltraCosmetics.usingSpigot())
//                getPlayer().getWorld().spigot().playEffect(getCustomEntity().getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
//                        MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)), Effect.POTION_BREAK, 0, 0, random.nextFloat(),
//                        random.nextFloat(), random.nextFloat(), 1, 20, 64);
//            else
//                UtilParticles.display(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255),
//                        getCustomEntity().getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
//                                MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)));
    }

    @Override
    protected void onClear() {

    }
}
