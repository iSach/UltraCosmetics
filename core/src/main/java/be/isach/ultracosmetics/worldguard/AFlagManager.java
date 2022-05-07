package be.isach.ultracosmetics.worldguard;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmetics.CosmeticRegionState;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

import java.util.Set;

public abstract class AFlagManager {
    protected static boolean registered = false;
    protected static final StateFlag TREASURE_FLAG = new StateFlag("uc-treasurechest", true);
    protected static final StateFlag COSMETIC_FLAG = new StateFlag("uc-cosmetics", true);
    protected static final StateFlag AFFECT_PLAYERS_FLAG = new StateFlag("uc-affect-players", true);
    protected static final SetFlag<Category> CATEGORY_FLAG = new SetFlag<>("uc-blocked-categories", new EnumFlag<>(null, Category.class));

    public AFlagManager() {
        if (!registered) register();
    }

    public boolean areCosmeticsAllowedHere(Player player, Category category) {
        return allowedCosmeticsState(player, category) == CosmeticRegionState.ALLOWED;
    }

    public CosmeticRegionState allowedCosmeticsState(Player player, Category category) {
        if (!flagCheck(COSMETIC_FLAG, player)) {
            return CosmeticRegionState.BLOCKED_ALL;
        }
        if (category != null && !categoryFlagCheck(player, category)) {
            return CosmeticRegionState.BLOCKED_CATEGORY;
        }
        return CosmeticRegionState.ALLOWED;
    }

    public boolean canAffectPlayersHere(Player player) {
        return flagCheck(COSMETIC_FLAG, player) && flagCheck(AFFECT_PLAYERS_FLAG, player);
    }

    public boolean areChestsAllowedHere(Player player) {
        return flagCheck(TREASURE_FLAG, player);
    }

    public void doCosmeticCheck(Player player, UltraCosmetics uc) {
        if (!flagCheck(COSMETIC_FLAG, player) && uc.getPlayerManager().getUltraPlayer(player).clear()) {
            player.sendMessage(MessageManager.getMessage("Region-Disabled"));
            return;
        }
        Set<Category> blockedCategories = categoryFlagCheck(player);
        if (blockedCategories == null) return;
        for (Category category : blockedCategories) {
            if (blockedCategories.contains(category) && uc.getPlayerManager().getUltraPlayer(player).removeCosmetic(category)) {
                player.sendMessage(MessageManager.getMessage("Region-Disabled-Category")
                        .replace("%category%", MessageManager.getMessage("Menu." + category.getConfigName() + ".Title")));
            }
        }
    }

    protected abstract void register();
    public abstract void registerPhase2();
    protected abstract boolean flagCheck(StateFlag flag, Player player);
    protected abstract Set<Category> categoryFlagCheck(Player player);
    protected boolean categoryFlagCheck(Player player, Category category) {
        Set<Category> categories = categoryFlagCheck(player);
        return categories == null || !categories.contains(category);
    }
}
