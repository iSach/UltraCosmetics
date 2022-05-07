package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface PlayerAffectingCosmetic {
    public default boolean canAffect(Entity entity) {
        if (!isAffectingPlayersEnabled()) {
            Bukkit.getLogger().info("settings check false");
            return false;
        }
        if (entity.hasMetadata("NPC") || entity.hasMetadata("Pet") || entity.hasMetadata("Mount")) {
            Bukkit.getLogger().info("bad meta");
            return false;
        }
        if (entity instanceof Player) {
            Bukkit.getLogger().info("player checks");
            Player target = (Player) entity;
            // alternate NPC check
            if (Bukkit.getPlayer(target.getUniqueId()) == null) {
                Bukkit.getLogger().info("fake player");
                return false;
            }
            if (!getSelf().getUltraCosmetics().getPlayerManager().getUltraPlayer(target).hasGadgetsEnabled()) {
                Bukkit.getLogger().info("target has gadgets disabled");
                return false;
            }
            if (!getSelf().getUltraCosmetics().arePlayersAffectedInRegion(target)) {
                Bukkit.getLogger().info("bad region");
                return false;
            }
        // if the entity is neither a player nor a creature, just skip it
        } else if (!(entity instanceof Creature)) {
            Bukkit.getLogger().info("bad entity type");
            return false;
        }
        Bukkit.getLogger().info("effect permitted");
        return true;
    }

    public default boolean isAffectingPlayersEnabled() {
        CosmeticType<?> type = getSelf().getType();
        return SettingsManager.getConfig().getBoolean(type.getCategory().getConfigPath() + "." + type.getConfigName() + ".Affect-Players");
    }

    // Is this good interface design? Probably not,
    // but I'm not sure of a better way to do it.
    public default Cosmetic<?> getSelf() {
        return (Cosmetic<?>)this;
    }
}
