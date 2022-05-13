package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.version.VersionManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a smashdown gadget summoned by a player.
 *
 * @author iSach
 * @since 08-08-2015
 */
public class GadgetSmashDown extends Gadget implements PlayerAffectingCosmetic, Updatable {

    private boolean active = false;
    private List<FallingBlock> fallingBlocks = new ArrayList<>();
    private int i = 1;
    private boolean playEffect;

    public GadgetSmashDown(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("smashdown"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.play(getPlayer().getLocation(), 2.0f, 1.0f);
        getPlayer().setVelocity(new Vector(0, 3, 0));
        final BukkitTask task = Bukkit.getScheduler().runTaskTimer(getUltraCosmetics(), () -> {
            if (getOwner() != null && getPlayer() != null && isEquipped()) {
                Particles.CLOUD.display(getPlayer().getLocation());
            } else {
                cancel();
            }
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            if (getOwner() != null && getPlayer() != null && isEquipped()) {
                task.cancel();
                getOwner().applyVelocity(new Vector(0, -3, 0));
                active = true;
            }
        }, 25);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (active && event.getEntity() == getPlayer()) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onUpdate() {
        if (active && getPlayer().isOnGround()) {
            this.playEffect = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> active = false, 5);
            return;
        }

        if (!playEffect) {
            return;
        }

        Location loc = getPlayer().getLocation();
        XSound.ENTITY_GENERIC_EXPLODE.play(getPlayer().getLocation(), 2.0f, 1.0f);

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
                if (!BlockUtils.isBadMaterial(b.getType())
                        && !BlockUtils.isRocketBlock(b)
                        && !BlockUtils.isTreasureChestBlock(b)
                        && b.getType().isSolid()
                        && BlockUtils.isAir(b.getRelative(BlockFace.UP).getType())) {
                    Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                        FallingBlock fb = BlockUtils.spawnFallingBlock(b.getLocation().clone().add(0, 1.1f, 0), b);

                        fb.setVelocity(new Vector(0, 0.3f, 0));
                        fb.setDropItem(false);
                        fallingBlocks.add(fb);
                        fb.getNearbyEntities(1, 1, 1).stream().filter(ent -> ent != getPlayer()
                                && ent.getType() != EntityType.FALLING_BLOCK && canAffect(ent))
                                .forEach(ent -> MathUtils.applyVelocity(ent, new Vector(0, 0.5, 0)));
                    });
                }
            }
        }
        i++;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockChangeState(EntityChangeBlockEvent event) {
        if (fallingBlocks.contains(event.getEntity())) {
            event.setCancelled(true);
            fallingBlocks.remove(event.getEntity());
            FallingBlock fb = (FallingBlock) event.getEntity();
            if (VersionManager.IS_VERSION_1_13) {
                BlockData data = fb.getBlockData();
                fb.getWorld().spawnParticle(Particle.BLOCK_CRACK, fb.getLocation(), 50, 0, 0, 0, 0.4d, data);
            } else {
                Particles.BLOCK_CRACK.display(new Particles.BlockData(fb.getMaterial(), event.getBlock().getData()), 0f, 0f, 0f, 0.4f, 50, fb.getLocation(), 128);
            }
            XSound.BLOCK_ANVIL_BREAK.play(getPlayer().getLocation(), 0.05f, 1.0f);
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
