package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.PlayerUtils;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecartRideable;

import java.util.UUID;

public class MountHypeCart extends Mount {

    public MountHypeCart(UUID owner) {
        super(
                owner, MountType.HYPECART
        );
    }

    @Override
    void onUpdate() {
        if (ent.isOnGround())
            ent.setVelocity(PlayerUtils.getHorizontalDirection(getPlayer(), 7.6));
        ((CraftMinecartRideable) ent).getHandle().S = 1;
    }

    @Override
    public void onClear() {
    }
}
