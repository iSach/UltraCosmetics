package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Morph implements Listener {

    /**
     * The Morph Type.
     */
    private MorphType type;

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

    public Morph(UUID owner, MorphType type) {
        this.type = type;

        if (owner == null) return;

        this.owner = owner;

        if (Core.getCustomPlayer(getPlayer()).currentMorph != null)
            Core.getCustomPlayer(getPlayer()).removeMorph();

        if (!getPlayer().hasPermission(getType().getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }

        getPlayer().sendMessage(MessageManager.getMessage("Morphs.Morph").replace("%morphname%", (Core.placeHolderColor) ?
                getType().getName() : Core.filterColor(getType().getName())));
        Core.getCustomPlayer(getPlayer()).currentMorph = this;

        disguise = new MobDisguise(getType().getDisguiseType());
        DisguiseAPI.disguiseToAll(getPlayer(), disguise);
        if (!Core.getCustomPlayer(getPlayer()).canSeeSelfMorph())
            disguise.setViewSelfDisguise(false);
        disguise.setModifyBoundingBox(true);
        disguise.setShowName(true);
    }

    /**
     * Called when Morph is cleared.
     */
    public void clear() {
        DisguiseAPI.undisguiseToAll(getPlayer());
        Core.getCustomPlayer(getPlayer()).currentMorph = null;
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Morphs.Unmorph").replace("%morphname%", (Core.placeHolderColor) ?
                    getType().getName() : Core.filterColor(getType().getName())));
        owner = null;
        try {
            HandlerList.unregisterAll(this);
        } catch (Exception exc) {
        }
    }

    /**
     * Get the type of the Morph.
     *
     * @return
     */
    public MorphType getType() {
        return this.type;
    }

    /**
     * Get the Owner UUID.
     *
     * @return The Owner's UUID.
     */
    protected final UUID getOwner() {
        return owner;
    }

    /**
     * Get the owner as a Player.
     *
     * @return The Player who owns the morph.
     */
    protected final Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }
}
