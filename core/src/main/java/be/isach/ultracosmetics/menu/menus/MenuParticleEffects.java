package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu.menus
 * Created by: sachalewin
 * Date: 23/08/16
 * Project: UltraCosmetics
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
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {
        ParticleEffectType.getByName(name).equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        ultraPlayer.removeParticleEffect();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentParticleEffect();
    }
}
