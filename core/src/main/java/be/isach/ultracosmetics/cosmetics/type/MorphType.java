package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.morphs.*;
import be.isach.ultracosmetics.util.ServerVersion;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Morph types.
 *
 * @author iSach
 * @since 12-19-2015
 */
public class MorphType extends CosmeticMatType<Morph> {
	
	private final static List<MorphType> ENABLED = new ArrayList<>();
	private final static List<MorphType> VALUES = new ArrayList<>();
	
	public static List<MorphType> enabled() {
		return ENABLED;
	}
	
	public static List<MorphType> values() {
		return VALUES;
	}
	
	public static MorphType valueOf(String s) {
		for (MorphType morphType : VALUES) {
			if (morphType.getConfigName().equalsIgnoreCase(s)) return morphType;
		}
		return null;
	}
	
	public static void checkEnabled() {
		ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
	}

	/**
	 * Disguise Type of the morph.
	 */
	private DisguiseType disguiseType;
	
	private MorphType(String permission, String configName, Material material, byte data, DisguiseType disguiseType, String defaultDesc, Class<? extends Morph> clazz, ServerVersion baseVersion) {
		super(Category.MORPHS, configName, permission, defaultDesc, material, data, clazz, baseVersion);
		this.disguiseType = disguiseType;
		
		VALUES.add(this);
	}
	
	/**
	 * Get the skill message.
	 *
	 * @return The skill message of the morph.
	 */
	public String getSkill() {
		return MessageManager.getMessage("Morphs." + getConfigName() + ".skill");
	}
	
	/**
	 * Get the morph Disguise Type.
	 *
	 * @return
	 */
	public DisguiseType getDisguiseType() {
		return disguiseType;
	}

	public static void register() {
		ServerVersion serverVersion = UltraCosmeticsData.get().getServerVersion();

		new MorphType("ultracosmetics.morphs.bat", "Bat", Material.COAL, (byte) 0, DisguiseType.BAT, "&7&oBecome a bat!", MorphBat.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.blaze", "Blaze", Material.BLAZE_POWDER, (byte) 0, DisguiseType.BLAZE, "&7&oIt might be hot on there..", MorphBlaze.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.chicken", "Chicken", Material.EGG, (byte) 0, DisguiseType.CHICKEN, "&7&oBecome a chicken!", MorphChicken.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.pig", "Pig", Material.PORK, (byte) 0, DisguiseType.PIG, "&7&oMust hold your carrot!", MorphPig.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.enderman", "Enderman", Material.ENDER_PEARL, (byte) 0, DisguiseType.ENDERMAN, "&7&oI go from there to\n" + "&7&othere, then you lost me..", MorphEnderman.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.slime", "Slime", Material.SLIME_BALL, (byte) 0, DisguiseType.SLIME, "&7&oSplat splat!", MorphSlime.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.creeper", "Creeper", Material.SULPHUR, (byte) 0, DisguiseType.CREEPER, "&7&oHey What'sssssss up?", MorphCreeper.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.witherskeleton", "WitherSkeleton", Material.SKULL_ITEM, (byte) 1, DisguiseType.WITHER_SKELETON, "&7&oJust a regular skeleton..\n&7&obut from the Nether!", MorphWitherSkeleton.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.snowman", "Snowman", Material.SNOW_BALL, (byte) 0, DisguiseType.SNOWMAN, "&7&oBecome Olaf!", MorphSnowman.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.elderguardian", "ElderGuardian", Material.PRISMARINE_CRYSTALS, (byte) 0, DisguiseType.ELDER_GUARDIAN, "&7&oBecome an Elder Guardian!!", UltraCosmeticsData.get().getVersionManager().getMorphs().getElderGuardianClass(), ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.cow", "Cow", Material.MILK_BUCKET, (byte) 0, DisguiseType.COW, "&7&oMoooo!", MorphCow.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.sheep", "Sheep", Material.WOOL, (byte) 0, DisguiseType.SHEEP, "&7&oBaaaa, baa", MorphSheep.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.mooshroom", "Mooshroom", Material.RED_MUSHROOM, (byte) 0, DisguiseType.MUSHROOM_COW, "&7&oMoooo!", MorphMooshroom.class, ServerVersion.v1_8_R1);
		new MorphType("ultracosmetics.morphs.villager", "Villager", Material.EMERALD, (byte) 0, DisguiseType.VILLAGER, "&7&oHmmmmmmmmm", MorphVillager.class, ServerVersion.v1_8_R1);

		if (serverVersion.compareTo(ServerVersion.v1_9_R1) >= 0) {
			new MorphType("ultracosmetics.morphs.witch", "Witch", Material.POISONOUS_POTATO, (byte) 0, DisguiseType.WITCH, "&7&oMuahahahahaha", MorphWitch.class, ServerVersion.v1_9_R1);
		}
		if (serverVersion.compareTo(ServerVersion.v1_10_R1) >= 0) {
			new MorphType("ultracosmetics.morphs.polarbear", "PolarBear", Material.SNOW_BLOCK, (byte) 0, DisguiseType.POLAR_BEAR, "&7&oI prefer cold areas", MorphPolarBear.class, ServerVersion.v1_10_R1);
		}
		if (serverVersion.compareTo(ServerVersion.v1_11_R1) >= 0) {
			new MorphType("ultracosmetics.morphs.llama", "Llama", Material.WOOL, (byte) 14, DisguiseType.LLAMA, "&7&oNeed me to carry anything?", MorphLlama.class, ServerVersion.v1_11_R1);
		}
		if (serverVersion.compareTo(ServerVersion.v1_12_R1) >= 0) {
			new MorphType("ultracosmetics.morphs.parrot", "Parrot", Material.COOKIE, (byte) 0, DisguiseType.PARROT, "&7&oPolly want a cracker?", MorphParrot.class, ServerVersion.v1_12_R1);
		}
	}
}
