package be.isach.ultracosmetics.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import be.isach.ultracosmetics.UltraCosmetics;

public class TreasureManager {
	private static YamlConfiguration rewardFile;
	private static YamlConfiguration designFile;
	private UltraCosmetics ultraCosmetics;
	
	public TreasureManager(UltraCosmetics ultraCosmetics) {
		this.ultraCosmetics = ultraCosmetics;
		rewardFile = YamlConfiguration.loadConfiguration(new File(this.ultraCosmetics.getDataFolder(), "rewards.yml"));
		designFile = YamlConfiguration.loadConfiguration(new File(this.ultraCosmetics.getDataFolder(), "designs.yml"));
	}
	
	public static YamlConfiguration getRewardFile() {
		return rewardFile;
	}
	
	public static YamlConfiguration getDesignFile() {
		return designFile;
	}
		
	public static Object getReward(String path) {
		return rewardFile.get(path);
	}
	
	public static Object getDesign(String path){
		return designFile.get(path);
	}
}
