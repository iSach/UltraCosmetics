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
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;


/**
 * A particle effect reward.
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class ParticleEffectReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<ParticleEffectType> particleList;
	private UltraPlayer ultraPlayer; 

	public ParticleEffectReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.EFFECT, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}
	
	@Override
	public void clear() {
		particleList.clear();
	}
	
	@Override
	public boolean canEarn() {
		particleList = new ArrayList<ParticleEffectType>();
        for (ParticleEffectType type : ParticleEffectType.values())
            if (type.isEnabled()
                    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
                    && type.canBeFound())
                particleList.add(type);
        return particleList.size() > 0;
	}

	@Override
	public void give() {
		int i = new Random().nextInt(particleList.size());
        ParticleEffectType p = particleList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Effect").replace("%effect%", p.getName());
        itemStack = new MaterialData(p.getMaterial(), p.getData()).toItemStack(1);
        givePermission(p.getPermission());
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.effect.firework-effect.enabled"))
        	super.firework(TreasureManager.getRewardFile().getString("UcRewards.effect.firework-effect.color"));
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.effect.chat-message.enabled"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.effect.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%effect%", p.getName()));
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
