package me.isach.ultracosmetics.cosmetics.morphs;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.util.MathUtils;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 26/08/15.
 */
public class MorphSlime extends Morph {

    private boolean cooldown;

    public MorphSlime(UUID owner) {
        super(DisguiseType.SLIME, Material.SLIME_BLOCK, (byte) 0x0, "Slime", "ultracosmetics.morphs.slime", owner, MorphType.SLIME);
        Core.registerListener(this);
        if(owner != null) {
            Core.registerListener(this);
            SlimeWatcher slimeWatcher = (SlimeWatcher)disguise.getWatcher();
            slimeWatcher.setSize(3);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if(event.getPlayer() == getPlayer() && Core.getCustomPlayer(getPlayer()).currentMorph == this && event.getReason().equalsIgnoreCase("Flying is not enabled on this server"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && Core.getCustomPlayer(getPlayer()).currentMorph == this && !cooldown) {
            MathUtils.applyVelocity(getPlayer(), new Vector(0, 2.3, 0));
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 80);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() == getPlayer() && Core.getCustomPlayer(getPlayer()).currentMorph == this && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }

}
