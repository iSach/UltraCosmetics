package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.util.*;
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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class TreasureChest implements Listener {

    private Map<Block, BlockState> blocksToRestore = new HashMap<>();
    private ArrayList<Block> chests = new ArrayList<>();
    private ArrayList<Block> chestsToRemove = new ArrayList<>();
    public UUID owner;
    private final BukkitRunnable[] RUNNABLES = new BukkitRunnable[2];
    private TreasureChest instance;
    private TreasureRandomizer randomGenerator;
    private Location center;
    private Particles particleEffect;
    private int chestsLeft = 4;
    private Player player;
    private List<Entity> items = new ArrayList<>();
    private List<Entity> holograms = new ArrayList<>();
    private boolean stopping;
    private boolean cooldown = false;
    private TreasureChestDesign design;
    private Location preLoc = null;

    public TreasureChest(UUID owner, final TreasureChestDesign design) {
        this(owner, design, null);
    }

    public TreasureChest(UUID owner, final TreasureChestDesign design, Location preLoc) {
        if (owner == null) return;

        this.instance = this;
        this.design = design;
        this.particleEffect = design.getEffect();
        this.owner = owner;
        this.preLoc = preLoc;
        
        UltraCosmetics uc = UltraCosmeticsData.get().getPlugin();
        UltraPlayerManager pm = uc.getPlayerManager();

        Bukkit.getPluginManager().registerEvents(this, uc);

        this.player = getPlayer();

        Location loc = getPlayer().getLocation().getBlock().getLocation();
        Block centerPossibleBlock = loc.getBlock();
        if(centerPossibleBlock.getType() != Material.AIR) {
            // Save the block
            blocksToRestore.put(centerPossibleBlock, centerPossibleBlock.getState());

            // Temporarly remove it
            centerPossibleBlock.setType(Material.AIR);
        }

        if (pm.getUltraPlayer(getPlayer()).getCurrentMorph() != null)
            pm.getUltraPlayer(getPlayer()).setSeeSelfMorph(false);

        this.randomGenerator = new TreasureRandomizer(getPlayer(), getPlayer().getLocation());

        BukkitRunnable runnable = new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                if ((getPlayer() == null) || (pm.getUltraPlayer(getPlayer()).getCurrentTreasureChest() != instance)) {
                    cancel();
                    return;
                }
                try {
                    if (this.i == 0) {
                        RUNNABLES[0] = new BukkitRunnable() {
                            int i = 4;

                            @Override
                            public void run() {
                                if (i <= 0) {
                                    cancel();
                                    return;
                                }
                                if ((getPlayer() == null) || (pm.getUltraPlayer(getPlayer()).getCurrentTreasureChest() != instance)) {
                                    cancel();
                                    return;
                                }
                                UtilParticles.playHelix(getChestLocation(this.i, center.clone()), 0.0F, particleEffect);
                                UtilParticles.playHelix(getChestLocation(this.i, center.clone()), 3.5F, particleEffect);
                                RUNNABLES[1] = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        try {

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

                                            BlockState blockState = b.getState();
                                            Directional data = (Directional) blockState.getData();
                                            data.setFacingDirection(blockFace);
                                            blockState.update(true, true);

                                            chests.add(b);
//                                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()),
//                                                    0f, 0f, 0f, 0f, 1, getChestLocation(i, getPlayer().getLocation()), 128);
                                            i--;
                                        } catch (Exception exc) {
                                            clear();
                                            exc.printStackTrace();
                                            cancel();
                                        }
                                    }
                                };
                                RUNNABLES[1].runTaskLater(uc, 30L);
                            }
                        };
                        RUNNABLES[0].runTaskTimer(uc, 0L, 50L);
                    }
                    Block lampBlock;
                    if (this.i == 5) {
                        lampBlock = getPlayer().getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
                        center = lampBlock.getLocation().add(0.5D, 1.0D, 0.5D);
                        doChestStage(Arrays.asList(lampBlock), design.getCenter());
//                        Particles.BLOCK_CRACK.display(new Particles.BlockData(lampBlock.getType(), lampBlock.getData()), 0f, 0f, 0f, 1f, 50, lampBlock.getLocation());
                    } else if (this.i == 4) {
                        doChestStage(getSurroundingBlocks(center.clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBlocks2());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                    } else if (this.i == 3) {
                        doChestStage(getSurroundingSurrounding(center.clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBlocks3());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                    } else if (this.i == 2) {
                        doChestStage(getBlock3(center.clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBelowChests());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                    } else if (this.i == 1) {
                        doChestStage(getSurroundingSurrounding(center.getBlock()), design.getBarriers());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                    }
                    this.i -= 1;
                } catch (Exception exc) {
                    cancel();
                    exc.printStackTrace();
                    forceOpen(0);
                }
            }
        };
        runnable.runTaskTimer(uc, 0L, 12L);

        final TreasureChest treasureChest = this;

        Bukkit.getScheduler().runTaskLater(uc, () -> {
            if (pm.getUltraPlayer(player).getCurrentTreasureChest() == treasureChest)
                forceOpen(45);
        }, 1200L);

        pm.getUltraPlayer(getPlayer()).setCurrentTreasureChest(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                if ((getPlayer() == null) ||
                        (pm.getUltraPlayer(TreasureChest.this
                                .getPlayer()) == null) ||
                        (pm.getUltraPlayer(TreasureChest.this
                                .getPlayer()).getCurrentTreasureChest() != treasureChest)) {
                    for (Entity entity : holograms)
                        entity.remove();
                    cancel();
                    return;
                }
                if (!getPlayer().getWorld().getName().equals(center.getWorld().getName()))
                    getPlayer().teleport(center);
                if (getPlayer().getLocation().distance(center) > 1.5D)
                    getPlayer().teleport(center);
                for (Entity ent : player.getNearbyEntities(2.0D, 2.0D, 2.0D))
                    if ((pm.getUltraPlayer(player).getCurrentPet() == null) || (
                            (ent != pm.getUltraPlayer(player).getCurrentPet()) &&
                                    (!pm.getUltraPlayer(player).getCurrentPet().items
                                            .contains(ent)))) {
                        if ((!items.contains(ent)) &&
                                (ent != TreasureChest.this
                                        .getPlayer()) &&
                                (!holograms
                                        .contains(ent))) {
                            Vector v = ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).multiply(0.5D).add(new Vector(0.0D, 1.5D, 0.0D));
                            v.setY(0);
                            v.add(new Vector(0, 1, 0));
                            MathUtils.applyVelocity(ent, v.add(MathUtils.getRandomCircleVector().multiply(0.2D)));
                        }
                    }
            }
        }.runTaskTimer(uc, 0L, 1L);
    }
    
    private void doChestStage(Iterable<Block> blocks, MaterialData newData) {
        for (Block b : blocks) {
            blocksToRestore.put(b, b.getState());
            BlockUtils.treasureBlocks.add(b);
            b.setType(newData.getItemType());
            b.getState().setData(newData);
            //b.getState().update(true, true); // I don't think this does anything?
        }
    }

    public Player getPlayer() {
        if (this.owner != null)
            return Bukkit.getPlayer(this.owner);
        return null;
    }

    public void clear() {
        for (BlockState b : this.blocksToRestore.values()) {
//            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
            b.update(true);
            BlockUtils.treasureBlocks.remove(b.getBlock());
        }
        if (!this.stopping) {
            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
                        for (Entity hologram : holograms)
                            hologram.remove();
                        for (Block b : chestsToRemove) {
//                        Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                            b.setType(Material.AIR);
                        }
                        for (Block b : chests) {
//                        Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                            b.setType(Material.AIR);
                        }
                        if (items != null)
                            for (Entity ent : items)
                                ent.remove();
                        if (RUNNABLES[0] != null)
                            RUNNABLES[0].cancel();
                        if (RUNNABLES[1] != null)
                            RUNNABLES[1].cancel();
                        if (items != null)
                            items.clear();
                        if (chests != null)
                            chests.clear();
                        if (holograms != null)
                            holograms.clear();
                        if (chestsToRemove != null)
                            chestsToRemove.clear();
                        if (blocksToRestore != null)
                            blocksToRestore.clear();
                        if (getPlayer() != null && UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(getPlayer()) != null) {
                            UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(getPlayer()).setCurrentTreasureChest(null);
                            if (preLoc != null) {
                                getPlayer().teleport(preLoc);
                            }
                        }
                        owner = null;
                        if (randomGenerator != null)
                            randomGenerator.clear();
                    }
                    , 30L);
        } else {
            for (Entity hologram : this.holograms)
                hologram.remove();
            for (Block b : this.chestsToRemove) {
//                Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                b.setType(Material.AIR);
            }
            for (Block b : this.chests) {
//                Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                b.setType(Material.AIR);
            }
            for (Entity ent : this.items)
                ent.remove();
            if (this.RUNNABLES != null) {
                if (this.RUNNABLES[0] != null) {
                    this.RUNNABLES[0].cancel();
                }
                if (this.RUNNABLES[1] != null) {
                    this.RUNNABLES[1].cancel();
                }
            }
            this.items.clear();
            this.chests.clear();
            this.holograms.clear();
            this.chestsToRemove.clear();
            this.blocksToRestore.clear();
            if (getPlayer() != null) {
                UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(getPlayer()).setCurrentTreasureChest(null);
                if(preLoc != null) {
                    getPlayer().teleport(preLoc);
                }
            }
            this.owner = null;
            if (this.randomGenerator != null)
                this.randomGenerator.clear();
            this.randomGenerator = null;
        }
    }

    public List<Block> getSurroundingBlocks(Block b) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(b.getRelative(BlockFace.EAST));
        blocks.add(b.getRelative(BlockFace.WEST));
        blocks.add(b.getRelative(BlockFace.NORTH));
        blocks.add(b.getRelative(BlockFace.SOUTH));
        blocks.add(b.getRelative(BlockFace.SOUTH_EAST));
        blocks.add(b.getRelative(BlockFace.NORTH_WEST));
        blocks.add(b.getRelative(BlockFace.NORTH_EAST));
        blocks.add(b.getRelative(BlockFace.SOUTH_WEST));
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
            this.stopping = true;
            for (i = 0; i < this.chestsLeft; i++) {
                this.randomGenerator.giveRandomThing();
                getPlayer().sendMessage(MessageManager.getMessage("You-Won-Treasure-Chests").replace("%name%", this.randomGenerator.getName()));
            }
        } else {
            for (final Block b : this.chests) {
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().playChestAnimation(b, true, design);
                this.randomGenerator.loc = b.getLocation().clone().add(0.0D, 1.0D, 0.0D);
                this.randomGenerator.giveRandomThing();
                ItemStack is = this.randomGenerator.getItemStack();
                ItemMeta itemMeta = is.getItemMeta();
                itemMeta.setDisplayName(UltraCosmeticsData.get().getItemNoPickupString());
                is.setItemMeta(itemMeta);

                Entity entity = UltraCosmeticsData.get().getVersionManager().getEntityUtil().spawnItem(is, b.getLocation());

                this.items.add(entity);
                final String nameas = this.randomGenerator.getName();
                Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () ->
                        spawnHologram(b.getLocation().clone().add(0.5D, UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_9_R1 ?
                                -0.7 : 0.3D, 0.5D), nameas), 15L);

                this.chestsLeft -= 1;
                this.chestsToRemove.add(b);
            }
            this.chests.clear();

            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, delay);
        }
    }

    public List<Block> getSurroundingSurrounding(Block b) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(b.getRelative(2, 0, 1));
        blocks.add(b.getRelative(2, 0, -1));
        blocks.add(b.getRelative(2, 0, 2));
        blocks.add(b.getRelative(2, 0, -2));
        blocks.add(b.getRelative(1, 0, -2));
        blocks.add(b.getRelative(1, 0, 2));
        blocks.add(b.getRelative(-1, 0, -2));
        blocks.add(b.getRelative(-1, 0, 2));
        blocks.add(b.getRelative(-2, 0, 1));
        blocks.add(b.getRelative(-2, 0, -1));
        blocks.add(b.getRelative(-2, 0, 2));
        blocks.add(b.getRelative(-2, 0, -2));
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
        if (this.blocksToRestore.containsKey(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private void spawnHologram(Location location, String s) {
        if (UltraCosmeticsData.get().getServerVersion().compareTo(ServerVersion.v1_9_R1) > 0
                && UltraCosmeticsData.get().getServerVersion().compareTo(ServerVersion.v1_11_R1) < 0) {
            location.setY(location.getY() - 1);
        }
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.valueOf("ARMOR_STAND"));
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomName(s);
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "C_AD_ArmorStand"));
        this.holograms.add(armorStand);
    }

    @EventHandler
    public void onInter(final PlayerInteractEvent event) {
        if ((event.getClickedBlock() != null) &&
                (event.getClickedBlock().getType() == Material.CHEST
                        || event.getClickedBlock().getType() == Material.ENDER_CHEST
                        || event.getClickedBlock().getType() == Material.TRAPPED_CHEST) &&
                (this.chests.contains(event.getClickedBlock())) && (!this.cooldown)) {
            if (event.getPlayer() == getPlayer()) {
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().playChestAnimation(event.getClickedBlock(), true, design);
                this.randomGenerator.loc = event.getClickedBlock().getLocation().add(0.0D, 1.0D, 0.0D);
                this.randomGenerator.giveRandomThing();

                this.cooldown = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmeticsData.get().getPlugin(), () -> cooldown = false, 3L);

                ItemStack is = this.randomGenerator.getItemStack();
                ItemMeta itemMeta = is.getItemMeta();
                itemMeta.setDisplayName(UltraCosmeticsData.get().getItemNoPickupString());
                is.setItemMeta(itemMeta);

                Entity itemEntity = UltraCosmeticsData.get().getVersionManager().getEntityUtil().spawnItem(is, event.getClickedBlock().getLocation());

                this.items.add(itemEntity);
                final String nameas = this.randomGenerator.getName();
                Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () ->
                        spawnHologram(event.getClickedBlock().getLocation().add(0.5D, UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_9_R1 ?
                                -0.7 : 0.3D, 0.5D), nameas), 15L);

                this.chestsLeft -= 1;
                this.chests.remove(event.getClickedBlock());
                this.chestsToRemove.add(event.getClickedBlock());
                if (this.chestsLeft == 0)
                    Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, 50L);
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if ((event.getPlayer() == getPlayer()) &&
                (event.getReason().contains("Fly"))) {
            event.setCancelled(true);
            event.getPlayer().teleport(this.center);
        }
    }

    public Location getChestLocation(int i, Location loc) {
        Location chestLocation = this.center.clone();
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
