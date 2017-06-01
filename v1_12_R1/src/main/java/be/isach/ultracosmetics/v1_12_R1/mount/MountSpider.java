package be.isach.ultracosmetics.v1_12_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * @author RadBuilder
 */
public class MountSpider extends MountCustomEntity {
	public MountSpider(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MountType.SPIDER, ultraCosmetics);
	}
	
	@Override
	public void onUpdate() {
	}
}
