package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.treasurechests.reward.*;
import org.bukkit.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Treasure Chest Randomizer
 * 
 * @author RadBuilder
 * @since 01-14-2017
 * 
 * Work in progress.
 * TODO Clear method(?), custom reward support, make cleaner.
 */
public class TreasureRandomizer {
	
	private List<RewardType> chance = new ArrayList<RewardType>();
	private UltraPlayer owner;
	private UltraCosmetics ultraCosmetics;
	private List<Reward> rewards;
	private Reward reward;
	
	public TreasureRandomizer(UltraPlayer owner, Location location, UltraCosmetics ultraCosmetics) {
		calculateChances();
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
	}
	
	private void calculateChances() {
		for(Reward r : rewards) {
			if(r.canEarn()) {
				RewardType t = r.getType();
				int c = t.getChance();
				for(int i = 0; i < c; i++) {
					chance.add(t);
				}
			}
		}
	}

	public Reward getRandomThing() {
		Reward reward = null;
		if(chance.isEmpty()) {
			return new NothingReward(owner, ultraCosmetics);
		} else {
			List<RewardType> random = new ArrayList<RewardType>(chance);
			Collections.shuffle(random);
			switch(random.get(0)) {
				case AMMO:
					reward = rewards.get(0);
					break;
				case BOOTS:
					reward = rewards.get(1);
					break;
				case CHESTPLATE:
					reward = rewards.get(2);
					break;
				case EFFECT:
					reward = rewards.get(3);
					break;
				case EMOTE:
					reward = rewards.get(4);
					break;
				case GADGET:
					reward = rewards.get(5);
					break;
				case HAT:
					reward = rewards.get(6);
					break;
				case HELMET:
					reward = rewards.get(7);
					break;
				case LEGGINGS:
					reward = rewards.get(8);
					break;
				case MONEY:
					reward = rewards.get(9);
					break;
				case MORPH:
					reward = rewards.get(10);
					break;
				case MOUNT:
					reward = rewards.get(11);
					break;
				case PET:
					reward = rewards.get(12);
					break;
				case NOTHING:
					reward = new NothingReward(owner, ultraCosmetics);
					break;
			}
		}
		this.reward = reward;
		return reward;
	}
	
	public Reward getReward() {
		return reward;
	}
}
