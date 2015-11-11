package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
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
        World world = customEnt.getBukkitEntity().getWorld();
        Random random = new Random();
        for (int i = 0; i < 5; i++)
            world.spigot().playEffect(customEnt.getBukkitEntity().getLocation().add(MathUtils.randomDouble(-2, 2),
                            MathUtils.randomDouble(-1, 1.3), MathUtils.randomDouble(-2, 2)), Effect.POTION_SWIRL, 0, 0, random.nextFloat(),
                    random.nextFloat(), random.nextFloat(), 1, 20, 64);
    }
}
