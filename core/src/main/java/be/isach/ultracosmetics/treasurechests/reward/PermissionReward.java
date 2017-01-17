package be.isach.ultracosmetics.treasurechests.reward;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * A permission reward.
 * 
 * @author RadBuilder
 * @since 01-16-2017
 */
public class PermissionReward extends Reward {
	private int chance;
	private boolean chatMessage;
	private String message;
	private boolean permDisable;
	private String disablePerm;
	private boolean firework;
	private String fireworkColor;
	private List<String> rewardCommands;
	private boolean sound;
	private String soundEffect;
	private String name;
	private UltraPlayer ultraPlayer;
	
	public PermissionReward(String key, UltraPlayer player, UltraCosmetics ultraCosmetics) {
		super(player, RewardType.PERMISSION, ultraCosmetics);
		this.name = key;
		this.ultraPlayer = player;
		key = "CustomRewards." + key;
		chance = f().getInt(key + ".chance");
		chatMessage = f().getBoolean(key + ".chat-message.enabled");
		message = f().getString(key + ".chat-message.message");
		permDisable = f().getBoolean(key + ".disable-if-permission.enabled");
		disablePerm = f().getString(key + ".disable-if-permission.permission");
		firework = f().getBoolean(key + ".firework-effect.enabled");
		fireworkColor = f().getString(key + ".firework-effect.color");
		rewardCommands = f().getStringList(key + ".reward-commands");
		sound = f().getBoolean(key + ".sound.enabled");
		soundEffect = f().getString(key + ".sound.sound-effect");
	}
	
	public int getChance() {
		return chance;
	}
	
	public boolean displayChatMessage() {
		return chatMessage;
	}
	
	public String getChatMessage() {
		return message;
	}
	
	public boolean disableWithPerm() {
		return permDisable;
	}
	
	public String getDisablePerm() {
		return disablePerm;
	}
	
	public boolean launchFirework() {
		return firework;
	}
	
	public String getFireworkColor() {
		return fireworkColor;
	}
	
	public List<String> rewardCommands() {
		return rewardCommands;
	}
	
	public boolean makeSound() {
		return sound;
	}
	
	public String getSound() {
		return soundEffect;
	}
	
	private YamlConfiguration f() {
		return TreasureManager.getRewardFile();
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean canEarn() {
		if(permDisable)
			return !ultraPlayer.getBukkitPlayer().hasPermission(disablePerm);
		return true;
	}

	@Override
	public void give() {
		for(String rewardCommand : rewardCommands) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand.replace("{player-name}", ultraPlayer.getBukkitPlayer().getName()).replace("{player-uuid}", "" + ultraPlayer.getBukkitPlayer().getUniqueId()));
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(Material.NETHER_STAR);
	}
}
