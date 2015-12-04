package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecartRideable;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class MountHypeCart extends Mount {

    public MountHypeCart(UUID owner) {
        super(EntityType.MINECART, Material.MINECART, (byte) 0, "HypeCart",
                "ultracosmetics.mounts.hypecart", owner, MountType.HYPECART,
                "&7&oNow you can be in a F1!");
    }

    @Override
    void onUpdate() {
        if (ent.isOnGround())
            ent.setVelocity(PlayerUtils.getHorizontalDirection(getPlayer()).multiply(1.6));
        ((CraftMinecartRideable)ent).getHandle().S = 1;
    }

    @Override
    public void onClear() {
    }
}
