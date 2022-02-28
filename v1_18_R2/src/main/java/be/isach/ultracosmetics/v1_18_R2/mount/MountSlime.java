package be.isach.ultracosmetics.v1_18_R2.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_18_R2.customentities.CustomSlime;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
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
    public LivingEntity getNewEntity() {
        return new CustomSlime(EntityType.SLIME, ((CraftPlayer) getPlayer()).getHandle().getLevel());
    }
}
