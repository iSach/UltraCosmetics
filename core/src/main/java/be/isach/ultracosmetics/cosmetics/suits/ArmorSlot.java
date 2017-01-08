<<<<<<< HEAD
package be.isach.ultracosmetics.cosmetics.suits;

/**
 * Created by Sacha on 20/12/15.
 */
public enum ArmorSlot {

    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS

}
=======
package be.isach.ultracosmetics.cosmetics.suits;

import org.bukkit.ChatColor;

/**
 * Created by Sacha on 20/12/15.
 */
public enum ArmorSlot {
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS;
	
	public static ArmorSlot getByName(String s) {
       for (ArmorSlot a : ArmorSlot.values()){
    	   if (a.toString().equalsIgnoreCase(ChatColor.stripColor(s)))
    		   return a;
       }
       return null;
    }
}
>>>>>>> refs/remotes/origin/master
