package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.MathUtils;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 26/08/15.
 */
public class MorphSlime extends Morph {

    private boolean cooldown;

    public MorphSlime(UUID owner) {
        super(owner, MorphType.SLIME);
        UltraCosmetics.getInstance().registerListener(this);
        if(owner != null) {
            UltraCosmetics.getInstance().registerListener(this);
            SlimeWatcher slimeWatcher = (SlimeWatcher)disguise.getWatcher();
            slimeWatcher.setSize(3);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if(event.getPlayer() == getPlayer() && UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph == this && event.getReason().equalsIgnoreCase("Flying is not enabled on this server"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph == this && !cooldown) {
            MathUtils.applyVelocity(getPlayer(), new Vector(0, 2.3, 0));
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 80);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() == getPlayer() && UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph == this && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }

}
