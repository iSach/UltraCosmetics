package me.isach.ultracosmetics.cosmetics.morphs;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.util.MathUtils;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sacha on 26/08/15.
 */
public class MorphEnderman extends Morph {

    private boolean cooldown;

    public MorphEnderman(UUID owner) {
        super(DisguiseType.ENDERMAN, Material.ENDER_PEARL, (byte) 0, "Enderman", "ultracosmetics.morphs.enderman", owner, MorphType.ENDERMAN);
        if (owner != null) {
            Core.registerListener(this);
            getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerToggleFligh(PlayerToggleFlightEvent event) {
        if (event.getPlayer() == getPlayer()
                && event.getPlayer().getGameMode() != GameMode.CREATIVE
                && !event.getPlayer().isFlying()
                && !cooldown) {
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 70);
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

    public static FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        Random r = new Random();
        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.fromRGB(0, 0, 0)).withFade(Color.fromRGB(0, 0, 0)).build();
        return effect;
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
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Firework f : fireworks)
                    f.detonate();
            }
        }, 2);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION
                && event.getEntity() == getPlayer())
            event.setCancelled(true);
    }

    @Override
    public void clear() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE)
            getPlayer().setAllowFlight(false);
        DisguiseAPI.undisguiseToAll(getPlayer());
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Morphs.Unmorph").replace("%morphname%", getName()));
        Core.getCustomPlayer(getPlayer()).currentMorph = null;
        owner = null;
        HandlerList.unregisterAll(this);
    }

}
