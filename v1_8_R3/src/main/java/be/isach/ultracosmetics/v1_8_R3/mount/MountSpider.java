package be.isach.ultracosmetics.v1_8_R3.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Created by Sacha on 18/10/15.
 */
public class MountSpider extends MountCustomEntity {
	public MountSpider(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MountType.valueOf("spider"), ultraCosmetics);
	}

	@Override
	public void onUpdate() {}
}
