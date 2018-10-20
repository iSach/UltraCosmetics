package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.util.BlockUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author RadBuilder
 */
public class SpawnEggs {
	public static ItemStack getEggFromData(byte b) {
		if (VersionManager.IS_VERSION_1_13) {
			switch (b) {
				case (byte) 4:
					return new ItemStack(Material.ELDER_GUARDIAN_SPAWN_EGG);
				case (byte) 5:
					return new ItemStack(Material.WITHER_SKELETON_SPAWN_EGG);
				case (byte) 6:
					return new ItemStack(Material.STRAY_SPAWN_EGG);
				case (byte) 23:
					return new ItemStack(Material.HUSK_SPAWN_EGG);
				case (byte) 27:
					return new ItemStack(Material.ZOMBIE_VILLAGER_SPAWN_EGG);
				case (byte) 28:
					return new ItemStack(Material.SKELETON_HORSE_SPAWN_EGG);
				case (byte) 29:
					return new ItemStack(Material.ZOMBIE_HORSE_SPAWN_EGG);
				case (byte) 31:
					return new ItemStack(Material.DONKEY_SPAWN_EGG);
				case (byte) 32:
					return new ItemStack(Material.MULE_SPAWN_EGG);
				case (byte) 34:
					return new ItemStack(Material.EVOKER_SPAWN_EGG);
				case (byte) 35:
					return new ItemStack(Material.VEX_SPAWN_EGG);
				case (byte) 36:
					return new ItemStack(Material.VINDICATOR_SPAWN_EGG);
				case (byte) 50:
					return new ItemStack(Material.CREEPER_SPAWN_EGG);
				case (byte) 51:
					return new ItemStack(Material.SKELETON_HORSE_SPAWN_EGG);
				case (byte) 52:
					return new ItemStack(Material.SPIDER_SPAWN_EGG);
				case (byte) 54:
					return new ItemStack(Material.ZOMBIE_HORSE_SPAWN_EGG);
				case (byte) 55:
					return new ItemStack(Material.SLIME_SPAWN_EGG);
				case (byte) 56:
					return new ItemStack(Material.GHAST_SPAWN_EGG);
				case (byte) 57:
					return new ItemStack(Material.ZOMBIE_PIGMAN_SPAWN_EGG);
				case (byte) 58:
					return new ItemStack(Material.ENDERMAN_SPAWN_EGG);
				case (byte) 59:
					return new ItemStack(Material.CAVE_SPIDER_SPAWN_EGG);
				case (byte) 60:
					return new ItemStack(Material.SILVERFISH_SPAWN_EGG);
				case (byte) 61:
					return new ItemStack(Material.BLAZE_SPAWN_EGG);
				case (byte) 62:
					return new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG);
				case (byte) 65:
					return new ItemStack(Material.BAT_SPAWN_EGG);
				case (byte) 66:
					return new ItemStack(Material.WITCH_SPAWN_EGG);
				case (byte) 67:
					return new ItemStack(Material.ENDERMITE_SPAWN_EGG);
				case (byte) 68:
					return new ItemStack(Material.GUARDIAN_SPAWN_EGG);
				case (byte) 69:
					return new ItemStack(Material.SHULKER_SPAWN_EGG);
				case (byte) 90:
					return new ItemStack(Material.PIG_SPAWN_EGG);
				case (byte) 91:
					return new ItemStack(Material.SHEEP_SPAWN_EGG);
				case (byte) 92:
					return new ItemStack(Material.COW_SPAWN_EGG);
				case (byte) 93:
					return new ItemStack(Material.CHICKEN_SPAWN_EGG);
				case (byte) 94:
					return new ItemStack(Material.SQUID_SPAWN_EGG);
				case (byte) 95:
					return new ItemStack(Material.WOLF_SPAWN_EGG);
				case (byte) 96:
					return new ItemStack(Material.MOOSHROOM_SPAWN_EGG);
				case (byte) 98:
					return new ItemStack(Material.OCELOT_SPAWN_EGG);
				case (byte) 100:
					return new ItemStack(Material.HORSE_SPAWN_EGG);
				case (byte) 101:
					return new ItemStack(Material.RABBIT_SPAWN_EGG);
				case (byte) 102:
					return new ItemStack(Material.POLAR_BEAR_SPAWN_EGG);
				case (byte) 103:
					return new ItemStack(Material.LLAMA_SPAWN_EGG);
				case (byte) 105:
					return new ItemStack(Material.PARROT_SPAWN_EGG);
				case (byte) 120:
					return new ItemStack(Material.VILLAGER_SPAWN_EGG);
				default:
					return new ItemStack(BlockUtils.getOldMaterial("SPAWN_EGG"));
			}
		} else {
			return new ItemStack(BlockUtils.getOldMaterial("SPAWN_EGG"), 1, b);
		}
	}
}
