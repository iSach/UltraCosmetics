package be.isach.ultracosmetics.tempchests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.TreasureManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.tempchests.reward.*;
import org.bukkit.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Treasure Chest Randomizer
 * 
 * @author RadBuilder
 * @since 01-14-2017
 */
public class TreasureRandomizer {
	private List<Reward> chance;
	private List<PermissionReward> customRewards;
	private UltraPlayer owner;
	private UltraCosmetics ultraCosmetics;
	private List<Reward> rewards;
	private Reward reward;
	
	public TreasureRandomizer(UltraPlayer owner, Location location, UltraCosmetics ultraCosmetics) {
		chance = new ArrayList<Reward>();
		customRewards = new ArrayList<PermissionReward>();
		for(String key : TreasureManager.getRewardFile().getConfigurationSection("CustomRewards").getKeys(false))
			if(TreasureManager.getRewardFile().getBoolean("CustomRewards." + key + ".enabled"))
				customRewards.add(new PermissionReward(key, owner, ultraCosmetics));
		
		this.owner = owner;
		this.ultraCosmetics = ultraCosmetics;
		
		this.rewards = new ArrayList<Reward>();
		rewards.add(new AmmoReward(owner, ultraCosmetics));
		rewards.add(new BootReward(owner, ultraCosmetics));
		rewards.add(new ChestplateReward(owner, ultraCosmetics));
		rewards.add(new EmoteReward(owner, ultraCosmetics));
		rewards.add(new GadgetReward(owner, ultraCosmetics));
		rewards.add(new HatReward(owner, ultraCosmetics));
		rewards.add(new HelmetReward(owner, ultraCosmetics));
		rewards.add(new LeggingReward(owner, ultraCosmetics));
		rewards.add(new MoneyReward(owner, ultraCosmetics));
		rewards.add(new MorphReward(owner, ultraCosmetics));
		rewards.add(new MountReward(owner, ultraCosmetics));
		rewards.add(new ParticleEffectReward(owner, ultraCosmetics));
		rewards.add(new PetReward(owner, ultraCosmetics));
		
		calculateChances();
	}
	
	private void calculateChances() {
		for(Reward r : rewards) {
			if(r.canEarn()) {
				int c = r.getType().getChance();
				for(int i = 0; i < c; i++) {
					chance.add(r);
				}
			}
		}
		if(!customRewards.isEmpty()) {
			for(PermissionReward r : customRewards) {
				if(r.canEarn()) {
					int c = r.getChance();
					for(int i = 0; i < c; i++) {
						chance.add(r);
					}
				}
			}
		}
	}

	public Reward getRandomThing() {
		Reward reward = null;
		if(chance.isEmpty()) {
			if(!rewards.get(9).canEarn())
				return new NothingReward(owner, ultraCosmetics);
			else
				reward = rewards.get(9);
		} else {
			List<Reward> random = new ArrayList<Reward>(chance);
			Collections.shuffle(random);
			reward = random.get(0);
		}
		this.reward = reward;
		return reward;
	}
	
	public Reward getReward() {
		return reward;
	}

	public void clear() {
		for(Reward r : rewards) {
			r.clear();
		}
		chance.clear();
	}

	public String getName() {
		return reward.getName();
	}
}
