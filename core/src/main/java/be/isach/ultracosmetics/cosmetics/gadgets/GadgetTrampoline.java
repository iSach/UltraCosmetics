package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraPlayer;
import be.isach.ultracosmetics.util.Cuboid;
import be.isach.ultracosmetics.util.EntityUtils;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Sacha on 19/12/15.
 */
public class GadgetTrampoline extends Gadget {

    private int duration = 12, durationInTicks;
    private Map<Block, MaterialData> trampoline = new HashMap<>();
    private Cuboid cuboid;
    private Location initialCenter;
    private boolean running;

    public GadgetTrampoline(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.TRAMPOLINE, ultraCosmetics);

        if (owner == null) return;

        Location loc1 = getPlayer().getLocation().add(-2, 0, -2);
        Location loc2 = getPlayer().getLocation().add(2, 15, 2);

        initialCenter = getPlayer().getLocation();

        this.cuboid = new Cuboid(loc1, loc2);

        if (duration > GadgetType.TRAMPOLINE.getCountdown())
            duration = (int) GadgetType.TRAMPOLINE.getCountdown() / 2;
        durationInTicks = duration * 20;
    }

    @Override
    void onRightClick() {
        Location loc1 = getPlayer().getLocation().add(-2, 0, -2);
        Location loc2 = getPlayer().getLocation().add(2, 15, 2);

        initialCenter = getPlayer().getLocation();

        this.cuboid = new Cuboid(loc1, loc2);

        clearBlocks();

        generateStructure();

        getPlayer().teleport(getPlayer().getLocation().add(0, 4, 0));

        running = true;
    }

    @Override
    void onLeftClick() {
    }

    @Override
    void onUpdate() {
        Bukkit.getScheduler().runTask(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Entity entity : EntityUtils.getEntitiesInRadius(initialCenter, 4d)) {
                    Block b = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
                    if (b.getType() == Material.WOOL
                            && cuboid.contains(b))
                        MathUtils.applyVelocity(entity, new Vector(0, 3, 0));
                }
            }
        });
    }

    @Override
    public void onClear() {
        clearBlocks();
        trampoline = null;
        running = false;
    }

    private void generateStructure() {
        genBarr(get(2, 0, 2));
        genBarr(get(-2, 0, 2));
        genBarr(get(2, 0, -2));
        genBarr(get(-2, 0, -2));

        genBlue(get(2, 1, 2));
        genBlue(get(2, 1, 1));
        genBlue(get(2, 1, 0));
        genBlue(get(2, 1, -1));
        genBlue(get(2, 1, -2));
        genBlue(get(-2, 1, 2));
        genBlue(get(-2, 1, 1));
        genBlue(get(-2, 1, 0));
        genBlue(get(-2, 1, -1));
        genBlue(get(-2, 1, -2));
        genBlue(get(1, 1, 2));
        genBlue(get(0, 1, 2));
        genBlue(get(-1, 1, 2));
        genBlue(get(1, 1, -2));
        genBlue(get(0, 1, -2));
        genBlue(get(-1, 1, -2));

        genBlack(get(0, 1, 0));
        genBlack(get(0, 1, 1));
        genBlack(get(1, 1, 0));
        genBlack(get(0, 1, -1));
        genBlack(get(-1, 1, 0));
        genBlack(get(1, 1, 1));
        genBlack(get(-1, 1, -1));
        genBlack(get(1, 1, -1));
        genBlack(get(-1, 1, 1));

        genLadder(get(-3, 1, 0));
        genLadder(get(-3, 0, 0));

        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                clearBlocks();
            }
        }, durationInTicks);
    }

    private void genBarr(Block block) {
        setToRestore(block, Material.FENCE, (byte) 0);
    }

    private void genBlue(Block block) {
        setToRestore(block, Material.WOOL, (byte) 11);
    }

    private void genBlack(Block block) {
        setToRestore(block, Material.WOOL, (byte) 15);
    }

    private void genLadder(Block block) {
        setToRestore(block, Material.LADDER, (byte) 4);
    }

    private void setToRestore(Block block, Material material, byte data) {
        MaterialData materialData = new MaterialData(material, data);
        trampoline.put(block, materialData);
        block.setType(material);
        block.setData(data);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (cuboid != null && running && cuboid.contains(event.getBlock()))
            event.setCancelled(true);
        if (cuboid != null && running && (event.getBlock().getLocation().equals(initialCenter.getBlock().getRelative(-3, 0, 0).getLocation())
                || event.getBlock().getLocation().equals(initialCenter.getBlock().getRelative(-3, 1, 0).getLocation())))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (cuboid != null && running && cuboid.contains(event.getBlock()))
            event.setCancelled(true);
        if (cuboid != null && running && (event.getBlock().getLocation().equals(initialCenter.getBlock().getRelative(-3, 0, 0).getLocation())
                || event.getBlock().getLocation().equals(initialCenter.getBlock().getRelative(-3, 1, 0).getLocation())))
            event.setCancelled(true);
    }

    private void clearBlocks() {
        if (initialCenter != null) {
            get(-3, 0, 0).setType(Material.AIR);
            get(-3, 1, 0).setType(Material.AIR);
        }
        if (trampoline != null) {
            for (Block block : trampoline.keySet())
                block.setType(Material.AIR);
            trampoline.clear();
        }
        running = false;
    }

    private Block get(int x, int y, int z) {
        return initialCenter.getBlock().getRelative(x, y, z);
    }
}
