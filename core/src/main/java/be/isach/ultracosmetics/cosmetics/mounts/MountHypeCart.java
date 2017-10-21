package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.PlayerUtils;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

/**
 * Represents an instance of a hype cart mount.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class MountHypeCart extends Mount<Minecart> {
	
	public MountHypeCart(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MountType.valueOf("hypecart"), ultraCosmetics);
	}
	
	@Override
	public void onUpdate() {
		if (entity.isOnGround())
			entity.setVelocity(PlayerUtils.getHorizontalDirection(getPlayer(), 7.6));
		UltraCosmeticsData.get().getVersionManager().getEntityUtil().setClimb(entity);
	}
	
	@EventHandler
	public void onDestroy(VehicleDestroyEvent event) {
		if (event.getVehicle() == entity) {
			event.setCancelled(true);
		}
	}
}
