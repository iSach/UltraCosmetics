package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a rocket gadget summoned by a player.
 *
 * @author iSach
 * @since 08-17-2015
 */
public class GadgetRocket extends Gadget {

    public static final List<Block> BLOCKS = new ArrayList<>();

    boolean launching;
    ArmorStand armorStand;
    List<FallingBlock> fallingBlocks = new ArrayList<>();

    public GadgetRocket(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("rocket"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        getPlayer().setVelocity(new Vector(0, 1, 0));
        final Location loc = getPlayer().getLocation();
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ() + 0.5);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            for (int i = 0; i < 2; i++) {
                Block b1 = loc.clone().add(1, i, 0).getBlock();
                Block b2 = loc.clone().add(-1, i, 0).getBlock();
                Block b3 = loc.clone().add(0, i, 1).getBlock();
                Block b4 = loc.clone().add(0, i, -1).getBlock();
                Block b5 = loc.clone().add(0, i + 1, 0).getBlock();
                b1.setType(BlockUtils.getOldMaterial("FENCE"));
                b2.setType(BlockUtils.getOldMaterial("FENCE"));
                b3.setType(BlockUtils.getOldMaterial("FENCE"));
                b4.setType(BlockUtils.getOldMaterial("FENCE"));
                b5.setType(Material.QUARTZ_BLOCK);
                BLOCKS.add(b1);
                BLOCKS.add(b2);
                BLOCKS.add(b3);
                BLOCKS.add(b4);
                BLOCKS.add(b5);
            }
            armorStand = loc.getWorld().spawn(loc.add(0, 1.5, 0), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
        }, 10);
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
            armorStand.setPassenger(getPlayer());
            BukkitRunnable runnable = new BukkitRunnable() {
                int i = 5;

                @Override
                public void run() {
                    if (getOwner() == null) {
                        cancel();
                        return;
                    }

                    if (getPlayer() == null) {
                        cancel();
                        return;
                    }

                    if (!getPlayer().isOnline()) {
                        cancel();
                        return;
                    }

                    if (i > 0) {
                        if (!isStillCurrentGadget()) {
                            cancel();
                            return;
                        }
                        getPlayer().sendTitle(ChatColor.RED + "" + ChatColor.BOLD + i, "");
                        SoundUtil.playSound(getPlayer(), Sounds.NOTE_BASS_DRUM, 1.0f, 1.0f);
                        i--;
                    } else {
                        if (!isStillCurrentGadget()) {
                            cancel();
                            return;
                        }

                        getPlayer().sendTitle(MessageManager.getMessage("Gadgets.Rocket.Takeoff"), "");
                        SoundUtil.playSound(getPlayer().getLocation(), Sounds.EXPLODE, 1.0f, 1.0f);
                        armorStand.remove();
                        armorStand = null;


                        for (Block block : BLOCKS) {
                            block.setType(Material.AIR);
                        }

                        BLOCKS.clear();

                        final FallingBlock top = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 3, 0), Material.QUARTZ_BLOCK, (byte) 0);
                        FallingBlock base = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 2, 0), Material.QUARTZ_BLOCK, (byte) 0);
                        for (int i = 0; i < 2; i++) {
                            FallingBlock fence1 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, 1), BlockUtils.getOldMaterial("FENCE"), (byte) 0);
                            FallingBlock fence2 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, -1), BlockUtils.getOldMaterial("FENCE"), (byte) 0);
                            FallingBlock fence3 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(1, 1 + i, 0), BlockUtils.getOldMaterial("FENCE"), (byte) 0);
                            FallingBlock fence4 = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(-1, 1 + i, 0), BlockUtils.getOldMaterial("FENCE"), (byte) 0);
                            fallingBlocks.add(fence1);
                            fallingBlocks.add(fence2);
                            fallingBlocks.add(fence3);
                            fallingBlocks.add(fence4);
                        }

                        fallingBlocks.add(top);
                        fallingBlocks.add(base);
                        if (fallingBlocks.get(8).getPassenger() == null) {
                            fallingBlocks.get(8).setPassenger(getPlayer());
                        }
                        top.setPassenger(getPlayer());
                        launching = true;
                        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                            if (!isStillCurrentGadget()) {
                                cancel();
                                return;
                            }
                            fallingBlocks.forEach(Entity::remove);
                            fallingBlocks.clear();
                            FallDamageManager.addNoFall(getPlayer());
                            SoundUtil.playSound(getPlayer().getLocation(), Sounds.EXPLODE, 1.0f, 1.0f);
                            UtilParticles.display(Particles.EXPLOSION_HUGE, getPlayer().getLocation());
                            launching = false;
                        }, 80);
                        cancel();
                    }
                }
            };
            runnable.runTaskTimer(getUltraCosmetics(), 0, 20);
        }, 12);
    }

    @Override
    protected boolean checkRequirements(PlayerInteractEvent event) {
        Cuboid c = new Cuboid(getPlayer().getLocation().add(-1, 0, -1), getPlayer().getLocation().add(1, 75, 1));
        if (!c.isEmpty()) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-Enough-Space"));
            return false;
        }
        if (!getPlayer().isOnGround()) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
            return false;
        }
        return true;
    }

    private boolean isStillCurrentGadget() {
        return getOwner() != null;
    }

    @Override
    public void onUpdate() {
        if (armorStand != null) {
            if (armorStand.getPassenger() == null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        armorStand.setPassenger(getPlayer());
                    }
                }.runTask(getUltraCosmetics());
            }

            UtilParticles.display(Particles.SMOKE_LARGE, 0.3f, 0.2f, 0.3f, armorStand.getLocation().add(0, -3, 0), 10);
            SoundUtil.playSound(armorStand.getLocation().clone().add(0, -3, 0), Sounds.FIZZ, 0.025f, 1.0f);
        }
        for (FallingBlock fallingBlock : fallingBlocks) {
            fallingBlock.setVelocity(new Vector(0, 0.8, 0));
        }
        if (launching) {
            if (fallingBlocks.get(8).getPassenger() == null) {
                fallingBlocks.get(8).setPassenger(getPlayer());
            }
            UtilParticles.display(Particles.FLAME, 0.3f, 0.2f, 0.3f, getPlayer().getLocation().add(0, -3, 0), 10);
            UtilParticles.display(Particles.LAVA, 0.3f, 0.2f, 0.3f, getPlayer().getLocation().add(0, -3, 0), 10);
            SoundUtil.playSound(fallingBlocks.get(9).getLocation().clone().add(0, -1, 0), Sounds.BAT_LOOP, 1.5f, 1.0f);
            SoundUtil.playSound(fallingBlocks.get(9).getLocation().clone().add(0, -1, 0), Sounds.FIZZ, 0.025f, 1.0f);
        }
    }

    @Override
    public void onClear() {
        for (Block block : BLOCKS) {
            block.setType(Material.AIR);
        }
        for (FallingBlock fallingBlock : fallingBlocks) {
            fallingBlock.remove();
        }
        BLOCKS.clear();
        fallingBlocks.clear();
        if (armorStand != null) {
            armorStand.remove();
        }
        launching = false;

        if (getPlayer() != null) {
            getPlayer().sendTitle(" ", "");
        }
    }

    @Override
    void onLeftClick() {
    }
}
