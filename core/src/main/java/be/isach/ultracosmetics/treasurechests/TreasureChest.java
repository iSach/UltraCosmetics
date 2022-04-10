package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import be.isach.ultracosmetics.util.UtilParticles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;

public class TreasureChest implements Listener {

    private static final BlockFace[] SURROUNDING_FACES = new BlockFace[] {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.SOUTH_EAST, BlockFace.NORTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_WEST};
    private final Map<Block, BlockState> blocksToRestore = new HashMap<>();
    private final List<Block> chests = new ArrayList<>();
    private final List<Block> chestsToRemove = new ArrayList<>();
    private final UUID owner;
    private BukkitRunnable chestParticleRunnable = null;
    private BukkitRunnable placeChestRunnable = null;
    private TreasureRandomizer randomGenerator;
    private Location center;
    private final Particles particleEffect;
    private int chestsLeft = SettingsManager.getConfig().getInt("TreasureChests.Count", 4);
    private Player player;
    private List<Entity> items = new ArrayList<>();
    private List<Entity> holograms = new ArrayList<>();
    private boolean stopping;
    private boolean cooldown = false;
    private final TreasureChestDesign design;
    private final Location preLoc;
    private final TreasureLocation treasureLoc;

    public TreasureChest(UUID owner, final TreasureChestDesign design, Location preLoc, TreasureLocation destLoc) {
        this.design = design;
        this.particleEffect = design.getEffect();
        this.owner = owner;
        this.preLoc = preLoc;
        this.treasureLoc = destLoc;
        
        UltraCosmetics uc = UltraCosmeticsData.get().getPlugin();
        UltraPlayerManager pm = uc.getPlayerManager();

        Bukkit.getPluginManager().registerEvents(this, uc);

        this.player = getPlayer();

        Location loc = getPlayer().getLocation().getBlock().getLocation();
        Block centerPossibleBlock = loc.getBlock();
        if(!BlockUtils.isAir(centerPossibleBlock.getType())) {
            // Save the block
            blocksToRestore.put(centerPossibleBlock, centerPossibleBlock.getState());

            // Temporarily remove it
            centerPossibleBlock.setType(Material.AIR);
        }

        if (pm.getUltraPlayer(getPlayer()).getCurrentMorph() != null)
            pm.getUltraPlayer(getPlayer()).setSeeSelfMorph(false);

        this.randomGenerator = new TreasureRandomizer(getPlayer(), getPlayer().getLocation());

        BukkitRunnable runnable = new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                if ((getPlayer() == null) || (pm.getUltraPlayer(getPlayer()).getCurrentTreasureChest() != TreasureChest.this)) {
                    cancel();
                    return;
                }
                try {
                    if (i == 0) {
                        chestParticleRunnable = new BukkitRunnable() {
                            int i = chestsLeft;

                            @Override
                            public void run() {
                                if (i <= 0) {
                                    cancel();
                                    return;
                                }
                                if ((getPlayer() == null) || (pm.getUltraPlayer(getPlayer()).getCurrentTreasureChest() != TreasureChest.this)) {
                                    cancel();
                                    return;
                                }
                                int animationTime = 0;
                                if (particleEffect != null) {
                                    UtilParticles.playHelix(getChestLocation(i, center.clone()), 0.0F, particleEffect);
                                    UtilParticles.playHelix(getChestLocation(i, center.clone()), 3.5F, particleEffect);
                                    animationTime = 30;
                                }
                                placeChestRunnable = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Block b = getChestLocation(i, center.clone()).getBlock();
                                        b.setType(design.getChestType().getType());
                                        SoundUtil.playSound(getPlayer(), Sounds.ANVIL_LAND, 1.4f, 1.5f);
                                        UtilParticles.display(Particles.SMOKE_LARGE, b.getLocation(), 5);
                                        UtilParticles.display(Particles.LAVA, b.getLocation(), 5);
                                        BlockFace blockFace = BlockFace.SOUTH;
                                        switch (i) {
                                            case 4:
                                                blockFace = BlockFace.SOUTH;
                                                break;
                                            case 3:
                                                blockFace = BlockFace.NORTH;
                                                break;
                                            case 2:
                                                blockFace = BlockFace.EAST;
                                                break;
                                            case 1:
                                                blockFace = BlockFace.WEST;
                                                break;
                                        }

                                        XBlock.setDirection(b, blockFace);

                                        chests.add(b);
                                        i--;
                                    }
                                };
                                placeChestRunnable.runTaskLater(uc, animationTime);
                            }
                        };
                        chestParticleRunnable.runTaskTimer(uc, 0L, 50L);
                    }
                    Block lampBlock;
                    if (i == 5) {
                        lampBlock = getPlayer().getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
                        center = lampBlock.getLocation().add(0.5D, 1.0D, 0.5D);
                        doChestStage(Arrays.asList(lampBlock), design.getCenter());
                    } else if (i == 4) {
                        doChestStage(getSurroundingBlocks(center.clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBlocks2());
                    } else if (i == 3) {
                        doChestStage(getSurroundingSurrounding(center.clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBlocks3());
                    } else if (i == 2) {
                        doChestStage(getBlock3(center.clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBelowChests());
                    } else if (i == 1) {
                        doChestStage(getSurroundingSurrounding(center.getBlock()), design.getBarriers());
                    }
                    i--;
                } catch (Exception exc) {
                    cancel();
                    exc.printStackTrace();
                    forceOpen(0);
                }
            }
        };
        runnable.runTaskTimer(uc, 0L, 12L);

        Bukkit.getScheduler().runTaskLater(uc, () -> {
            if (pm.getUltraPlayer(player) != null && pm.getUltraPlayer(player).getCurrentTreasureChest() == TreasureChest.this)
                forceOpen(45);
        }, 1200L);

        pm.getUltraPlayer(getPlayer()).setCurrentTreasureChest(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                UltraPlayer ultraPlayer = pm.getUltraPlayer(getPlayer());
                if (ultraPlayer == null || ultraPlayer.getCurrentTreasureChest() != TreasureChest.this)  {
                    for (Entity entity : holograms) {
                        entity.remove();
                    }
                    cancel();
                    return;
                }
                if (!getPlayer().getWorld().getName().equals(center.getWorld().getName())) {
                    getPlayer().teleport(center);
                }
                //                            distance(center) > 1.5D
                if (getPlayer().getLocation().distanceSquared(center) > 2.25D) {
                    getPlayer().teleport(center);
                }
                for (Entity ent : player.getNearbyEntities(2.0D, 2.0D, 2.0D)) {
                    if (ent == TreasureChest.this.getPlayer()) continue;
                    if (items.contains(ent)) continue;
                    if (holograms.contains(ent)) continue;
                    UltraPlayer up = pm.getUltraPlayer(player);
                    // if player has a pet and the loop entity is either the pet or one of its items, skip it
                    if (up.getCurrentPet() != null) {
                        if (ent == up.getCurrentPet()) continue;
                        if (up.getCurrentPet().getItems().contains(ent)) continue;
                    }
                    // Passed all checks!
                    Vector v = ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).multiply(0.5D).add(new Vector(0.0D, 1.5D, 0.0D));
                    v.setY(0);
                    v.add(new Vector(0, 1, 0));
                    MathUtils.applyVelocity(ent, v.add(MathUtils.getRandomCircleVector().multiply(0.2D)));
                }
            }
        }.runTaskTimer(uc, 0L, 1L);
    }
    
    private void doChestStage(Iterable<Block> blocks, XMaterial newData) {
        if (newData == null) return;
        for (Block b : blocks) {
            blocksToRestore.put(b, b.getState());
            BlockUtils.treasureBlocks.add(b);
            XBlock.setType(b, newData);
        }
    }

    public Player getPlayer() {
        if (owner != null) {
            return Bukkit.getPlayer(owner);
        }
        return null;
    }

    public void clear() {
        for (Entry<Block,BlockState> entry : blocksToRestore.entrySet()) {
            entry.getValue().update(true);
            BlockUtils.treasureBlocks.remove(entry.getKey());
        }
        blocksToRestore.clear();
        if (stopping) {
            cleanup();
        } else {
            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> cleanup(), 30L);
        }
    }

    private void cleanup() {
        for (Entity hologram : holograms)
            hologram.remove();
        for (Block b : chestsToRemove) {
            b.setType(Material.AIR);
        }
        for (Block b : chests) {
            b.setType(Material.AIR);
        }
        for (Entity ent : items)
            ent.remove();
        cancelRunnables();
        items.clear();
        chests.clear();
        holograms.clear();
        chestsToRemove.clear();
        if (getPlayer() != null) {
            UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(getPlayer()).setCurrentTreasureChest(null);
            if (preLoc != null) {
                getPlayer().teleport(preLoc);
            }
        }
        HandlerList.unregisterAll(this);
    }

    private void cancelRunnables() {
        if (chestParticleRunnable != null) {
            chestParticleRunnable.cancel();
        }
        if (placeChestRunnable != null) {
            placeChestRunnable.cancel();
        }
    }

    public List<Block> getSurroundingBlocks(Block b) {
        List<Block> blocks = new ArrayList<>();
        for (BlockFace face : SURROUNDING_FACES) {
            blocks.add(b.getRelative(face));
        }
        return blocks;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer() == getPlayer() && !event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getFrom());
        }
    }

    public void forceOpen(int delay) {
        int i;
        if (delay == 0) {
            stopping = true;
            for (i = 0; i < chestsLeft; i++) {
                randomGenerator.giveRandomThing();
                getPlayer().sendMessage(MessageManager.getMessage("You-Won-Treasure-Chests").replace("%name%", randomGenerator.getName()));
            }
            return;
        }

        for (final Block b : chests) {
            UltraCosmeticsData.get().getVersionManager().getEntityUtil().playChestAnimation(b, true, design);
            randomGenerator.setLocation(b.getLocation().clone().add(0.0D, 1.0D, 0.0D));
            randomGenerator.giveRandomThing();
            ItemStack is = randomGenerator.getItemStack();
            ItemMeta itemMeta = is.getItemMeta();
            itemMeta.setDisplayName(UltraCosmeticsData.get().getItemNoPickupString());
            is.setItemMeta(itemMeta);

            Entity entity = UltraCosmeticsData.get().getVersionManager().getEntityUtil().spawnItem(is, b.getLocation());

            items.add(entity);
            final String nameas = randomGenerator.getName();
            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () ->
                    spawnHologram(b.getLocation().clone().add(0.5D, 0.3D, 0.5D), nameas), 15L);

            chestsLeft -= 1;
            chestsToRemove.add(b);
        }
        chests.clear();

        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, delay);
    }

    public List<Block> getSurroundingSurrounding(Block b) {
        List<Block> blocks = new ArrayList<>();
        // makes a pattern in the shape of:
        // XX XX
        // X   X
        //      
        // X   X
        // XX XX
        for (int x = -2; x <= 2; x++) {
            if (x == 0) continue;
            
            for (int z = -2; z <= 2; z++) {
                if (z == 0) continue;
                if (Math.abs(x) == 1 && Math.abs(z) == 1) continue;
                
                blocks.add(b.getRelative(x, 0, z));
            }
        }
        return blocks;
    }

    public List<Block> getBlock3(Block b) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(b.getRelative(-2, 0, 0));
        blocks.add(b.getRelative(2, 0, 0));
        blocks.add(b.getRelative(0, 0, 2));
        blocks.add(b.getRelative(0, 0, -2));
        return blocks;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (blocksToRestore.containsKey(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private void spawnHologram(Location location, String s) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.valueOf("ARMOR_STAND"));
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomName(s);
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "C_AD_ArmorStand"));
        holograms.add(armorStand);
    }

    @EventHandler
    public void onInter(final PlayerInteractEvent event) {
        if ((event.getClickedBlock() != null) &&
                (event.getClickedBlock().getType() == Material.CHEST
                        || event.getClickedBlock().getType() == Material.ENDER_CHEST
                        || event.getClickedBlock().getType() == Material.TRAPPED_CHEST) &&
                (chests.contains(event.getClickedBlock())) && (!cooldown)) {
            if (event.getPlayer() == getPlayer()) {
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().playChestAnimation(event.getClickedBlock(), true, design);
                randomGenerator.setLocation(event.getClickedBlock().getLocation().add(0.0D, 1.0D, 0.0D));
                randomGenerator.giveRandomThing();

                cooldown = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmeticsData.get().getPlugin(), () -> cooldown = false, 3L);

                ItemStack is = randomGenerator.getItemStack();
                ItemMeta itemMeta = is.getItemMeta();
                itemMeta.setDisplayName(UltraCosmeticsData.get().getItemNoPickupString());
                is.setItemMeta(itemMeta);

                Entity itemEntity = UltraCosmeticsData.get().getVersionManager().getEntityUtil().spawnItem(is, event.getClickedBlock().getLocation());

                items.add(itemEntity);
                final String nameas = randomGenerator.getName();
                Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () ->
                        spawnHologram(event.getClickedBlock().getLocation().add(0.5D, 0.3D, 0.5D), nameas), 15L);

                chestsLeft -= 1;
                chests.remove(event.getClickedBlock());
                chestsToRemove.add(event.getClickedBlock());
                if (chestsLeft == 0)
                    Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, 50L);
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if ((event.getPlayer() == getPlayer()) &&
                (event.getReason().equals("Flying is not enabled on this server"))) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.INFO, "Cancelled flight kick while opening treasure chest");
            event.setCancelled(true);
            event.getPlayer().teleport(center);
        }
    }

    public Location getChestLocation(int i, Location loc) {
        Location chestLocation = center.clone();
        chestLocation.setX(loc.getBlockX() + 0.5D);
        chestLocation.setY(loc.getBlockY());
        chestLocation.setZ(loc.getBlockZ() + 0.5D);
        switch (i) {
            case 1:
                chestLocation.add(2.0D, 0.0D, 0.0D);
                break;
            case 2:
                chestLocation.add(-2.0D, 0.0D, 0.0D);
                break;
            case 3:
                chestLocation.add(0.0D, 0.0D, 2.0D);
                break;
            case 4:
                chestLocation.add(0.0D, 0.0D, -2.0D);
        }

        return chestLocation;
    }

    public TreasureLocation getTreasureLocation() {
        return treasureLoc;
    }


    /**
     * Cancel eggs from merging
     *
     * @param event
     */
    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        if (items.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
