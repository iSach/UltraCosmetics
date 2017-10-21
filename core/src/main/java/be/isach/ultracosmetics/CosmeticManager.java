package be.isach.ultracosmetics;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cosmetic manager.
 *
 * @author iSach
 * @since 08-09-2016
 */
public class CosmeticManager {
	
	private UltraCosmetics ultraCosmetics;
	
	public CosmeticManager(UltraCosmetics ultraCosmetics) {
		this.ultraCosmetics = ultraCosmetics;
	}
	
	/**
	 * Setup default Cosmetics ultraCosmetics.getConfig().
	 */
	public void setupCosmeticsConfigs() {
		
		for (Category category : Category.values()) {
			ultraCosmetics.getConfig().addDefault("Categories-Enabled." + category.getConfigPath(), true);
			ultraCosmetics.getConfig().addDefault("Categories." + category.getConfigPath() + ".Go-Back-Arrow", true, "Want Go back To Menu Item in that menu?");
		}
		
		ultraCosmetics.getConfig().addDefault("TreasureChests.Loots.Emotes.Enabled", true);
		ultraCosmetics.getConfig().addDefault("TreasureChests.Loots.Emotes.Chance", 5);
		ultraCosmetics.getConfig().addDefault("TreasureChests.Loots.Emotes.Message.enabled", true);
		ultraCosmetics.getConfig().addDefault("TreasureChests.Loots.Emotes.Message.message", "%prefix% &6&l%name% found rare %emote%");
		ultraCosmetics.getConfig().addDefault("Ammo-System-For-Gadgets.Show-Ammo-In-Menu-As-Item-Amount", true, "Do you want that in the gadgets menu", "each gadget item has an amount", "corresponding to your ammo.");
		
		// CALL STATIC BLOCK.
		GadgetType.register();
		MountType.register();
		ParticleEffectType.register();
		PetType.register();
		HatType.register();
		SuitType.register();
		EmoteType.ANGRY.getConfigName();
		if (Category.MORPHS.isEnabled()) {
			MorphType.register();
			// MorphType.valueOf("bat").getConfigName();
		}
		
		for (GadgetType gadgetType : GadgetType.values()) {
			ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Affect-Players", true, "Should it affect players? (Velocity, etc.)");
			ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Enabled", true, "if true, the gadget will be enabled.");
			ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Show-Description", true, "if true, the description of gadget will be showed.");
			ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
			if (gadgetType == GadgetType.valueOf("paintballgun")) {
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Block-Type", "STAINED_CLAY", "With what block will it paint?");
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Particle.Enabled", false, "Should it display particles?");
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Particle.Effect", "FIREWORKS_SPARK", "what particles? (List: http://pastebin.com/CVKkufck)");
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Radius", 2, "The radius of painting.");
				List<String> blackListedBlocks = new ArrayList<>();
				blackListedBlocks.add("REDSTONE_BLOCK");
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".BlackList", blackListedBlocks, "A list of the BLOCKS that", "can't be painted.");
			}
			if (UltraCosmeticsData.get().isAmmoEnabled()) {
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Enabled", true, "You want this gadget to need ammo?");
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Price", 500, "What price for the ammo?");
				ultraCosmetics.getConfig().addDefault("Gadgets." + gadgetType.getConfigName() + ".Ammo.Result-Amount", 20, "And how much ammo is given", "when bought?");
			}
		}
		
		for (MountType mountType : MountType.values()) {
			ultraCosmetics.getConfig().addDefault("Mounts." + mountType.getConfigName() + ".Enabled", true, "if true, the mount will be enabled.");
			ultraCosmetics.getConfig().addDefault("Mounts." + mountType.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
			ultraCosmetics.getConfig().addDefault("Mounts." + mountType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
		}
		
		for (ParticleEffectType particleEffect : ParticleEffectType.values()) {
			ultraCosmetics.getConfig().addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Enabled", true, "if true, the effect will be enabled.");
			ultraCosmetics.getConfig().addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
			ultraCosmetics.getConfig().addDefault("Particle-Effects." + particleEffect.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
		}
		
		for (PetType pet : PetType.values()) {
			ultraCosmetics.getConfig().addDefault("Pets." + pet.getConfigName() + ".Enabled", true, "if true, the pet will be enabled.");
			ultraCosmetics.getConfig().addDefault("Pets." + pet.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
			ultraCosmetics.getConfig().addDefault("Pets." + pet.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
		}
		if (Category.MORPHS.isEnabled())
			for (MorphType morphType : MorphType.values()) {
				ultraCosmetics.getConfig().addDefault("Morphs." + morphType.getConfigName() + ".Enabled", true, "if true, the morph will be enabled.");
				ultraCosmetics.getConfig().addDefault("Morphs." + morphType.getConfigName() + ".Show-Description", true, "if true, the description of this morph will be showed.");
				ultraCosmetics.getConfig().addDefault("Morphs." + morphType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
			}
		for (HatType hat : HatType.values()) {
			ultraCosmetics.getConfig().addDefault("Hats." + hat.getConfigName() + ".Enabled", true, "if true, the hat will be enabled.");
			ultraCosmetics.getConfig().addDefault("Hats." + hat.getConfigName() + ".Show-Description", true, "if true, the description of this hat will be showed.");
			ultraCosmetics.getConfig().addDefault("Hats." + hat.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
		}
		for (SuitType suit : SuitType.values()) {
			ultraCosmetics.getConfig().addDefault("Suits." + suit.getConfigName() + ".Enabled", true, "if true, the suit will be enabled.");
			ultraCosmetics.getConfig().addDefault("Suits." + suit.getConfigName() + ".Show-Description", true, "if true, the description of this suit will be showed.");
			ultraCosmetics.getConfig().addDefault("Suits." + suit.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
		}
		
		for (EmoteType emoteType : EmoteType.values()) {
			ultraCosmetics.getConfig().addDefault("Emotes." + emoteType.getConfigName() + ".Enabled", true, "if true, the mount will be enabled.");
			ultraCosmetics.getConfig().addDefault("Emotes." + emoteType.getConfigName() + ".Show-Description", true, "if true, the description will be showed.");
			ultraCosmetics.getConfig().addDefault("Emotes." + emoteType.getConfigName() + ".Can-Be-Found-In-Treasure-Chests", true, "if true, it'll be possible to find", "it in treasure chests");
		}
		
		try {
			ultraCosmetics.getConfig().save(ultraCosmetics.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		GadgetType.checkEnabled();
		MountType.checkEnabled();
		ParticleEffectType.checkEnabled();
		PetType.checkEnabled();
		HatType.checkEnabled();
		SuitType.checkEnabled();
		EmoteType.checkEnabled();
		if (Category.MORPHS.isEnabled()) {
			MorphType.checkEnabled();
		}
		
		try {
			ultraCosmetics.getConfig().save(ultraCosmetics.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
