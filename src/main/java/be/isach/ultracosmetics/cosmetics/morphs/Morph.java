package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Morph implements Listener {

    private Material material;
    private Byte data;
    private String name;
    public String permission;

    private MorphType type;

    public DisguiseType disguiseType;
    public MobDisguise disguise;

    public UUID owner;

    public Morph(DisguiseType disguiseType, Material material, Byte data, String configName, String permission, final UUID owner, final MorphType type) {
        this.material = material;
        this.data = data;
        this.name = configName;
        this.permission = permission;
        this.type = type;
        this.disguiseType = disguiseType;
        if (owner != null) {
            this.owner = owner;
            if (Core.getCustomPlayer(getPlayer()).currentMorph != null)
                Core.getCustomPlayer(getPlayer()).removeMorph();

            getPlayer().sendMessage(MessageManager.getMessage("Morphs.Morph").replace("%morphname%", (Core.placeHolderColor)?getName():Core.filterColor(getName())));
            Core.getCustomPlayer(getPlayer()).currentMorph = this;
            if (!getPlayer().hasPermission(permission)) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }

            // Disguise the player
            disguise = new MobDisguise(disguiseType);
            DisguiseAPI.disguiseToAll(getPlayer(), disguise);
            if (!Core.getCustomPlayer(getPlayer()).canSeeSelfMorph())
                disguise.setViewSelfDisguise(false);
            disguise.setModifyBoundingBox(true);
            disguise.setShowName(true);
        }
    }

    public String getConfigName() {
        return name;
    }

    public String getName() {
        return MessageManager.getMessage("Morphs." + name + ".name");
    }

    public Material getMaterial() {
        return this.material;
    }

    public MorphType getType() {
        return this.type;
    }

    public Byte getData() {
        return this.data;
    }

    public void clear() {
        DisguiseAPI.undisguiseToAll(getPlayer());
        Core.getCustomPlayer(getPlayer()).currentMorph = null;
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Morphs.Unmorph").replace("%morphname%", (Core.placeHolderColor)?getName():Core.filterColor(getName())));
        owner = null;
        try {
            HandlerList.unregisterAll(this);
        } catch (Exception exc) {
        }
    }

    protected UUID getOwner() {
        return owner;
    }

    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public enum MorphType {

        BAT("ultracosmetics.morphs.bat", "Bat"),
        BLAZE("ultracosmetics.morphs.blaze", "Blaze"),
        CHICKEN("ultracosmetics.morphs.chicken", "Chicken"),
        PIG("ultracosmetics.morphs.pig", "Pig"),
        ENDERMAN("ultracosmetics.morphs.enderman", "Enderman"),
        SLIME("ultracosmetics.morphs.slime", "Slime"),
        CREEPER("ultracosmetics.morphs.creeper", "Creeper"),
        WITHERSKELETON("ultracosmetics.morphs.witherskeleton", "WitherSkeleton");

        String permission;
        String configName;

        MorphType(String permission, String configName) {
            this.permission = permission;
            this.configName = configName;
        }

        public String getPermission() {
            return permission;
        }

        public String getSkill() {
            return MessageManager.getMessage("Morphs." + configName + ".skill");
        }

        public boolean isEnabled() {
            return SettingsManager.getConfig().get("Morphs." + configName + ".Enabled");
        }

    }

}
