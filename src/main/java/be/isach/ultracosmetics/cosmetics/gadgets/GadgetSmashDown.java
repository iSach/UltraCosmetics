package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetSmashDown extends Gadget {

    List<Player> activePlayers = new ArrayList<>();
    List<FallingBlock> fallingBlocks = new ArrayList<>();
    GadgetSmashDown instance;

    public GadgetSmashDown(UUID owner) {
        super(Material.FIREWORK_CHARGE, (byte) 0x0, "SmashDown", "ultracosmetics.gadgets.smashdown", 15, owner, GadgetType.SMASHDOWN, "&7&oSmash the ground like Hulk!");
        Core.registerListener(this);
        instance = this;
    }

    @Override
    void onInteractRightClick() {
        getPlayer().playSound(getPlayer().getLocation(), Sound.FIREWORK_LAUNCH, 2, 1);
        getPlayer().setVelocity(new Vector(0, 3, 0));
        final int taskId = Bukkit.getScheduler().runTaskTimer(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                UtilParticles.play(getPlayer().getLocation(), Effect.CLOUD);
            }
        }, 0, 1).getTaskId();
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(taskId);
                getPlayer().setVelocity(new Vector(0, -3, 0));
                activePlayers.add(getPlayer());
            }
        }, 25);
    }

    @Override
    void onInteractLeftClick() {
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (activePlayers.contains(event.getEntity()))
            event.setCancelled(true);
    }

    @Override
    void onUpdate() {
        if (activePlayers.contains(getPlayer()) && getPlayer().isOnGround()) {
            activePlayers.remove(getPlayer());
            playBoomEffect();
        }
    }

    private void playBoomEffect() {
        final Location loc = getPlayer().getLocation();
        loc.getWorld().playSound(loc, Sound.EXPLODE, 2, 1);
        new BukkitRunnable() {
            int i = 1;

            @Override
            public void run() {
                if (i == 5) {
                    cancel();
                }
                if (Core.getCustomPlayer(getPlayer()).currentGadget != instance) {
                    cancel();
                    return;
                }
                for (Block b : BlockUtils.getBlocksInRadius(loc.clone().add(0, -1, 0), i, true)) {
                    if (b.getLocation().getBlockY() == loc.getBlockY() - 1) {
                        if (b.getType() != Material.AIR
                                && b.getType() != Material.SIGN_POST
                                && b.getType() != Material.CHEST
                                && b.getType() != Material.STONE_PLATE
                                && b.getType() != Material.WOOD_PLATE
                                && b.getType() != Material.WALL_SIGN
                                && b.getType() != Material.WALL_BANNER
                                && b.getType() != Material.STANDING_BANNER
                                && b.getType() != Material.CROPS
                                && b.getType() != Material.LONG_GRASS
                                && b.getType() != Material.SAPLING
                                && b.getType() != Material.DEAD_BUSH
                                && b.getType() != Material.RED_ROSE
                                && b.getType() != Material.RED_MUSHROOM
                                && b.getType() != Material.BROWN_MUSHROOM
                                && b.getType() != Material.TORCH
                                && b.getType() != Material.LADDER
                                && b.getType() != Material.VINE
                                && b.getType() != Material.DOUBLE_PLANT
                                && b.getType() != Material.PORTAL
                                && b.getType() != Material.CACTUS
                                && b.getType() != Material.WATER
                                && b.getType() != Material.STATIONARY_WATER
                                && b.getType() != Material.LAVA
                                && b.getType() != Material.STATIONARY_LAVA
                                && !BlockUtils.isRocketBlock(b)
                                && net.minecraft.server.v1_8_R3.Block.getById(b.getTypeId()).getMaterial().isSolid()
                                && b.getType().getId() != 43
                                && b.getType().getId() != 44
                                && b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                            FallingBlock fb = loc.getWorld().spawnFallingBlock(b.getLocation().clone().add(0, 1.1f, 0), b.getType(), b.getData());
                            fb.setVelocity(new Vector(0, 0.3f, 0));
                            fb.setDropItem(false);
                            fallingBlocks.add(fb);
                            for (Entity ent : fb.getNearbyEntities(1, 1, 1)) {
                                if (ent != getPlayer() && ent.getType() != EntityType.FALLING_BLOCK)
                                    if (affectPlayers)
                                        MathUtils.applyVelocity(ent, new Vector(0, 0.5, 0));
                            }
                        }
                    }
                }
                i++;
            }
        }.runTaskTimer(Core.getPlugin(), 0, 1);
    }

    @EventHandler
    public void onBlockChangeState(EntityChangeBlockEvent event) {
        if (fallingBlocks.contains(event.getEntity())) {
            event.setCancelled(true);
            fallingBlocks.remove(event.getEntity());
            FallingBlock fb = (FallingBlock) event.getEntity();
            fb.getWorld().spigot().playEffect(fb.getLocation(), Effect.STEP_SOUND, fb.getBlockId(), (int) fb.getBlockData(), 0, 0, 0, 0, 1, 32);
            event.getEntity().remove();
        }
    }

    @Override
    public void onClear() {
        for (FallingBlock block : fallingBlocks) {
            block.remove();
        }
        HandlerList.unregisterAll(this);
    }
}
