package be.isach.ultracosmetics.version;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.StateFlag;

public abstract class AFlagManager {
    protected static boolean registered = false;
    protected static final StateFlag TREASURE_FLAG = new StateFlag("uc-treasurechest", true);
    protected static final StateFlag COSMETIC_FLAG = new StateFlag("uc-cosmetics", true);
    public AFlagManager() {
        if (!registered) register();
    }

    public StateFlag getTreasureFlag() {
        return TREASURE_FLAG;
    }

    public StateFlag getCosmeticFlag() {
        return COSMETIC_FLAG;
    }

    public boolean areCosmeticsAllowedHere(Player player) {
        return flagCheck(COSMETIC_FLAG, player);
    }

    public boolean areChestsAllowedHere(Player player) {
        return flagCheck(TREASURE_FLAG, player);
    }

    protected abstract void register();
    public abstract void registerPhase2();
    protected abstract boolean flagCheck(StateFlag flag, Player player);
}
