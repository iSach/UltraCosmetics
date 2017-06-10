package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an instance of a trampoline gadget summoned by a player.
 *
 * @author iSach
 * @since 12-19-2015
 */
public class GadgetTrampoline extends Gadget {
	private Map<Block, MaterialData> trampoline = new HashMap<>();
	private Cuboid cuboid;
	private Location center;
	private boolean running;
	
	public GadgetTrampoline(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.TRAMPOLINE, ultraCosmetics);
	}
	
	@Override
	void onRightClick() {
		Location loc1 = getPlayer().getLocation().add(-2, 0, -2);
		Location loc2 = getPlayer().getLocation().add(2, 15, 2);
		
		clearBlocks();
		
		center = getPlayer().getLocation();
		cuboid = new Cuboid(loc1, loc2);
		
		generateStructure();
		
		getPlayer().teleport(getPlayer().getLocation().add(0, 4, 0));
		
		running = true;
	}
	
	@Override
	protected boolean checkRequirements(PlayerInteractEvent event) {
		Location loc1 = getPlayer().getLocation().add(2, 15, 2);
		Location loc2 = getPlayer().getLocation().clone().add(-3, 0, -2);
		Block block = loc1.getBlock().getRelative(3, 0, 0);
		Block block2 = loc1.getBlock().getRelative(3, 1, 0);
		Cuboid checkCuboid = new Cuboid(loc1, loc2);
		
		if (!checkCuboid.isEmpty() || block.getType() != Material.AIR || block2.getType() != Material.AIR) {
			getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-Enough-Space"));
			return false;
		}
		return true;
	}
	
	@Override
	void onLeftClick() {
	}
	
	@Override
	public void onUpdate() {
		if (running && cuboid != null) {
			for (Entity entity : EntityUtils.getEntitiesInRadius(center, 4d)) {
				Block b = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
				if (b.getType() == Material.WOOL && cuboid.contains(b))
					MathUtils.applyVelocity(entity, new Vector(0, 3, 0));
			}
		}
	}
	
	@Override
	public void onClear() {
		clearBlocks();
		trampoline = null;
		cuboid = null;
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
		
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), this::clearBlocks, 240);
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
	
	@SuppressWarnings("deprecation")
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
		if (cuboid != null && running && (event.getBlock().getLocation().equals(center.getBlock().getRelative(-3, 0, 0).getLocation())
		                                  || event.getBlock().getLocation().equals(center.getBlock().getRelative(-3, 1, 0).getLocation())))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (cuboid != null && running && cuboid.contains(event.getBlock()))
			event.setCancelled(true);
		if (cuboid != null && running && (event.getBlock().getLocation().equals(center.getBlock().getRelative(-3, 0, 0).getLocation())
		                                  || event.getBlock().getLocation().equals(center.getBlock().getRelative(-3, 1, 0).getLocation())))
			event.setCancelled(true);
	}
	
	private void clearBlocks() {
		if (center != null) {
			get(-3, 0, 0).setType(Material.AIR);
			get(-3, 1, 0).setType(Material.AIR);
		}
		if (trampoline != null) {
			for (Block block : trampoline.keySet())
				block.setType(Material.AIR);
			trampoline.clear();
		}
		cuboid = null;
		running = false;
	}
	
	private Block get(int x, int y, int z) {
		return center.getBlock().getRelative(x, y, z);
	}
}
