package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Represents an instance of a glacial steed mount.
 *
 * @author iSach
 * @since 08-10-2015
 */
public class MountGlacialSteed extends MountHorse {
	
	public MountGlacialSteed(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MountType.valueOf("glacialsteed"), ultraCosmetics);
	}
	
	@Override
	public void onEquip() {
		super.onEquip();
		entity.setJumpStrength(0.7);
		UltraCosmeticsData.get().getVersionManager().getEntityUtil().setHorseSpeed(entity, 0.4d);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getPlayer() == getPlayer()
		    && getOwner().getCurrentMount() == this
		    && (boolean) SettingsManager.getConfig().get("Mounts-Block-Trails")) {
			for (Block b : BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false)) {
				if (b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1) {
					BlockUtils.setToRestore(b, Material.SNOW_BLOCK, (byte) 0x0, 20);
				}
			}
		}
	}
	
	@Override
	public void onUpdate() {
		UtilParticles.display(Particles.SNOW_SHOVEL, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
	}
	
	@Override
	protected Horse.Variant getVariant() {
		return Horse.Variant.HORSE;
	}
	
	@Override
	protected Horse.Color getColor() {
		return Horse.Color.WHITE;
	}
}