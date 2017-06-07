package be.isach.ultracosmetics.v1_8_R2.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Slime;

/**
 * @author RadBuilder
 */
public class MountSlime extends MountCustomEntity {

	public MountSlime(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MountType.SLIME, ultraCosmetics);
	}

	@Override
	public void onUpdate() {
		((Slime) getEntity()).setSize(3);
	}
}
