package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a smashdown gadget summoned by a player.
 *
 * @author iSach
 * @since 08-08-2015
 */
public class GadgetSmashDown extends Gadget {

    private boolean active = false;
    private List<FallingBlock> fallingBlocks = new ArrayList<>();
    private int i = 1;
    private boolean playEffect;

    public GadgetSmashDown(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.SMASHDOWN, ultraCosmetics);
    }

    @Override
    void onRightClick() {
        SoundUtil.playSound(getPlayer().getLocation(), Sounds.FIREWORK_LAUNCH, 2.0f, 1.0f);
        getPlayer().setVelocity(new Vector(0, 3, 0));
        final int taskId = Bukkit.getScheduler().runTaskTimer(getUltraCosmetics(), () ->
                UtilParticles.display(Particles.CLOUD, getPlayer().getLocation()), 0, 1).getTaskId();
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            Bukkit.getScheduler().cancelTask(taskId);
            if (getOwner() != null) {
		        getOwner().applyVelocity(new Vector(0, -3, 0));
		        active = true;
            }
        }, 25);
    }

    @Override
    void onLeftClick() {
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (active && event.getEntity() == getPlayer()) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUpdate() {
        if (active && getPlayer().isOnGround()) {
            this.playEffect = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> {
                active = false;
            }, 5);
            return;
        }

        if (!playEffect) {
            return;
        }

        Location loc = getPlayer().getLocation();
        SoundUtil.playSound(getPlayer().getLocation(), Sounds.EXPLODE, 2.0f, 1.0f);

        if (i == 5) {
            playEffect = false;
            active = false;
            i = 1;
            return;
        }
        if (getOwner().getCurrentGadget() != this) {
            playEffect = false;
            active = false;
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
                        && !BlockUtils.isTreasureChestBlock(b)
                        && b.getType().isSolid()
                        && b.getType().getId() != 43
                        && b.getType().getId() != 44
                        && b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                        FallingBlock fb = loc.getWorld().spawnFallingBlock(b.getLocation().clone().add(0, 1.1f, 0),
                                b.getType(), b.getData());

                        fb.setVelocity(new Vector(0, 0.3f, 0));
                        fb.setDropItem(false);
                        fallingBlocks.add(fb);
                        fb.getNearbyEntities(1, 1, 1).stream().filter(ent -> ent != getPlayer()
                                && ent.getType() != EntityType.FALLING_BLOCK).filter(ent -> affectPlayers).
                                forEach(ent -> MathUtils.applyVelocity(ent, new Vector(0, 0.5, 0)));
                    });
                }
            }
        }
        i++;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockChangeState(EntityChangeBlockEvent event) {
        if (fallingBlocks.contains(event.getEntity())) {
            event.setCancelled(true);
            fallingBlocks.remove(event.getEntity());
            FallingBlock fb = (FallingBlock) event.getEntity();
            Particles.BLOCK_CRACK.display(new Particles.BlockData(Material.getMaterial(fb.getBlockId()), fb.getBlockData()), 0f, 0f, 0f, 0.4f, 50, fb.getLocation(), 128);
            SoundUtil.playSound(getPlayer().getLocation(), Sounds.ANVIL_BREAK, 0.05f, 1.0f);
            event.getEntity().remove();
        }
    }

    @Override
    public void onClear() {
        for (FallingBlock block : fallingBlocks) {
            block.remove();
        }
    }
}
