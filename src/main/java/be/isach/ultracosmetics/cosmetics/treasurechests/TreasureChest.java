package be.isach.ultracosmetics.cosmetics.treasurechests;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UtilParticles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.TileEntityEnderChest;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public abstract class TreasureChest
        implements Listener {
    Map<Location, Material> oldMaterials = new HashMap();
    Map<Location, Byte> oldDatas = new HashMap();
    ArrayList<Block> blocksToRestore = new ArrayList();
    ArrayList<Block> chests = new ArrayList();
    ArrayList<Block> chestsToRemove = new ArrayList();
    public UUID owner;
    public Material barriersMaterial;
    public Material b1Mat;
    public Material b2Mat;
    public Material b3Mat;
    public Material lampMaterial;
    private final BukkitRunnable[] RUNNABLES = new BukkitRunnable[2];
    TreasureChest instance;
    public boolean enderChests = false;
    TreasureRandomizer randomGenerator;
    public Byte b1data = Byte.valueOf((byte) 0);
    public Byte b2data = Byte.valueOf((byte) 0);
    public Byte b3data = Byte.valueOf((byte) 0);
    public Byte barrierData = Byte.valueOf((byte) 0);
    Location center;
    Effect particleEffect;
    int chestsLeft = 4;
    private Player player;
    private List<Entity> items = new ArrayList();
    private List<Entity> holograms = new ArrayList();
    boolean stopping;
    boolean cooldown = false;

    public TreasureChest(UUID owner, Material barriers, Material b1, Material b2, Material b3, Material lamp, Effect effect) {
        if (owner == null) return;

        this.instance = this;

        this.particleEffect = effect;
        this.owner = owner;
        this.barriersMaterial = barriers;
        this.b1Mat = b1;
        this.b2Mat = b2;
        this.b3Mat = b3;
        this.lampMaterial = lamp;

        Core.registerListener(this);

        this.player = getPlayer();

        this.randomGenerator = new TreasureRandomizer(getPlayer(), getPlayer().getLocation());

        BukkitRunnable runnable = new BukkitRunnable() {
            int i = 5;

            public void run() {
                if ((TreasureChest.this.getPlayer() == null) || (Core.getCustomPlayer(TreasureChest.this.getPlayer()).currentTreasureChest != TreasureChest.this.instance)) {
                    cancel();
                    return;
                }
                try {
                    if (this.i == 0) {
                        TreasureChest.this.RUNNABLES[0] = new BukkitRunnable() {
                            int i = 4;

                            public void run() {
                                if ((TreasureChest.this.getPlayer() == null) || (Core.getCustomPlayer(TreasureChest.this.getPlayer()).currentTreasureChest != TreasureChest.this.instance)) {
                                    cancel();
                                    return;
                                }
                                UtilParticles.playHelix(TreasureChest.this.getChestLocation(this.i, TreasureChest.this.center.clone()), 0.0F, TreasureChest.this.particleEffect);
                                UtilParticles.playHelix(TreasureChest.this.getChestLocation(this.i, TreasureChest.this.center.clone()), 3.5F, TreasureChest.this.particleEffect);
                                TreasureChest.this.RUNNABLES[1] = new BukkitRunnable() {
                                    public void run() {
                                        try {
                                            Block b = getChestLocation(i, center.clone()).getBlock();
                                            b.setType(Material.CHEST);
                                            if (enderChests)
                                                b.setType(Material.ENDER_CHEST);
                                            getPlayer().playSound(getPlayer().getLocation(), Sound.ANVIL_LAND, 2, 1);
                                            for (int i = 0; i < 5; i++) {
                                                UtilParticles.play(b.getLocation(), Effect.LARGE_SMOKE);
                                                UtilParticles.play(b.getLocation(), Effect.LAVA_POP);
                                            }
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
                                            if (enderChests) {
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
                                            UtilParticles.play(getChestLocation(i, getPlayer().getLocation()), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0, 0, 0, 1, 50);
                                            i--;
                                            if (i == 0)
                                                cancel();
                                        } catch (Exception exc) {
                                            TreasureChest.this.clear();
                                            cancel();
                                        }
                                    }
                                };
                                TreasureChest.this.RUNNABLES[1].runTaskLater(Core.getPlugin(), 30L);
                            }
                        };
                        TreasureChest.this.RUNNABLES[0].runTaskTimer(Core.getPlugin(), 0L, 50L);
                    }
                    Block lampBlock;
                    if (this.i == 5) {
                        lampBlock = TreasureChest.this.getPlayer().getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
                        TreasureChest.this.center = lampBlock.getLocation().add(0.5D, 1.0D, 0.5D);
                        TreasureChest.this.oldMaterials.put(lampBlock.getLocation(), lampBlock.getType());
                        TreasureChest.this.oldDatas.put(lampBlock.getLocation(), Byte.valueOf(lampBlock.getData()));
                        TreasureChest.this.blocksToRestore.add(lampBlock);
                        lampBlock.setType(TreasureChest.this.lampMaterial);
                        UtilParticles.play(lampBlock.getLocation(), Effect.STEP_SOUND, lampBlock.getTypeId(), lampBlock.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                    } else if (this.i == 4) {
                        for (Block b : TreasureChest.this.getSurroundingBlocks(TreasureChest.this.center.clone().add(0.0D, -1.0D, 0.0D).getBlock())) {
                            TreasureChest.this.oldMaterials.put(b.getLocation(), b.getType());
                            TreasureChest.this.oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            TreasureChest.this.blocksToRestore.add(b);
                            b.setType(TreasureChest.this.b1Mat);
                            b.setData(TreasureChest.this.b1data.byteValue());
                            UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                        }
                    } else if (this.i == 3) {
                        for (Block b : TreasureChest.this.getSurroundingSurrounding(TreasureChest.this.center.clone().add(0.0D, -1.0D, 0.0D).getBlock())) {
                            TreasureChest.this.oldMaterials.put(b.getLocation(), b.getType());
                            TreasureChest.this.oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            TreasureChest.this.blocksToRestore.add(b);
                            b.setType(TreasureChest.this.b2Mat);
                            b.setData(TreasureChest.this.b2data.byteValue());
                            UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                        }
                    } else if (this.i == 2) {
                        for (Block b : TreasureChest.this.getBlock3(TreasureChest.this.center.clone().add(0.0D, -1.0D, 0.0D).getBlock())) {
                            TreasureChest.this.oldMaterials.put(b.getLocation(), b.getType());
                            TreasureChest.this.oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            TreasureChest.this.blocksToRestore.add(b);
                            b.setType(TreasureChest.this.b3Mat);
                            b.setData(TreasureChest.this.b3data.byteValue());
                            UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                        }
                    } else if (this.i == 1) {
                        for (Block b : TreasureChest.this.getSurroundingSurrounding(TreasureChest.this.center.getBlock())) {
                            TreasureChest.this.oldMaterials.put(b.getLocation(), b.getType());
                            TreasureChest.this.oldDatas.put(b.getLocation(), Byte.valueOf(b.getData()));
                            TreasureChest.this.blocksToRestore.add(b);
                            b.setType(TreasureChest.this.barriersMaterial);
                            b.setData(TreasureChest.this.barrierData.byteValue());
                            UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                        }
                    }
                    this.i -= 1;
                } catch (Exception exc) {
                    cancel();
                    TreasureChest.this.forceOpen(0);
                }
            }
        };
        runnable.runTaskTimer(Core.getPlugin(), 0L, 12L);

        final TreasureChest treasureChest = this;

        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            public void run() {
                if (Core.getCustomPlayer(TreasureChest.this.player).currentTreasureChest == treasureChest)
                    TreasureChest.this.forceOpen(45);
            }
        }
                , 1200L);

        Core.getCustomPlayer(getPlayer()).currentTreasureChest = this;

        new BukkitRunnable() {
            public void run() {
                if ((TreasureChest.this.getPlayer() == null) ||
                        (Core.getCustomPlayer(TreasureChest.this
                                .getPlayer()) == null) ||
                        (Core.getCustomPlayer(TreasureChest.this
                                .getPlayer()).currentTreasureChest != treasureChest)) {
                    for (Entity entity : TreasureChest.this.holograms)
                        entity.remove();
                    cancel();
                    return;
                }
                if (!TreasureChest.this.getPlayer().getWorld().getName().equals(TreasureChest.this.center.getWorld().getName()))
                    TreasureChest.this.getPlayer().teleport(TreasureChest.this.center);
                if (TreasureChest.this.getPlayer().getLocation().distance(TreasureChest.this.center) > 1.5D)
                    TreasureChest.this.getPlayer().teleport(TreasureChest.this.center);
                for (Entity ent : TreasureChest.this.player.getNearbyEntities(2.0D, 2.0D, 2.0D))
                    if ((Core.getCustomPlayer(TreasureChest.this.player).currentPet == null) || (
                            (ent != Core.getCustomPlayer(TreasureChest.this.player).currentPet) &&
                                    (!Core.getCustomPlayer(TreasureChest.this.player).currentPet.items
                                            .contains(ent)))) {
                        if ((!TreasureChest.this.items.contains(ent)) &&
                                (ent != TreasureChest.this
                                        .getPlayer()) &&
                                (!TreasureChest.this.holograms
                                        .contains(ent))) {
                            Vector v = ent.getLocation().toVector().subtract(TreasureChest.this.getPlayer().getLocation().toVector()).multiply(0.5D).add(new Vector(0.0D, 1.5D, 0.0D));
                            v.setY(0);
                            v.add(new Vector(0, 1, 0));
                            MathUtils.applyVelocity(ent, v.add(MathUtils.getRandomCircleVector().multiply(0.2D)));
                        }
                    }
            }
        }
                .runTaskTimer(Core.getPlugin(), 0L, 1L);
    }

    public Player getPlayer() {
        if (this.owner != null)
            return Bukkit.getPlayer(this.owner);
        return null;
    }

    public void clear() {
        for (Block b : this.blocksToRestore) {
            UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
            b.setType((Material) this.oldMaterials.get(b.getLocation()));
            b.setData(((Byte) this.oldDatas.get(b.getLocation())).byteValue());
        }
        if (!this.stopping) {
            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                public void run() {
                    for (Entity hologram : TreasureChest.this.holograms)
                        hologram.remove();
                    for (Block b : TreasureChest.this.chestsToRemove) {
                        UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                        b.setType(Material.AIR);
                    }
                    for (Block b : TreasureChest.this.chests) {
                        UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                        b.setType(Material.AIR);
                    }
                    for (Entity ent : TreasureChest.this.items)
                        ent.remove();
                    TreasureChest.this.RUNNABLES[0].cancel();
                    TreasureChest.this.RUNNABLES[1].cancel();
                    TreasureChest.this.items.clear();
                    TreasureChest.this.chests.clear();
                    TreasureChest.this.holograms.clear();
                    TreasureChest.this.chestsToRemove.clear();
                    TreasureChest.this.blocksToRestore.clear();
                    if (Core.getCustomPlayer(TreasureChest.this.getPlayer()) != null)
                        Core.getCustomPlayer(TreasureChest.this.getPlayer()).currentTreasureChest = null;
                    TreasureChest.this.owner = null;
                    TreasureChest.this.randomGenerator.clear();
                }
            }
                    , 30L);
        } else {
            for (Entity hologram : this.holograms)
                hologram.remove();
            for (Block b : this.chestsToRemove) {
                UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
                b.setType(Material.AIR);
            }
            for (Block b : this.chests) {
                UtilParticles.play(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 50);
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
                Core.getCustomPlayer(getPlayer()).currentTreasureChest = null;
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
                Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                    public void run() {
                        TreasureChest.this.spawnHologram(b.getLocation().clone().add(0.5D, 0.3D, 0.5D), nameas);
                    }
                }
                        , 15L);

                this.chestsLeft -= 1;
                this.chestsToRemove.add(b);
            }
            this.chests.clear();

            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                public void run() {
                    TreasureChest.this.clear();
                }
            }
                    , delay);
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
        if (this.enderChests) {
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
        this.holograms.add(armorStand);
    }

    @EventHandler
    public void onInter(final PlayerInteractEvent event) {
        if ((event.getClickedBlock() != null) &&
                ((event
                        .getClickedBlock().getType() == Material.CHEST) ||
                        (event
                                .getClickedBlock().getType() == Material.ENDER_CHEST)) &&
                (this.chests
                        .contains(event
                                .getClickedBlock())) && (!this.cooldown)) {
            if (event
                    .getPlayer() == getPlayer()) {
                playChestAction(event.getClickedBlock(), true);
                this.randomGenerator.loc = event.getClickedBlock().getLocation().add(0.0D, 1.0D, 0.0D);
                this.randomGenerator.giveRandomThing();

                this.cooldown = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                    public void run() {
                        TreasureChest.this.cooldown = false;
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
                Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                    public void run() {
                        TreasureChest.this.spawnHologram(event.getClickedBlock().getLocation().add(0.5D, 0.3D, 0.5D), nameas);
                    }
                }
                        , 15L);

                this.chestsLeft -= 1;
                this.chests.remove(event.getClickedBlock());
                this.chestsToRemove.add(event.getClickedBlock());
                if (this.chestsLeft == 0)
                    Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                        public void run() {
                            TreasureChest.this.clear();
                        }
                    }
                            , 50L);
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if ((event.getPlayer() == getPlayer()) &&
                (event
                        .getReason().contains("Fly"))) {
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