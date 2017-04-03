package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

/**
* Represents an instance of a slime morph summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-26-2015
 */
public class MorphSlime extends Morph {

    private boolean cooldown;

    public MorphSlime(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.SLIME, ultraCosmetics);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (event.getPlayer() == getPlayer() && getOwner().getCurrentMorph() == this && event.getReason().equalsIgnoreCase("Flying is not enabled on this server"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && getOwner().getCurrentMorph() == this && !cooldown) {
            MathUtils.applyVelocity(getPlayer(), new Vector(0, 2.3, 0));
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 80);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() == getPlayer() && getOwner().getCurrentMorph() == this && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }

    @Override
    protected void onEquip() {
        super.onEquip();
        SlimeWatcher slimeWatcher = (SlimeWatcher) disguise.getWatcher();
        slimeWatcher.setSize(3);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    protected void onClear() {

    }
}