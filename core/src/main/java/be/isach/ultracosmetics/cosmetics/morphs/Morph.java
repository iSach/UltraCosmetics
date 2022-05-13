package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

/**
 * Represents an instance of a morph summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class Morph extends Cosmetic<MorphType> {

    /**
     * The MobDiguise
     *
     * @see me.libraryaddict.disguise.disguisetypes.MobDisguise MobDisguise from Lib's Disguises
     */
    protected MobDisguise disguise;

    public Morph(UltraPlayer owner, MorphType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MORPHS, owner, type);
    }

    @Override
    protected void onEquip() {

        disguise = new MobDisguise(getType().getDisguiseType());
        FlagWatcher watcher = disguise.getWatcher();
        watcher.setCustomName(getPlayer().getName());
        watcher.setCustomNameVisible(true);

        disguise.setViewSelfDisguise(getOwner().canSeeSelfMorph());

        DisguiseAPI.disguiseToAll(getPlayer(), disguise);
    }

    /**
     * Called when Morph is cleared.
     */
    @Override
    public void clear() {
        DisguiseAPI.undisguiseToAll(getPlayer());
        super.clear();
    }

    /**
     * @return Disguise.
     */
    public MobDisguise getDisguise() {
        return disguise;
    }

    public void setSeeSelf(boolean enabled) {
        disguise.setViewSelfDisguise(enabled);
    }
}
