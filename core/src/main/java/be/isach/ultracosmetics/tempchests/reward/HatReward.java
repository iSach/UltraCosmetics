package be.isach.ultracosmetics.tempchests.reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.cosmetics.type.HatType;
import be.isach.ultracosmetics.player.UltraPlayer;


/**
 * A hat reward.
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class HatReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<HatType> hatList;
	private UltraPlayer ultraPlayer;

	public HatReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.HAT, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}

	@Override
	public void clear() {
		hatList.clear();
	}
	
	@Override
	public boolean canEarn() {
		hatList = new ArrayList<HatType>();
        for (HatType type : HatType.values())
            if (type.isEnabled()
                    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
                    && type.canBeFound())
                hatList.add(type);
        return hatList.size() > 0;
	}
	
	@Override
	public void give() {
		int i = new Random().nextInt(hatList.size());
        HatType h = hatList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Hat").replace("%hat%", h.getName());
        itemStack = new MaterialData(h.getMaterial(), h.getData()).toItemStack(1);
        givePermission(h.getPermission());
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.hat.firework-effect.enabled"))
        	super.firework(TreasureManager.getRewardFile().getString("UcRewards.hat.firework-effect.color"));
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.hat.chat-message.enabled"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.hat.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%hat%", h.getName()));
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
