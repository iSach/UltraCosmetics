package be.isach.ultracosmetics.treasurechests.reward;

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
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;


/**
 * A gadget reward.
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class GadgetReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private List<GadgetType> gadgetList;
	private UltraPlayer ultraPlayer;

	public GadgetReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.GADGET, ultraCosmetics);
		this.ultraPlayer = ultraPlayer;
	}
	
	@Override
	public void clear() {
		gadgetList.clear();
	}

	@Override
	public boolean canEarn() {
		gadgetList = new ArrayList<GadgetType>();
        for (GadgetType type : GadgetType.values())
            if (type.isEnabled()
                    && !ultraPlayer.getBukkitPlayer().hasPermission(type.getPermission())
                    && type.canBeFound())
                gadgetList.add(type);
        return gadgetList.size() > 0;
	}
	
	@Override
	public void give() {
		int i = new Random().nextInt(gadgetList.size());
        GadgetType g = gadgetList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Gadget").replace("%gadget%", g.getName());
        itemStack = new MaterialData(g.getMaterial(), g.getData()).toItemStack(1);
        givePermission(g.getPermission());
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.gadget.firework-effect.enabled"))
        	super.firework(TreasureManager.getRewardFile().getString("UcRewards.gadget.firework-effect.color"));
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.gadget.chat-message.enabled"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.gadget.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%gadget%", g.getName()));
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
