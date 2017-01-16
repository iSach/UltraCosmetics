package be.isach.ultracosmetics.treasurechests.reward;

import be.isach.ultracosmetics.config.TreasureManager;

public enum RewardType {
	AMMO(TreasureManager.getRewardFile().getInt("UcRewards.gadget-ammo.chance")),
	MONEY(TreasureManager.getRewardFile().getInt("UcRewards.money.chance")),
	GADGET(TreasureManager.getRewardFile().getInt("UcRewards.gadget.chance")),
	MORPH(TreasureManager.getRewardFile().getInt("UcRewards.morph.chance")),
	PET(TreasureManager.getRewardFile().getInt("UcRewards.pet.chance")),
	EFFECT(TreasureManager.getRewardFile().getInt("UcRewards.effect.chance")),
	MOUNT(TreasureManager.getRewardFile().getInt("UcRewards.mount.chance")),
	HAT(TreasureManager.getRewardFile().getInt("UcRewards.hat.chance")),
	HELMET(TreasureManager.getRewardFile().getInt("UcRewards.suits.chance") / 4),
	CHESTPLATE(TreasureManager.getRewardFile().getInt("UcRewards.suits.chance") / 4),
	LEGGINGS(TreasureManager.getRewardFile().getInt("UcRewards.suits.chance") / 4),
	BOOTS(TreasureManager.getRewardFile().getInt("UcRewards.suits.chance") / 4),
	EMOTE(TreasureManager.getRewardFile().getInt("UcRewards.emote.chance")),
	NOTHING(0);
	
	private int chance;

	RewardType(int chance) {
		this.chance = chance;
	}
	
	public int getChance() {
		return chance;
	}
}
