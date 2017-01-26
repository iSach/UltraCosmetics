package be.isach.ultracosmetics.treasurechests.reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;


/**
 * An ammo reward.
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class AmmoReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<GadgetType> ammoList;
	private UltraPlayer ultraPlayer;

	public AmmoReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.AMMO, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}
	
	@Override
	public void clear() {
		ammoList.clear();
	}
	
	@Override
	public boolean canEarn() {
		ammoList = new ArrayList<GadgetType>();
		 if (UltraCosmeticsData.get().isAmmoEnabled())
	            for (GadgetType type : GadgetType.values())
	                if (type.isEnabled()
	                        && ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
	                        && type.requiresAmmo()
	                        && type.canBeFound())
	                    ammoList.add(type);
		 return ammoList.size() > 0;
	}

	@Override
	public void give() {
		int i = new Random().nextInt(ammoList.size());
        GadgetType g = ammoList.get(i);
        int ammo = MathUtils.randomRangeInt(TreasureManager.getRewardFile().getInt("UcRewards.gadget-ammo.min"), TreasureManager.getRewardFile().getInt("UcRewards.gadget-ammo.max"));
        name = MessageManager.getMessage("Treasure-Chests-Loot.Ammo").replace("%name%", g.getName()).replace("%ammo%", ammo + "");
        ultraPlayer.addAmmo(g.toString().toLowerCase(), ammo);
        itemStack = new MaterialData(g.getMaterial(), g.getData()).toItemStack(1);
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.gadget-ammo.firework-effect.enabled"))
        	super.firework(TreasureManager.getRewardFile().getString("UcRewards.gadget-ammo.firework-effect.color"));
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.gadget-ammo.chat-message.enabled"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.gadget-ammo.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%ammo%", ammo + "").replace("%gadget%", g.getName()));
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
