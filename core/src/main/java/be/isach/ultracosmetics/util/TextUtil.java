package be.isach.ultracosmetics.util;

import org.bukkit.ChatColor;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;

/**
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class TextUtil {

    public static String filterPlaceHolder(String placeHolderReplacement, UltraCosmetics ultraCosmetics) {
        return UltraCosmeticsData.get().arePlaceholdersColored() ? placeHolderReplacement : "" + filterColor(placeHolderReplacement);
    }

    /**
     * Removes color in a text.
     *
     * @param toFilter The text to filter.
     * @return The filtered text.
     */
    private static CharSequence filterColor(String toFilter) {
        return ChatColor.stripColor(toFilter);
    }
}
