package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Mount {@link be.isach.ultracosmetics.menu.Menu Menu}.
 * 
 * @author 	iSach
 * @since 	08-23-2016
 */
public class MenuMounts extends CosmeticMenu<MountType> {

    public MenuMounts(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MOUNTS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
    }

    @Override
    public List<MountType> enabled() {
        return MountType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {
        MountType.getByName(name).equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        if(ultraPlayer.getCurrentMount() == null) {
            return;
        }
        ultraPlayer.getCurrentMount().setBeingRemoved(true);
        ultraPlayer.removeMount();
    }

    @Override
    protected String getTypeName(MountType cosmeticType, UltraPlayer ultraPlayer) {
        return cosmeticType.getMenuName();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentMount();
    }
}
