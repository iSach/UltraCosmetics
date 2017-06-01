package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Set;

/**
* Represents an instance of an enderman morph summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-26-2015
 */
public class MorphEnderman extends Morph {

    private boolean cooldown;

    public MorphEnderman(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.ENDERMAN, ultraCosmetics);
    }

    @Override
    protected void onEquip() {
        super.onEquip();
        getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerToggleFligh(PlayerToggleFlightEvent event) {
        if (event.getPlayer() == getPlayer()
                && event.getPlayer().getGameMode() != GameMode.CREATIVE
                && !event.getPlayer().isFlying()) {
            if(cooldown) {
                event.getPlayer().setFlying(false);
                event.setCancelled(true);
                return;
            }
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> cooldown = false, 70);
            Block b = event.getPlayer().getTargetBlock((Set<Material>) null, 17);
            Location loc = b.getLocation();
            loc.setPitch(event.getPlayer().getLocation().getPitch());
            loc.setYaw(event.getPlayer().getLocation().getYaw());
            event.getPlayer().teleport(loc);
            spawnRandomFirework(b.getLocation().add(0.5, 0, 0.5));
            event.getPlayer().setFlying(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() == getPlayer()
                && getOwner().getCurrentMorph() == this) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUpdate() {

    }

    public static FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
	    return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.fromRGB(0, 0, 0)).withFade(Color.fromRGB(0, 0, 0)).build();
    }

    public void spawnRandomFirework(Location location) {
        final ArrayList<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Firework f = getPlayer().getWorld().spawn(location, Firework.class);

            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            f.setFireworkMeta(fm);
            fireworks.add(f);
        }
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            for (Firework f : fireworks)
                f.detonate();
        }, 2);
    }

    @Override
    public void onClear() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE) {
            getPlayer().setAllowFlight(false);
        }
    }
}
