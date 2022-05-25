package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.Particles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class TreasureChest implements Listener {

    private final Map<Block, BlockState> blocksToRestore = new HashMap<>();
    private final List<Block> chests = new ArrayList<>();
    private final List<Block> chestsToRemove = new ArrayList<>();
    private final UUID owner;
    private TreasureRandomizer randomGenerator;
    private Location center;
    private final Particles particleEffect;
    private int chestsLeft = SettingsManager.getConfig().getInt("TreasureChests.Count", 4);
    private Player player;
    private Set<Item> items = new HashSet<>();
    private Set<ArmorStand> holograms = new HashSet<>();
    private boolean stopping;
    private boolean cooldown = false;
    private final TreasureChestDesign design;
    private final Location preLoc;
    private final TreasureLocation treasureLoc;
    private final PlaceBlocksRunnable blocksRunnable;

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

        if (pm.getUltraPlayer(getPlayer()).getCurrentMorph() != null) {
            pm.getUltraPlayer(getPlayer()).setSeeSelfMorph(false);
        }

        this.randomGenerator = new TreasureRandomizer(getPlayer(), getPlayer().getLocation());

        blocksRunnable = new PlaceBlocksRunnable(this);
        blocksRunnable.runTaskTimer(uc, 0L, 12L);

        Bukkit.getScheduler().runTaskLater(uc, () -> {
            if (pm.getUltraPlayer(player) != null && pm.getUltraPlayer(player).getCurrentTreasureChest() == TreasureChest.this) {
                forceOpen(45);
            }
        }, 1200L);

        pm.getUltraPlayer(getPlayer()).setCurrentTreasureChest(this);

        new PlayerBounceRunnable(this).runTaskTimer(uc, 0L, 1L);
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
        for (ArmorStand hologram : holograms) {
            hologram.remove();
        }
        for (Item item : items) {
            item.remove();
        }
        for (Block b : chestsToRemove) {
            b.setType(Material.AIR);
        }
        for (Block b : chests) {
            b.setType(Material.AIR);
        }
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
        // cancels all child runnables as well
        blocksRunnable.propogateCancel();
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

            items.add(spawnItem(is, b.getLocation()));
            final String name = randomGenerator.getName();
            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () ->
                    spawnHologram(b.getLocation().clone().add(0.5D, 0.3D, 0.5D), name), 15L);

            chestsLeft -= 1;
            chestsToRemove.add(b);
        }
        chests.clear();

        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, delay);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (blocksToRestore.containsKey(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private void spawnHologram(Location location, String s) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomName(s);
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "C_AD_ArmorStand"));
        holograms.add(armorStand);
    }

    public boolean isSpecialEntity(Entity entity) {
        return items.contains(entity) || holograms.contains(entity);
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

                items.add(spawnItem(is, event.getClickedBlock().getLocation()));
                final String nameas = randomGenerator.getName();
                Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () ->
                        spawnHologram(event.getClickedBlock().getLocation().add(0.5D, 0.3D, 0.5D), nameas), 15L);

                chestsLeft -= 1;
                chests.remove(event.getClickedBlock());
                chestsToRemove.add(event.getClickedBlock());
                if (chestsLeft == 0) {
                    Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, 50L);
                }
            }
        }
    }

    private Item spawnItem(ItemStack stack, Location loc) {
        return ItemFactory.spawnUnpickableItem(stack, loc.clone().add(0.5, 1.2, 0.5), new Vector(0, 0.25, 0));
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

    public TreasureLocation getTreasureLocation() {
        return treasureLoc;
    }

    public Particles getParticleEffect() {
        return particleEffect;
    }

    protected void setCenter(Location loc) {
        center = loc;
    }

    public Location getCenter() {
        return center.clone();
    }

    public TreasureChestDesign getDesign() {
        return design;
    }

    public void addChest(Block b) {
        chests.add(b);
    }

    public void addRestoreBlock(Block b) {
        blocksToRestore.put(b, b.getState());
    }

    public int getChestsLeft() {
        return chestsLeft;
    }

    protected void setChestsLeft(int chestsLeft) {
        this.chestsLeft = chestsLeft;
    }

    /**
     * Cancel eggs from merging
     *
     * @param event
     */
    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        if (items.contains(event.getEntity()) || items.contains(event.getTarget())) {
            event.setCancelled(true);
        }
    }
}
