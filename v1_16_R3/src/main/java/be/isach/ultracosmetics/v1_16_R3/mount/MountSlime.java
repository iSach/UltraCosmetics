package be.isach.ultracosmetics.v1_16_R3.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_16_R3.customentities.CustomSlime;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityTypes;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

/**
 * @author RadBuilder
 */
public class MountSlime extends MountCustomEntity {

    public MountSlime(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("slime"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public EntityLiving getNewEntity() {
        return new CustomSlime(EntityTypes.SLIME, ((CraftPlayer) getPlayer()).getHandle().getWorld());
    }
}
