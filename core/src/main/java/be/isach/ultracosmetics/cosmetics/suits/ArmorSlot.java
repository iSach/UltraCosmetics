package be.isach.ultracosmetics.cosmetics.suits;

import org.bukkit.ChatColor;

/**
 * Armor slot enum.
 *
 * @author iSach
 * @since 12-20-2015
 */
public enum ArmorSlot {
	HELMET,
	CHESTPLATE,
	LEGGINGS,
	BOOTS;
	
	public static ArmorSlot getByName(String s) {
		for (ArmorSlot a : ArmorSlot.values()) {
			if (a.toString().equalsIgnoreCase(ChatColor.stripColor(s)))
				return a;
		}
		return null;
	}
}
