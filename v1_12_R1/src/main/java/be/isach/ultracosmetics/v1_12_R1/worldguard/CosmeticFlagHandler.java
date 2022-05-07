package be.isach.ultracosmetics.v1_12_R1.worldguard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import java.util.Set;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.UltraPlayerManager;

public class CosmeticFlagHandler extends Handler {
    private final UltraPlayerManager pm;
    private final StateFlag cosmeticsFlag;
    private final SetFlag<Category> categoryFlag;
    private State lastCosmeticsFlagValue = null;
    private Set<Category> lastCategoryFlagValue = null;
    protected CosmeticFlagHandler(Session session, StateFlag cosmeticsFlag, SetFlag<Category> categoryFlag) {
        super(session);
        pm = UltraCosmeticsData.get().getPlugin().getPlayerManager();
        this.cosmeticsFlag = cosmeticsFlag;
        this.categoryFlag = categoryFlag;
    }

    @Override
    public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        // ensure player is a real player and not an NPC
        if (Bukkit.getPlayer(player.getUniqueId()) == null) return true;

        LocalPlayer wrappedPlayer = getSession().getManager().getPlugin().wrapPlayer(player);
        boolean categoriesRequiresUpdate = true;
        State currentValue = toSet.queryState(wrappedPlayer, cosmeticsFlag);
        Set<Category> categoryValue = toSet.queryValue(wrappedPlayer, categoryFlag);
        if (!currentValue.equals(lastCosmeticsFlagValue)) {
            lastCosmeticsFlagValue = currentValue;
            if (currentValue == State.DENY) {
                // if the player is stripped of all cosmetics by the general flag anyway, no need to re-check the more granular flag
                categoriesRequiresUpdate = false;
                if (pm.getUltraPlayer(player).clear()) {
                    player.sendMessage(MessageManager.getMessage("Region-Disabled"));
                }
            }
        }
        if (!categoryValue.equals(lastCategoryFlagValue)) {
            // if lastCategoryFlagValue was as restrictive or more restrictive than categoryValue, nothing needs to be updated.
            if (categoriesRequiresUpdate && lastCategoryFlagValue != null && lastCategoryFlagValue.containsAll(categoryValue)) {
                categoriesRequiresUpdate = false;
            }
            lastCategoryFlagValue = categoryValue;
            if (categoriesRequiresUpdate) {
                UltraPlayer up = pm.getUltraPlayer(player);
                for (Category cat : categoryValue) {
                    if (up.removeCosmetic(cat)) {
                        player.sendMessage(MessageManager.getMessage("Region-Disabled-Category").replace("%category%", cat.getConfigName()));
                    }
                }
            }
        }
        return true;
    }
}
