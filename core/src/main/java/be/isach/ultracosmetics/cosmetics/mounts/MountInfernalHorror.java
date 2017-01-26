package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.entity.Horse;
import org.bukkit.entity.SkeletonHorse;

/**
* Represents an instance of a infernal horror mount.
 * 
 * @author 	iSach
 * @since 	08-10-2015
 */
public class MountInfernalHorror extends MountHorse<SkeletonHorse> {

    public MountInfernalHorror(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.INFERNALHORROR, ultraCosmetics);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        entity.setJumpStrength(0.7);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().setHorseSpeed(entity, 0.4d);
    }

    @Override
    public void onUpdate() {
        UtilParticles.display(Particles.FLAME, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }

    @Override
    protected Horse.Variant getVariant() {
        return Horse.Variant.SKELETON_HORSE;
    }

    @Override
    protected Horse.Color getColor() {
        return Horse.Color.WHITE;
    }
}
