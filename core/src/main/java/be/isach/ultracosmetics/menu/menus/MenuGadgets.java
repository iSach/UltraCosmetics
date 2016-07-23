package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.menu.Menu;

import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu.menus
 * Created by: sachalewin
 * Date: 23/07/16
 * Project: UltraCosmetics
 */
public class MenuGadgets extends Menu<GadgetType> {

    @Override
    public List<GadgetType> enabled() {
        return GadgetType.enabled();
    }
}
