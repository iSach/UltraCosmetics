package be.isach.ultracosmetics.tempchests.reward;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A boot reward.
 *
 * @author RadBuilder
 * @since 01-14-2017
 */
public class BootReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<SuitType> bootList;
	private UltraPlayer ultraPlayer;

	public BootReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.BOOTS, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}

	@Override
	public void clear() {
		bootList.clear();
	}
	
	@Override
	public boolean canEarn() {
		bootList = new ArrayList<>();
		for (SuitType type : SuitType.values())
			if (type.isEnabled()
			    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission(ArmorSlot.BOOTS))
			    && type.canBeFound())
				bootList.add(type);
		return bootList.size() > 0;
	}
	
	@Override
	public void give() {
		int i = new Random().nextInt(bootList.size());
		SuitType s = bootList.get(i);
		name = MessageManager.getMessage("Treasure-Chests-Loot.Suit").replace("%suit%", s.getName());
		itemStack = new MaterialData(s.getMaterial(), s.getData()).toItemStack(1);
		givePermission(s.getPermission());
		if (TreasureManager.getRewardFile().getBoolean("UcRewards.suit.firework-effect.enabled"))
			super.firework(TreasureManager.getRewardFile().getString("UcRewards.suit.firework-effect.color"));
		if (TreasureManager.getRewardFile().getBoolean("UcRewards.suit.chat-message.enabled"))
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.suit.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%suit%", s.getName()));
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
