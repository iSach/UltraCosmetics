package be.isach.ultracosmetics.tempchests.reward;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A pet reward.
 *
 * @author RadBuilder
 * @since 01-14-2017
 */
public class PetReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<PetType> petList;
	private UltraPlayer ultraPlayer;

	public PetReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.PET, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}

	@Override
	public void clear() {
		petList.clear();
	}
	
	@Override
	public boolean canEarn() {
		petList = new ArrayList<>();
		for (PetType type : PetType.values())
			if (type.isEnabled()
			    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
			    && type.canBeFound())
				petList.add(type);
		return petList.size() > 0;
	}
	
	@Override
	public void give() {
		int i = new Random().nextInt(petList.size());
		PetType p = petList.get(i);
		name = MessageManager.getMessage("Treasure-Chests-Loot.Pet").replace("%pet%", p.getName());
		itemStack = new MaterialData(p.getMaterial(), p.getData()).toItemStack(1);
		givePermission(p.getPermission());
		if (TreasureManager.getRewardFile().getBoolean("UcRewards.pet.firework-effect.enabled"))
			super.firework(TreasureManager.getRewardFile().getString("UcRewards.pet.firework-effect.color"));
		if (TreasureManager.getRewardFile().getBoolean("UcRewards.pet.chat-message.enabled"))
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.pet.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%pet%", p.getName()));
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
