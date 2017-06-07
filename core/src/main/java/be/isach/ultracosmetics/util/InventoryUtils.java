package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import org.bukkit.inventory.Inventory;

/**
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 9/08/16
 * Project: UltraCosmetics
 */
public class InventoryUtils {
	
	public static boolean areSame(Inventory a, Inventory b) {
		return UltraCosmeticsData.get().getVersionManager().getEntityUtil().isSameInventory(a, b);
	}
	
}
