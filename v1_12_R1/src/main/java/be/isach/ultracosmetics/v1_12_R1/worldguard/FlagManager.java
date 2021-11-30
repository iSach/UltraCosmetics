package be.isach.ultracosmetics.v1_12_R1.worldguard;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import be.isach.ultracosmetics.version.AFlagManager;

public class FlagManager extends AFlagManager {
    @Override
    protected void register() {
        FlagRegistry registry = WGBukkit.getPlugin().getFlagRegistry();
        registry.register(COSMETIC_FLAG);
        registry.register(TREASURE_FLAG);
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
    // from WorldGuard documentation:
    // https://worldguard.enginehub.org/en/latest/developer/regions/custom-flags/
    public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<CosmeticFlagHandler> {
        @Override
        public CosmeticFlagHandler create(Session session) {
            return new CosmeticFlagHandler(session, COSMETIC_FLAG);
        }
    }
}
