package be.isach.ultracosmetics.v1_14_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_14_R1.customentities.CustomSlime;
import net.minecraft.server.v1_14_R1.EntityTypes;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Slime;

/**
 * @author RadBuilder
 */
public class MountSlime extends MountCustomEntity<Slime> {

    public MountSlime(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("slime"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public IMountCustomEntity getNewEntity() {
        return new CustomSlime(EntityTypes.SLIME, ((CraftPlayer) getPlayer()).getHandle().getWorld());
    }
}
