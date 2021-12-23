package be.isach.ultracosmetics.v1_13_R2.worldguard;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
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
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, State value) {}

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet,
            State currentValue, State lastValue, MoveType moveType) {
        Player bukkitPlayer = ((BukkitPlayer)player).getPlayer();
        if (pm.getUltraPlayer(bukkitPlayer).clear()) {
            bukkitPlayer.sendMessage(MessageManager.getMessage("Region-Disabled"));
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet,
            State lastValue, MoveType moveType) {
        return true;
    }
}
