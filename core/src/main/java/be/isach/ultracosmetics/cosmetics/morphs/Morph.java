package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.util.TextUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Morph extends Cosmetic<MorphType> {

    /**
     * The MobDiguise
     *
     * @see MobDisguise (from Lib's Disguises)
     */
    public MobDisguise disguise;

    /**
     * The Morph Owner.
     */
    public UUID owner;

    public Morph(UltraPlayer owner, MorphType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MORPHS, owner, type);

        if (owner.getCurrentMorph() != null) {
            owner.removeMorph();
        }

        getPlayer().sendMessage(MessageManager.getMessage("Morphs.Morph").replace("%morphname%", TextUtil.filterPlaceHolder(getType().getName(), getUCInstance())));
        owner.setCurrentMorph(this);

        disguise = new MobDisguise(getType().getDisguiseType());
        DisguiseAPI.disguiseToAll(getPlayer(), disguise);

        if (!owner.canSeeSelfMorph()) {
            disguise.setViewSelfDisguise(false);
        }
    }

    /**
     * Called when Morph is cleared.
     */
    @Override
    public void onClear() {
        DisguiseAPI.undisguiseToAll(getPlayer());
        getOwner().setCurrentMorph(null);
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Morphs.Unmorph").replace("%morphname%", TextUtil.filterPlaceHolder(getType().getName(), getUCInstance())));
        owner = null;
        try {
            HandlerList.unregisterAll(this);
        } catch (Exception exc) {
        }
    }

    /**
     * @return Disguise.
     */
    public MobDisguise getDisguise() {
        return disguise;
    }
}
