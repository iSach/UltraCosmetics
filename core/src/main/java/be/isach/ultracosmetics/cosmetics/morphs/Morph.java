package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Represents an instance of a morph summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-03-2015
 */
public abstract class Morph extends Cosmetic<MorphType> implements Updatable {

    /**
     * The MobDiguise
     *
     * @see me.libraryaddict.disguise.disguisetypes.MobDisguise MobDisguise from Lib's Disguises
     */
    public MobDisguise disguise;

    /**
     * The Morph Owner.
     */
    public UUID owner;

    public Morph(UltraPlayer owner, MorphType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MORPHS, owner, type);
    }

    @Override
    public void equip() {

        super.equip();
        
        if (getOwner().getCurrentMorph() != null) {
            getOwner().removeMorph();
        }

        disguise = new MobDisguise(getType().getDisguiseType());
        DisguiseAPI.disguiseToAll(getPlayer(), disguise);

        if (!getOwner().canSeeSelfMorph()) {
            disguise.setViewSelfDisguise(false);
        }

        runTaskTimer(getUltraCosmetics(), 0, 1);

        getOwner().setCurrentMorph(this);
       
    }

    @Override
    public void run() {
        if (getPlayer() == null || getOwner().getCurrentMorph() != this) {
            return;
        }

        onUpdate();
    }

    /**
     * Called when Morph is cleared.
     */
    @Override
    public void clear() {
        DisguiseAPI.undisguiseToAll(getPlayer());
        getOwner().setCurrentMorph(null);
        super.clear();
    }

    /**
     * @return Disguise.
     */
    public MobDisguise getDisguise() {
        return disguise;
    }
}
