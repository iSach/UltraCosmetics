package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Particle Effect {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuParticleEffects extends CosmeticMenu<ParticleEffectType> {

    public MenuParticleEffects(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.EFFECTS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
    }

    @Override
    public List<ParticleEffectType> enabled() {
        return ParticleEffectType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, ParticleEffectType particleEffectType, UltraCosmetics ultraCosmetics) {
        particleEffectType.equip(ultraPlayer, ultraCosmetics);
    }
}
