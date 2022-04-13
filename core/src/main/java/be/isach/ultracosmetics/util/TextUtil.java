package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import org.bukkit.ChatColor;

/**
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class TextUtil {

    public static String filterPlaceHolder(String placeHolderReplacement) {
        return UltraCosmeticsData.get().arePlaceholdersColored() ? placeHolderReplacement : ChatColor.stripColor(placeHolderReplacement);
    }
}
