package be.isach.ultracosmetics.v1_18_R2.worldguard;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.worldguard.AFlagManager;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import java.util.Set;

public class FlagManager extends AFlagManager {
    @Override
    protected void register() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(COSMETIC_FLAG);
        registry.register(TREASURE_FLAG);
        registry.register(AFFECT_PLAYERS_FLAG);
        registry.register(CATEGORY_FLAG);
    }

    @Override
    public void registerPhase2() {
        WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(FACTORY, null);
    }

    @Override
    protected boolean flagCheck(StateFlag flag, Player bukkitPlayer) {
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = rc.createQuery();
        return query.testState(player.getLocation(), player, flag);
    }

    // from WorldGuard documentation:
    // https://worldguard.enginehub.org/en/latest/developer/regions/custom-flags/
    private static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<CosmeticFlagHandler> {
        @Override
        public CosmeticFlagHandler create(Session session) {
            return new CosmeticFlagHandler(session, COSMETIC_FLAG, CATEGORY_FLAG);
        }
    }

    @Override
    protected Set<Category> categoryFlagCheck(Player bukkitPlayer) {
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = rc.createQuery();
        return query.queryValue(player.getLocation(), player, CATEGORY_FLAG);
    }
}
