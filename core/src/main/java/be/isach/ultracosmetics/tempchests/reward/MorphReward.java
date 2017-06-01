package be.isach.ultracosmetics.tempchests.reward;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A morph reward.
 *
 * @author RadBuilder
 * @since 01-14-2017
 */
public class MorphReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<MorphType> morphList;
	private UltraPlayer ultraPlayer;

	public MorphReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.MORPH, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}
	
	@Override
	public void clear() {
		morphList.clear();
	}
	
	@Override
	public boolean canEarn() {
		morphList = new ArrayList<>();
		for (MorphType type : MorphType.values())
			if (type.isEnabled()
			    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
			    && type.canBeFound())
				morphList.add(type);
		return morphList.size() > 0;
	}

	@Override
	public void give() {
		int i = new Random().nextInt(morphList.size());
		MorphType m = morphList.get(i);
		name = MessageManager.getMessage("Treasure-Chests-Loot.Morph").replace("%morph%", m.getName());
		itemStack = new MaterialData(m.getMaterial(), m.getData()).toItemStack(1);
		givePermission(m.getPermission());
		if (TreasureManager.getRewardFile().getBoolean("UcRewards.morph.firework-effect.enabled"))
			super.firework(TreasureManager.getRewardFile().getString("UcRewards.morph.firework-effect.color"));
		if (TreasureManager.getRewardFile().getBoolean("UcRewards.morph.chat-message.enabled"))
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.morph.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%morph%", m.getName()));
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
