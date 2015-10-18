package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.util.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecartRideable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class MountHypeCart extends Mount {

    public MountHypeCart(UUID owner) {
        super(EntityType.MINECART, Material.MINECART, (byte) 0, "HypeCart",
                "ultracosmetics.mounts.hypecart", owner, MountType.HYPECART);
    }

    @Override
    void onUpdate() {
        if (ent.isOnGround())
            ent.setVelocity(PlayerUtils.getHorizontalDirection(getPlayer()));
        ((CraftMinecartRideable)ent).getHandle().S = 1;
    }

    @Override
    public void onClear() {
    }
}
