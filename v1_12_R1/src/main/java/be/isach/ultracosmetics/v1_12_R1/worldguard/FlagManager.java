package be.isach.ultracosmetics.v1_12_R1.worldguard;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.worldguard.AFlagManager;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import java.util.Set;

public class FlagManager extends AFlagManager {
    @Override
    protected void register() {
        FlagRegistry registry = WGBukkit.getPlugin().getFlagRegistry();
        registry.register(COSMETIC_FLAG);
        registry.register(TREASURE_FLAG);
        registry.register(CATEGORY_FLAG);
        registry.register(AFFECT_PLAYERS_FLAG);
    }

    @Override
    public void registerPhase2() {
        WGBukkit.getPlugin().getSessionManager().registerHandler(FACTORY, null);
    }

    @Override
    protected boolean flagCheck(StateFlag flag, Player bukkitPlayer) {
        RegionContainer rc = WGBukkit.getPlugin().getRegionContainer();
        RegionQuery query = rc.createQuery();
        return query.testState(bukkitPlayer.getLocation(), bukkitPlayer, flag);
    }

    @Override
    protected Set<Category> categoryFlagCheck(Player bukkitPlayer) {
        RegionContainer rc = WGBukkit.getPlugin().getRegionContainer();
        RegionQuery query = rc.createQuery();
        return query.queryValue(bukkitPlayer.getLocation(), bukkitPlayer, CATEGORY_FLAG);
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
}
