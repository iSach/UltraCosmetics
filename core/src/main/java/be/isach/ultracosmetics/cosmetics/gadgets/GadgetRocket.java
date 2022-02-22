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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an instance of a rocket gadget summoned by a player.
 *
 * @author iSach
 * @since 08-17-2015
 */
public class GadgetRocket extends Gadget {

    // EntityDismountEvent has existed at least since 1.8, but wasn't cancellable until 1.13
    private static final boolean DISMOUNT_CANCELLABLE = UltraCosmeticsData.get().getServerVersion().is113();
    private static final BlockFace[] CARDINAL = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
    private static final Material FENCE = XMaterial.OAK_FENCE.parseMaterial();
    public static final Set<GadgetRocket> ROCKETS_WITH_BLOCKS = new HashSet<>();

    private boolean launching;
    private ArmorStand armorStand;
    private List<Block> blocks = new ArrayList<>();
    private List<FallingBlock> fallingBlocks = new ArrayList<>();
    private Entity playerVehicle = null;
    private BukkitTask currentTask = null;

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
            ROCKETS_WITH_BLOCKS.add(this);
            for (int i = 0; i < 2; i++) {
                Block center = loc.clone().add(0, i, 0).getBlock();
                for (BlockFace face : CARDINAL) {
                    Block side = center.getRelative(face);
                    side.setType(FENCE);
                    blocks.add(side);
                }
                Block quartz = center.getRelative(BlockFace.UP);
                quartz.setType(Material.QUARTZ_BLOCK);
                blocks.add(quartz);
            }
            armorStand = loc.getWorld().spawn(loc.add(0, 1.5, 0), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
        }, 10);
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
            // prevent kicking
            enableFlight();
            playerVehicle = null;
            armorStand.setPassenger(getPlayer());
            playerVehicle = armorStand;
            new BukkitRunnable() {
                int i = 5;

                @Override
                public void run() {
                    if (getOwner() == null || getPlayer() == null || !getPlayer().isOnline()) {
                        cancel();
                        return;
                    }

                    if (!isStillCurrentGadget()) {
                        cancel();
                        return;
                    }
                    if (i > 0) {
                        getPlayer().sendTitle(ChatColor.RED + "" + ChatColor.BOLD + i, "");
                        SoundUtil.playSound(getPlayer(), Sounds.NOTE_BASS_DRUM, 1.0f, 1.0f);
                        i--;
                        return;
                    }

                    if (isTaskRunning()) {
                        // if the player is refusing to be on the rocket (by holding sneak), abort the launch
                        onClear();
                        getPlayer().sendTitle(MessageManager.getMessage("Gadgets.Rocket.LaunchAborted"), "");
                        cancel();
                        return;
                        
                    }
                    getPlayer().sendTitle(MessageManager.getMessage("Gadgets.Rocket.Takeoff"), "");
                    SoundUtil.playSound(getPlayer().getLocation(), Sounds.EXPLODE, 1.0f, 1.0f);
                    playerVehicle = null;
                    armorStand.remove();
                    armorStand = null;

                    for (Block block : blocks) {
                        block.setType(Material.AIR);
                    }

                    blocks.clear();
                    ROCKETS_WITH_BLOCKS.remove(GadgetRocket.this);

                    final FallingBlock top = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 3, 0), Material.QUARTZ_BLOCK, (byte) 0);
                    FallingBlock base = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 2, 0), Material.QUARTZ_BLOCK, (byte) 0);
                    for (int i = 0; i < 2; i++) {
                        fallingBlocks.add(getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, 1), FENCE, (byte) 0));
                        fallingBlocks.add(getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, -1), FENCE, (byte) 0));
                        fallingBlocks.add(getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(1, 1 + i, 0), FENCE, (byte) 0));
                        fallingBlocks.add(getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(-1, 1 + i, 0), FENCE, (byte) 0));
                    }

                    fallingBlocks.add(top);
                    fallingBlocks.add(base);
                    if (fallingBlocks.get(8).getPassenger() == null) {
                        fallingBlocks.get(8).setPassenger(getPlayer());
                    }
                    top.setPassenger(getPlayer());
                    playerVehicle = top;
                    launching = true;
                    Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                        playerVehicle = null;
                        if (!isStillCurrentGadget()) {
                            cancel();
                            return;
                        }
                        fallingBlocks.forEach(Entity::remove);
                        fallingBlocks.clear();
                        FallDamageManager.addNoFall(getPlayer());
                        SoundUtil.playSound(getPlayer().getLocation(), Sounds.EXPLODE, 1.0f, 1.0f);
                        UtilParticles.display(Particles.EXPLOSION_HUGE, getPlayer().getLocation());
                        disableFlight();
                        launching = false;
                    }, 80);
                    cancel();
                }
            }.runTaskTimer(getUltraCosmetics(), 0, 20);
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

    public void onUpdate() {
        for (FallingBlock fallingBlock : fallingBlocks) {
            fallingBlock.setVelocity(new Vector(0, 0.8, 0));
        }

        if (launching && !fallingBlocks.isEmpty()) {
            UtilParticles.display(Particles.FLAME, 0.3f, 0.2f, 0.3f, getPlayer().getLocation().add(0, -3, 0), 10);
            UtilParticles.display(Particles.LAVA, 0.3f, 0.2f, 0.3f, getPlayer().getLocation().add(0, -3, 0), 10);
            SoundUtil.playSound(fallingBlocks.get(9).getLocation().clone().add(0, -1, 0), Sounds.BAT_LOOP, 1.5f, 1.0f);
            SoundUtil.playSound(fallingBlocks.get(9).getLocation().clone().add(0, -1, 0), Sounds.FIZZ, 0.025f, 1.0f);
        }
    }

    @Override
    public void onClear() {
        for (Block block : blocks) {
            block.setType(Material.AIR);
        }
        for (FallingBlock fallingBlock : fallingBlocks) {
            fallingBlock.remove();
        }
        blocks.clear();
        playerVehicle = null;
        fallingBlocks.clear();
        if (armorStand != null) {
            armorStand.remove();
        }
        disableFlight();
        launching = false;

        if (getPlayer() != null) {
            getPlayer().sendTitle(" ", "");
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() != getPlayer()) return;
        if (event.getDismounted() != playerVehicle) return;
        if (isTaskRunning()) return;
        if (DISMOUNT_CANCELLABLE) {
            event.setCancelled(true);
            return;
        }
        disableFlight();
        currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (getPlayer() == null) return;
                // happens if player sneaks 1 tick rocket end?
                if (playerVehicle == null) return;

                Entity vehicle = playerVehicle;
                playerVehicle = null;
                // can fail if player is holding sneak
                boolean success = vehicle.setPassenger(getPlayer());
                playerVehicle = vehicle;
                if (!success) return;

                cancel();
                enableFlight();
                if (vehicle instanceof ArmorStand) {
                    UtilParticles.display(Particles.SMOKE_LARGE, 0.3f, 0.2f, 0.3f, armorStand.getLocation().add(0, -3, 0), 10);
                    SoundUtil.playSound(armorStand.getLocation().clone().add(0, -3, 0), Sounds.FIZZ, 0.025f, 1.0f);
                }
            }
            // doesn't seem to work as well if you only wait one tick before trying to remount the player
        }.runTaskTimer(getUltraCosmetics(), 2, 1);
    }
    
    private void enableFlight() {
        getPlayer().setAllowFlight(true);
    }
    
    private void disableFlight() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE) {
            getPlayer().setAllowFlight(false);
        }
    }

    private boolean isTaskRunning() {
        return currentTask != null && Bukkit.getScheduler().isQueued(currentTask.getTaskId());
    }
}
