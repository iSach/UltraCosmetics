package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.menu.menus.*;

/**
 * Package: be.isach.ultracosmetics.menu
 * Created by: sachalewin
 * Date: 23/08/16
 * Project: UltraCosmetics
 * <p>
 * Stores menus.
 */
public class Menus {

    private final MenuEmotes emotesMenu;
    private final MenuGadgets gadgetsMenu;
    private final MenuHats hatsMenu;
    private final MenuMorphs morphsMenu;
    private final MenuMounts mountsMenu;
    private final MenuParticleEffects effectsMenu;
    private final MenuPets petsMenu;
    private final MenuSuits suitsMenu;
    private final MenuMain mainMenu;

    private UltraCosmetics ultraCosmetics;

    public Menus(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;

        this.emotesMenu = new MenuEmotes(ultraCosmetics);
        this.gadgetsMenu = new MenuGadgets(ultraCosmetics);
        this.effectsMenu = new MenuParticleEffects(ultraCosmetics);
        this.hatsMenu = new MenuHats(ultraCosmetics);
        this.morphsMenu = new MenuMorphs(ultraCosmetics);
        this.mountsMenu = new MenuMounts(ultraCosmetics);
        this.petsMenu = new MenuPets(ultraCosmetics);
        this.suitsMenu = new MenuSuits(ultraCosmetics);
        this.mainMenu = new MenuMain(ultraCosmetics);
    }

    public MenuEmotes getEmotesMenu() {
        return emotesMenu;
    }

    public MenuMain getMainMenu() {
        return mainMenu;
    }

    public MenuGadgets getGadgetsMenu() {
        return gadgetsMenu;
    }

    public MenuHats getHatsMenu() {
        return hatsMenu;
    }

    public MenuMorphs getMorphsMenu() {
        return morphsMenu;
    }

    public MenuMounts getMountsMenu() {
        return mountsMenu;
    }

    public MenuParticleEffects getEffectsMenu() {
        return effectsMenu;
    }

    public MenuPets getPetsMenu() {
        return petsMenu;
    }

    public MenuSuits getSuitsMenu() {
        return suitsMenu;
    }
}
