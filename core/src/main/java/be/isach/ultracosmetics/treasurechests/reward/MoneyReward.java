package be.isach.ultracosmetics.treasurechests.reward;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;


/**
 * A money reward.
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class MoneyReward extends Reward {
	private String name;
	private ItemStack itemStack;
	private UltraPlayer ultraPlayer;
	private UltraCosmetics ultraCosmetics;

	public MoneyReward(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		super(ultraPlayer, RewardType.MONEY, ultraCosmetics);
		this.ultraCosmetics = ultraCosmetics;
		this.ultraPlayer = ultraPlayer;
	}
	
	@Override
	public void clear() {
	}
	
	@Override
	public boolean canEarn() {
		return ultraCosmetics.getEconomy() != null;
	}

	@Override
	public void give() {
		if (ultraCosmetics.getEconomy() == null) {
			name = MessageManager.getMessage("Treasure-Chests-Loot.Nothing");
			itemStack = new ItemStack(Material.BARRIER);
            return;
        }
        int money = MathUtils.randomRangeInt(20, (int) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Max"));
        name = MessageManager.getMessage("Treasure-Chests-Loot.Money").replace("%money%", money + "");
        ultraCosmetics.getEconomy().depositPlayer(ultraPlayer.getBukkitPlayer(), money);
        itemStack = new ItemStack(Material.DOUBLE_PLANT);
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.money.firework-effect.enabled"))
        	super.firework(TreasureManager.getRewardFile().getString("UcRewards.money.firework-effect.color"));
        if (TreasureManager.getRewardFile().getBoolean("UcRewards.money.chat-message.enabled"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TreasureManager.getRewardFile().getString("UcRewards.money.chat-message.message")).replace("%name%", ultraPlayer.getBukkitPlayer().getName()).replace("%money%", money + ""));
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
