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
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;


/**
 * A mount reward.
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class MountReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<MountType> mountList;
	private UltraPlayer ultraPlayer;

	public MountReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.MOUNT, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}
	
	@Override
	public void clear() {
		mountList.clear();
	}
	
	@Override
	public boolean canEarn() {
		mountList = new ArrayList<MountType>();
        for (MountType type : MountType.values())
            if (type.isEnabled()
                    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
                    && type.canBeFound())
                mountList.add(type);
        return mountList.size() > 0;
	}

	@Override
	public void give() {		 
		int i = new Random().nextInt(mountList.size());
        MountType m = mountList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Mount").replace("%mount%", m.getName());
        itemStack = new MaterialData(m.getMaterial(), m.getData()).toItemStack(1);
        givePermission(m.getPermission());
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.mount.firework-effect.enabled"))
        	super.firework(TreasureManager.getRewardFile().getString("UcRewards.mount.firework-effect.color"));
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.mount.chat-message.enabled"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.mount.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%mount%", m.getName()));
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
