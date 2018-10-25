package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.pets.*;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.GetForVersion;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pet types.
 *
 * @author iSach
 * @since 12-20-2015
 */
public final class PetType extends CosmeticMatType<Pet> {
	
	private final static List<PetType> ENABLED = new ArrayList<>();
	private final static List<PetType> VALUES = new ArrayList<>();
	
	public static List<PetType> enabled() {
		return ENABLED;
	}
	
	public static List<PetType> values() {
		return VALUES;
	}
	
	public static PetType valueOf(String s) {
		for (PetType petType : VALUES) {
			if (petType.getConfigName().equalsIgnoreCase(s)) return petType;
		}
		return null;
	}
	
	public static PetType getByName(String s) {
		try {
			return VALUES.stream().filter(value -> value.getConfigName().equalsIgnoreCase(s)).findFirst().get();
		} catch (Exception exc) {
			return null;
		}
	}
	
	public static void checkEnabled() {
		ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
	}

	private EntityType entityType;
	
	private PetType(String permission, String configName, Material material, byte data, String defaultDesc, EntityType entityType, Class<? extends Pet> clazz, ServerVersion baseVersion) {
		super(Category.PETS, configName, permission, defaultDesc, material, data, clazz, baseVersion);
		
		this.entityType = entityType;
		VALUES.add(this);
	}
	
	public EntityType getEntityType() {
		return this.entityType;
	}
	
	public String getEntityName(Player player) {
		return MessageManager.getMessage("Pets." + getConfigName() + ".entity-displayname").replace("%playername%", player.getName());
	}
	
	@Override
	public String getName() {
		return MessageManager.getMessage("Pets." + getConfigName() + ".menu-name");
	}

	public static void register() {
		ServerVersion serverVersion = UltraCosmeticsData.get().getServerVersion();

		new PetType("ultracosmetics.pets.piggy", "Piggy", BlockUtils.getOldMaterial("PORK"), (byte) 0, "&7&oOink! Oink!", EntityType.PIG, PetPiggy.class, ServerVersion.v1_8_R1);
		
		new PetType("ultracosmetics.pets.easterbunny", "EasterBunny", BlockUtils.getOldMaterial("CARROT_ITEM"), (byte) 0, "&7&oIs it Easter yet?", EntityType.RABBIT, PetEasterBunny.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.cow", "Cow", Material.MILK_BUCKET, (byte) 0, "&7&oMoooo!", EntityType.COW, PetCow.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.mooshroom", "Mooshroom", Material.RED_MUSHROOM, (byte) 0, "&7&oMoooo!", EntityType.MUSHROOM_COW, PetMooshroom.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.kitty", "Kitty", BlockUtils.getOldMaterial("RAW_FISH"), (byte) 0, "&7&oMeoooow", EntityType.OCELOT, PetKitty.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.dog", "Dog", Material.BONE, (byte) 0, "&7&oWoof!", EntityType.WOLF, PetDog.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.chick", "Chick", Material.EGG, (byte) 0, "&7&oBwaaaaaaak!!", EntityType.CHICKEN, PetChick.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.pumpling", "Pumpling", Material.PUMPKIN, (byte) 0, "&7&oJust a little floating pumpkin", EntityType.ZOMBIE, UltraCosmeticsData.get().getVersionManager().getPets().getPumplingClass(), ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.christmaself", "ChristmasElf", Material.BEACON, (byte) 0, "&7&oI can make presents for you!", EntityType.VILLAGER, PetChristmasElf.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.irongolem", "IronGolem", Material.IRON_INGOT, (byte) 0, "&7&oI like flowers", EntityType.IRON_GOLEM, PetIronGolem.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.snowman", "Snowman", BlockUtils.getOldMaterial("SNOW_BALL"), (byte) 0, "&7&oPew pew pew", EntityType.SNOWMAN, PetSnowman.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.villager", "Villager", Material.EMERALD, (byte) 0, "&7&oHmmmmmmmmm", EntityType.VILLAGER, PetVillager.class, ServerVersion.v1_8_R1);
		new PetType("ultracosmetics.pets.bat", "Bat", Material.COAL, (byte) 0, "&7&oI prefer dark areas", EntityType.BAT, PetBat.class, ServerVersion.v1_8_R1);

		if (serverVersion.compareTo(ServerVersion.v1_10_R1) >= 0) {
			new PetType("ultracosmetics.pets.polarbear", "PolarBear", Material.SNOW_BLOCK, (byte) 0, "&7&oI prefer cold areas", GetForVersion.entityType("POLAR_BEAR"), PetPolarBear.class, ServerVersion.v1_10_R1);
		}
		if (serverVersion.compareTo(ServerVersion.v1_11_R1) >= 0) {
			if (VersionManager.IS_VERSION_1_13) {
				new PetType("ultracosmetics.pets.llama", "Llama", BlockUtils.getBlockByColor("WOOL", (byte) 14), (byte) 14, "&7&oNeed me to carry anything?", GetForVersion.entityType("LLAMA"), PetLlama.class, ServerVersion.v1_11_R1);
			} else {
				new PetType("ultracosmetics.pets.llama", "Llama", Material.valueOf("WOOL"), (byte) 14, "&7&oNeed me to carry anything?", GetForVersion.entityType("LLAMA"), PetLlama.class, ServerVersion.v1_11_R1);
			}
		}
		if (serverVersion.compareTo(ServerVersion.v1_12_R1) >= 0) {
			new PetType("ultracosmetics.pets.parrot", "Parrot", Material.COOKIE, (byte) 0, "&7&oPolly want a cracker?", GetForVersion.entityType("PARROT"), PetParrot.class, ServerVersion.v1_12_R1);
		}
		
		if (VersionManager.IS_VERSION_1_13) {
			new PetType("ultracosmetics.pets.sheep", "Sheep", BlockUtils.getBlockByColor("WOOL", (byte) 0), (byte) 0, "&7&oBaaaa, baa", EntityType.SHEEP, PetSheep.class, ServerVersion.v1_13_R1);
			new PetType("ultracosmetics.pets.wither", "Wither", Material.valueOf("WITHER_SKELETON_SKULL"), (byte) 0, "&7&oWatch out for me..", EntityType.WITHER, PetWither.class, ServerVersion.v1_13_R1);
		} else {
			new PetType("ultracosmetics.pets.sheep", "Sheep", Material.valueOf("WOOL"), (byte) 0, "&7&oBaaaa, baa", EntityType.SHEEP, PetSheep.class, ServerVersion.v1_8_R1);
			new PetType("ultracosmetics.pets.wither", "Wither", Material.valueOf("SKULL_ITEM"), (byte) 1, "&7&oWatch out for me..", EntityType.WITHER, PetWither.class, ServerVersion.v1_8_R1);
		}
	}
}
