package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.CosmeticType;

import java.util.List;

import static be.isach.ultracosmetics.cosmetics.CosmeticType.*;

/**
 * Package: be.isach.ultracosmetics.menu
 * Created by: sachalewin
 * Date: 5/07/16
 * Project: UltraCosmetics
 */
public abstract class Menu<T extends CosmeticType> {

    private final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };

    public void open(UltraPlayer player, int page) {

    }

    public T getCosmeticType(String name) {
        for (T effectType : enabled()) {
            if (effectType.getConfigName().replace(" ", "").equals(name.replace(" ", "")))
                return effectType;
        }
        return null;
    }

    public abstract List<T> enabled();
}
