package be.isach.ultracosmetics.cosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.TileEntityEnderChest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Chest;
import org.bukkit.material.EnderChest;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class TreasureChest
        implements Listener {
    Map<Location, Material> oldMaterials = new HashMap();
    Map<Location, Byte> oldDatas = new HashMap();
    ArrayList<Block> blocksToRestore = new ArrayList();
    ArrayList<Block> chests = new ArrayList();
    ArrayList<Block> chestsToRemove = new ArrayList();
    public UUID owner;
    private final BukkitRunnable[] RUNNABLES = new BukkitRunnable[2];
    TreasureChest instance;
    TreasureRandomizer randomGenerator;
    Location center;
    Particles particleEffect;
    int chestsLeft = 4;
    private Player player;
    private List<Entity> items = new ArrayList();
    private List<Entity> holograms = new ArrayList();
    boolean stopping;
    boolean cooldown = false;
    private TreasureChestDesign design;

    public TreasureChest(UUID owner, final TreasureChestDesign design) {
        if (owner == null) return;

        this.instance = this;
        this.design = design;
        this.particleEffect = design.getEffect();
        this.owner = owner;

        UltraCosmetics.getInstance().registerListener(this);

        this.player = getPlayer();

        if (UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph != null)
            UltraCosmetics.getCustomPlayer(getPlayer()).setSeeSelfMorph(false);

        this.randomGenerator = new TreasureRandomizer(getPlayer(), getPlayer().getLocation());

        BukkitRunnable runnable = new BukkitRunnable() {
            int i = 5;

            public void run() {
                if ((getPlayer() == null) || (UltraCosmetics.getCustomPlayer(getPlayer()).currentTreasureChest != instance)) {
                    cancel();
                    return;
                }
                try {
                    if (this.i == 0) {
                        RUNNABLES[0] = new BukkitRunnable() {
                            int i = 4;

                            public void run() {
                                if (i <= 0) {
                                    cancel();
                                    return;
                                }
                                if ((getPlayer() == null) || (UltraCosmetics.getCustomPlayer(getPlayer()).currentTreasureChest != instance)) {
                                    cancel();
                                    return;
                                }
                                UtilParticles.playHelix(getChestLocation(this.i, center.clone()), 0.0F, particleEffect);
                                UtilParticles.playHelix(getChestLocation(this.i, center.clone()), 3.5F, particleEffect);
                                RUNNABLES[1] = new BukkitRunnable() {
                                    public void run() {
                                        try {

                                            Block b = getChestLocation(i, center.clone()).getBlock();
                                            b.setType(design.getChestType().getType());
                                            switch (UltraCosmetics.getServerVersion()) {
                                                case v1_8_R3:
                                                    getPlayer().playSound(getPlayer().getLocation(), Sound.valueOf("ANVIL_LAND"), 1.4f, 1.5f);
                                                    break;
                                                case v1_9_R1:
                                                    getPlayer().playSound(getPlayer().getLocation(), Sound.BLOCK_ANVIL_LAND, 1.4f, 1.5f);
                                                    break;
                                            }
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
                                            if (design.getChestType() == ChestType.ENDER) {
                                                EnderChest enderChest = (EnderChest) b.getState().getData();
                                                enderChest.setFacingDirection(blockFace);
                                                blockState.setData(enderChest);
                                            } else {
                                                Chest chest = (Chest) b.getState().getData();
                                                chest.setFacingDirection(blockFace);
                                                blockState.setData(chest);
                                            }
                                            blockState.update();

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
                                RUNNABLES[1].runTaskLater(UltraCosmetics.getInstance(), 30L);
                            }
                        };
                        RUNNABLES[0].runTaskTimer(UltraCosmetics.getInstance(), 0L, 50L);
                    }
                    Block lampBlock;
                    if (this.i == 5) {
                        lampBlock = getPlayer().getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
                        center = lampBlock.getLocation().add(0.5D, 1.0D, 0.5D);
                        oldMaterials.put(lampBlock.getLocation(), lampBlock.getType());
                        oldDatas.put(lampBlock.getLocation(), Byte.valueOf(lampBlock.getData()));
                        blocksToRestore.add(lampBlock);
                        lampBlock.setType(design.getCenter().getItemType());
                        lampBlock.setData(design.getCenter().getData());
//                        Particles.BLOCK_CRACK.display(new Particles.BlockData(lampBlock.getType(), lampBlock.getData()), 0f, 0f, 0f, 1f, 50, lampBlock.getLocation());
                    } else if (this.i == 4) {
                        for (Block b : getSurroundingBlocks(center.clone().add(0.0D, -1.0D, 0.0D).getBlock())) {
                            oldMaterials.put(b.getLocation(), b.getType());
                            oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            blocksToRestore.add(b);
                            BlockUtils.treasureBlocks.add(b);
                            b.setType(design.getBlocks2().getItemType());
                            b.setData(design.getBlocks2().getData());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                        }
                    } else if (this.i == 3) {
                        for (Block b : getSurroundingSurrounding(center.clone().add(0.0D, -1.0D, 0.0D).getBlock())) {
                            oldMaterials.put(b.getLocation(), b.getType());
                            oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            blocksToRestore.add(b);
                            BlockUtils.treasureBlocks.add(b);
                            b.setType(design.getBlocks3().getItemType());
                            b.setData(design.getBlocks3().getData());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                        }
                    } else if (this.i == 2) {
                        for (Block b : getBlock3(center.clone().add(0.0D, -1.0D, 0.0D).getBlock())) {
                            oldMaterials.put(b.getLocation(), b.getType());
                            oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            blocksToRestore.add(b);
                            BlockUtils.treasureBlocks.add(b);
                            BlockUtils.treasureBlocks.add(b);
                            b.setType(design.getBelowChests().getItemType());
                            b.setData(design.getBelowChests().getData());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                        }
                    } else if (this.i == 1) {
                        for (Block b : getSurroundingSurrounding(center.getBlock())) {
                            oldMaterials.put(b.getLocation(), b.getType());
                            oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            blocksToRestore.add(b);
                            BlockUtils.treasureBlocks.add(b);
                            b.setType(design.getBarriers().getItemType());
                            b.setData(design.getBarriers().getData());
//                            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
                        }
                    }
                    this.i -= 1;
                } catch (Exception exc) {
                    cancel();
                    exc.printStackTrace();
                    forceOpen(0);
                }
            }
        };
        runnable.runTaskTimer(UltraCosmetics.getInstance(), 0L, 12L);

        final TreasureChest treasureChest = this;

        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            public void run() {
                if (UltraCosmetics.getCustomPlayer(player).currentTreasureChest == treasureChest)
                    forceOpen(45);
            }
        }
                , 1200L);

        UltraCosmetics.getCustomPlayer(getPlayer()).currentTreasureChest = this;

        new BukkitRunnable() {
            public void run() {
                if ((getPlayer() == null) ||
                        (UltraCosmetics.getCustomPlayer(TreasureChest.this
                                .getPlayer()) == null) ||
                        (UltraCosmetics.getCustomPlayer(TreasureChest.this
                                .getPlayer()).currentTreasureChest != treasureChest)) {
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
                    if ((UltraCosmetics.getCustomPlayer(player).currentPet == null) || (
                            (ent != UltraCosmetics.getCustomPlayer(player).currentPet) &&
                                    (!UltraCosmetics.getCustomPlayer(player).currentPet.items
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
        }
                .runTaskTimer(UltraCosmetics.getInstance(), 0L, 1L);
    }

    public Player getPlayer() {
        if (this.owner != null)
            return Bukkit.getPlayer(this.owner);
        return null;
    }

    public void clear() {
        for (Block b : this.blocksToRestore) {
//            Particles.BLOCK_CRACK.display(new Particles.BlockData(b.getType(), b.getData()), 0f, 0f, 0f, 1f, 50, b.getLocation());
            b.setType((Material) this.oldMaterials.get(b.getLocation()));
            b.setData(((Byte) this.oldDatas.get(b.getLocation())).byteValue());
            BlockUtils.treasureBlocks.remove(b);
        }
        if (!this.stopping) {
            Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                public void run() {
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
                    if (UltraCosmetics.getCustomPlayer(getPlayer()) != null)
                        UltraCosmetics.getCustomPlayer(getPlayer()).currentTreasureChest = null;
                    owner = null;
                    if (randomGenerator != null)
                        randomGenerator.clear();
                }
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
            this.RUNNABLES[0].cancel();
            this.RUNNABLES[1].cancel();
            this.items.clear();
            this.chests.clear();
            this.holograms.clear();
            this.chestsToRemove.clear();
            this.blocksToRestore.clear();
            if (getPlayer() != null)
                UltraCosmetics.getCustomPlayer(getPlayer()).currentTreasureChest = null;
            this.owner = null;
            if (this.randomGenerator != null)
                this.randomGenerator.clear();
            this.randomGenerator = null;
        }
    }

    public List<Block> getSurroundingBlocks(Block b) {
        List blocks = new ArrayList();
        blocks.add(b.getRelative(BlockFace.EAST));
        blocks.add(b.getRelative(BlockFace.WEST));
        blocks.add(b.getRelative(BlockFace.NORTH));
        blocks.add(b.getRelative(BlockFace.SOUTH));
        blocks.add(b.getRelative(1, 0, 1));
        blocks.add(b.getRelative(-1, 0, -1));
        blocks.add(b.getRelative(1, 0, -1));
        blocks.add(b.getRelative(-1, 0, 1));
        return blocks;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if ((event.getPlayer() == getPlayer()) && (
                (event.getFrom().getBlockX() != event.getTo().getBlockX()) ||
                        (event
                                .getFrom().getBlockY() != event.getTo().getBlockY()) ||
                        (event
                                .getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getFrom());
        }
    }

    public void forceOpen(int delay) {
        int i;
        if (delay == 0) {
            this.stopping = true;
            for (i = 0; i < 4; i++) {
                this.randomGenerator.giveRandomThing();
                getPlayer().sendMessage(MessageManager.getMessage("You-Won-Treasure-Chests").replace("%name%", this.randomGenerator.getName()));
            }
        } else {
            for (final Block b : this.chests) {
                playChestAction(b, true);
                this.randomGenerator.loc = b.getLocation().clone().add(0.0D, 1.0D, 0.0D);
                this.randomGenerator.giveRandomThing();
                org.bukkit.inventory.ItemStack is = this.randomGenerator.getItemStack();
                ItemMeta itemMeta = is.getItemMeta();
                itemMeta.setDisplayName(UUID.randomUUID().toString());
                is.setItemMeta(itemMeta);

                EntityItem ei = new EntityItem(
                        ((CraftWorld) b
                                .getLocation().clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle(), b
                        .getLocation().clone().add(0.5D, 1.2D, 0.5D).getX(), b
                        .getLocation().clone().add(0.5D, 1.2D, 0.5D).getY(), b
                        .getLocation().clone().add(0.5D, 1.2D, 0.5D).getZ(),
                        CraftItemStack.asNMSCopy(is)) {
                    public boolean a(EntityItem entityitem) {
                        return false;
                    }
                };
                ei.getBukkitEntity().setVelocity(new Vector(0.0D, 0.25D, 0.0D));
                ei.pickupDelay = 2147483647;
                ei.getBukkitEntity().setCustomName(UUID.randomUUID().toString());

                ((CraftWorld) b.getLocation().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle().addEntity(ei);

                this.items.add(ei.getBukkitEntity());
                final String nameas = this.randomGenerator.getName();
                Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                    public void run() {
                        spawnHologram(b.getLocation().clone().add(0.5D, 0.3D, 0.5D), nameas);
                    }
                }
                        , 15L);

                this.chestsLeft -= 1;
                this.chestsToRemove.add(b);
            }
            this.chests.clear();

            Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                public void run() {
                    clear();
                }
            }, delay);
        }
    }

    public List<Block> getSurroundingSurrounding(Block b) {
        List blocks = new ArrayList();
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
        List blocks = new ArrayList();
        blocks.add(b.getRelative(-2, 0, 0));
        blocks.add(b.getRelative(2, 0, 0));
        blocks.add(b.getRelative(0, 0, 2));
        blocks.add(b.getRelative(0, 0, -2));
        return blocks;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (this.blocksToRestore.contains(event.getBlock())) {
            event.setCancelled(true);
            return;
        }
    }

    public void playChestAction(Block b, boolean open) {
        Location location = b.getLocation();
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        if (design.getChestType() == ChestType.ENDER) {
            TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
            world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
        } else {
            TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
            world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
        }
    }

    private void spawnHologram(Location location, String s) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomName(s);
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(UltraCosmetics.getInstance(),"C_AD_ArmorStand"));
        this.holograms.add(armorStand);
    }

    @EventHandler
    public void onInter(final PlayerInteractEvent event) {
        if ((event.getClickedBlock() != null) &&
                (event.getClickedBlock().getType() == Material.CHEST
                        || event.getClickedBlock().getType() == Material.ENDER_CHEST
                        || event.getClickedBlock().getType() == Material.TRAPPED_CHEST) &&
                (this.chests
                        .contains(event
                                .getClickedBlock())) && (!this.cooldown)) {
            if (event
                    .getPlayer() == getPlayer()) {
                playChestAction(event.getClickedBlock(), true);
                this.randomGenerator.loc = event.getClickedBlock().getLocation().add(0.0D, 1.0D, 0.0D);
                this.randomGenerator.giveRandomThing();

                this.cooldown = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
                    public void run() {
                        cooldown = false;
                    }
                }
                        , 3L);

                org.bukkit.inventory.ItemStack is = this.randomGenerator.getItemStack();
                ItemMeta itemMeta = is.getItemMeta();
                itemMeta.setDisplayName(UUID.randomUUID().toString());
                is.setItemMeta(itemMeta);

                EntityItem ei = new EntityItem(
                        ((CraftWorld) event
                                .getClickedBlock().getLocation().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle(), event
                        .getClickedBlock().getLocation().add(0.5D, 1.2D, 0.5D).getX(), event
                        .getClickedBlock().getLocation().add(0.5D, 1.2D, 0.5D).getY(), event
                        .getClickedBlock().getLocation().add(0.5D, 1.2D, 0.5D).getZ(),
                        CraftItemStack.asNMSCopy(is)) {
                    public boolean a(EntityItem entityitem) {
                        return false;
                    }
                };
                ei.getBukkitEntity().setVelocity(new Vector(0.0D, 0.25D, 0.0D));
                ei.pickupDelay = 2147483647;
                ei.getBukkitEntity().setCustomName(UUID.randomUUID().toString());
                ei.pickupDelay = 20;

                ((CraftWorld) event.getClickedBlock().getLocation().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle().addEntity(ei);

                this.items.add(ei.getBukkitEntity());
                final String nameas = this.randomGenerator.getName();
                Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                    public void run() {
                        spawnHologram(event.getClickedBlock().getLocation().add(0.5D, 0.3D, 0.5D), nameas);
                    }
                }
                        , 15L);

                this.chestsLeft -= 1;
                this.chests.remove(event.getClickedBlock());
                this.chestsToRemove.add(event.getClickedBlock());
                if (this.chestsLeft == 0)
                    Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                        public void run() {
                            clear();
                        }
                    }
                            , 50L);
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
}
