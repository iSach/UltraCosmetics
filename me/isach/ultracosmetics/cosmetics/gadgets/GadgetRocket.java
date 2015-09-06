package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.util.Title;
import me.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 17/08/15.
 */
public class GadgetRocket extends Gadget {

    boolean launching;
    Arrow arrow;
    List<FallingBlock> fallingBlocks = new ArrayList<>();
    public List<Block> blocks = new ArrayList<>();

    public GadgetRocket(UUID owner) {
        super(Material.getMaterial(401), (byte) 0x0, "Rocket", "ultracosmetics.gadgets.rocket", 60, owner, GadgetType.ROCKET);
    }

    @Override
    void onInteractRightClick() {
        getPlayer().setVelocity(new Vector(0, 1, 0));
        final Location loc = getPlayer().getLocation();
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ() + 0.5);
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    Block b1 = loc.clone().add(1, i, 0).getBlock();
                    Block b2 = loc.clone().add(-1, i, 0).getBlock();
                    Block b3 = loc.clone().add(0, i, 1).getBlock();
                    Block b4 = loc.clone().add(0, i, -1).getBlock();
                    Block b5 = loc.clone().add(0, i + 1, 0).getBlock();
                    b1.setType(Material.FENCE);
                    b2.setType(Material.FENCE);
                    b3.setType(Material.FENCE);
                    b4.setType(Material.FENCE);
                    b5.setType(Material.QUARTZ_BLOCK);
                    blocks.add(b1);
                    blocks.add(b2);
                    blocks.add(b3);
                    blocks.add(b4);
                    blocks.add(b5);
                }
                arrow = loc.getWorld().spawn(loc.add(0, 4, 0), Arrow.class);
                arrow.setKnockbackStrength(0);

            }
        }, 10);
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {

            @Override
            public void run() {
                arrow.setPassenger(getPlayer());
                BukkitRunnable runnable = new BukkitRunnable() {
                    int i = 5;

                    @Override
                    public void run() {
                        if (i > 0) {
                            if (!isStillCurrentGadget()) {
                                cancel();
                                return;
                            }
                            new Title("§c§l" + i).send(getPlayer());
                            getPlayer().playSound(getPlayer().getLocation(), Sound.NOTE_STICKS, 1, 1);
                            i--;
                        } else {
                            if (!isStillCurrentGadget()) {
                                cancel();
                                return;
                            }
                            new Title(MessageManager.getMessage("Gadgets.Rocket.Takeoff")).send(getPlayer());

                            getPlayer().playSound(getPlayer().getLocation(), Sound.EXPLODE, 1, 1);
                            arrow.remove();
                            arrow = null;

                            for (Block block : blocks) {
                                block.setType(Material.AIR);
                            }
                            blocks.clear();

                            FallingBlock top = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 3, 0), Material.QUARTZ_BLOCK, (byte) 0);
                            FallingBlock base = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 2, 0), Material.QUARTZ_BLOCK, (byte) 0);
                            for (int i = 0; i < 2; i++) {
                                FallingBlock fence1 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, 1), Material.FENCE, (byte) 0);
                                FallingBlock fence2 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, -1), Material.FENCE, (byte) 0);
                                FallingBlock fence3 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(1, 1 + i, 0), Material.FENCE, (byte) 0);
                                FallingBlock fence4 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(-1, 1 + i, 0), Material.FENCE, (byte) 0);
                                fallingBlocks.add(fence1);
                                fallingBlocks.add(fence2);
                                fallingBlocks.add(fence3);
                                fallingBlocks.add(fence4);
                            }
                            fallingBlocks.add(top);
                            fallingBlocks.add(base);
                            top.setPassenger(getPlayer());
                            launching = true;
                            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    if (!isStillCurrentGadget()) {
                                        cancel();
                                        return;
                                    }
                                    for (FallingBlock fb : fallingBlocks)
                                        fb.remove();
                                    fallingBlocks.clear();
                                    Core.noFallDamageEntities.add(getPlayer());
                                    getPlayer().playSound(getPlayer().getLocation(), Sound.EXPLODE, 3, 1);
                                    UtilParticles.play(getPlayer().getLocation(), Effect.EXPLOSION_HUGE);
                                    launching = false;
                                }
                            }, 80);
                            cancel();
                        }
                    }
                };
                runnable.runTaskTimer(Core.getPlugin(), 0, 20);
            }
        }, 12);
    }

    boolean isStillCurrentGadget() {
        return Core.getCustomPlayer(getPlayer()).currentGadget == this;
    }

    @Override
    void onUpdate() {
        if (arrow != null) {
            if (arrow.getPassenger() == null)
                arrow.setPassenger(getPlayer());
            UtilParticles.play(arrow.getLocation().clone().add(0, -3, 0), Effect.LARGE_SMOKE, 0, 0, 0.3f, 0.2f, 0.3f, 0, 10);
            arrow.getWorld().playSound(arrow.getLocation().clone().add(0, -3, 0), Sound.FIZZ, 0.025f, 1);
        }
        for (FallingBlock fallingBlock : fallingBlocks) {
            fallingBlock.setVelocity(new Vector(0, 0.8, 0));
        }
        if (launching) {
            if (fallingBlocks.get(8).getPassenger() == null)
                fallingBlocks.get(8).setPassenger(getPlayer());
            UtilParticles.play(getPlayer().getLocation().clone().add(0, -3, 0), Effect.LAVA_POP, 0, 0, 0.3f, 0.2f, 0.3f, 0, 10);
            UtilParticles.play(getPlayer().getLocation().clone().add(0, -3, 0), Effect.FLAME, 0, 0, 0.3f, 0.2f, 0.3f, 0.1f, 10);
            getPlayer().getWorld().playSound(getPlayer().getLocation().clone().add(0, -3, 0), Sound.BAT_LOOP, 1.5f, 1);
            getPlayer().getWorld().playSound(getPlayer().getLocation().clone().add(0, -3, 0), Sound.FIZZ, 0.025f, 1);
        }
    }

    @Override
    public void clear() {
        for (Block block : blocks)
            block.setType(Material.AIR);
        for (FallingBlock fallingBlock : fallingBlocks)
            fallingBlock.remove();
        blocks.clear();
        fallingBlocks.clear();
        if (arrow != null)
            arrow.remove();

        launching = false;
        new Title(" ").send(getPlayer());
        HandlerList.unregisterAll(this);
    }

    @Override
    void onInteractLeftClick() {
    }
}
