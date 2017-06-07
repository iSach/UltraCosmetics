package be.isach.ultracosmetics.tempchests.reward;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A "nothing" reward.
 *
 * @author RadBuilder
 * @since 01-16-2017
 */
public class NothingReward extends Reward {
	private String name;
	private ItemStack itemStack;
	
	public NothingReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.NOTHING, ultraCosmetics);
	}
	
	@Override
	public void clear() {
	}

	@Override
	public boolean canEarn() {
		return true;
	}
	
	@Override
	public void give() {
		name = MessageManager.getMessage("Treasure-Chests-Loot.Nothing");
		itemStack = new ItemStack(Material.BARRIER);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}
}
