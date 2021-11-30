package be.isach.ultracosmetics.v1_12_R1.worldguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayerManager;

public class CosmeticFlagHandler extends FlagValueChangeHandler<State> {
    private UltraPlayerManager pm;
    protected CosmeticFlagHandler(Session session, StateFlag flag) {
        super(session, flag);
        pm = UltraCosmeticsData.get().getPlugin().getPlayerManager();
    }

    @Override
    protected void onInitialValue(Player player, ApplicableRegionSet set, State value) {}

    @Override
    protected boolean onSetValue(Player player, Location from, Location to, ApplicableRegionSet toSet,
            State currentValue, State lastValue, MoveType moveType) {
        if (pm.getUltraPlayer(player).clear()) {
            player.sendMessage(MessageManager.getMessage("Region-Disabled"));
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(Player player, Location from, Location to, ApplicableRegionSet toSet,
            State lastValue, MoveType moveType) {
        return true;
    }
}
