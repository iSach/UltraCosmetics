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
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import be.isach.ultracosmetics.player.UltraPlayer;


/**
 * A gadget reward.
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class EmoteReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<EmoteType> emoteList;
	private UltraPlayer ultraPlayer;

	public EmoteReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.EMOTE, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}
	
	@Override
	public void clear() {
		emoteList.clear();
	}
	
	@Override
	public boolean canEarn() {
		emoteList = new ArrayList<>();
        for (EmoteType type : EmoteType.values())
            if (type.isEnabled()
                    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
                    && type.canBeFound())
                emoteList.add(type);
        return emoteList.size() > 0;
	}

	@Override
	public void give() {
		int i = new Random().nextInt(emoteList.size());
        EmoteType e = emoteList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Emote").replace("%emote%", e.getName());
        itemStack = new MaterialData(e.getMaterial(), e.getData()).toItemStack(1);
        givePermission(e.getPermission());
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.emote.firework-effect.enabled"))
        	super.firework(TreasureManager.getRewardFile().getString("UcRewards.emote.firework-effect.color"));
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.emote.chat-message.enabled"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.emote.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%emote%", e.getName()));
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
