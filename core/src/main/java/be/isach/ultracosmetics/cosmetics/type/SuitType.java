package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.*;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Suit types.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SuitType extends CosmeticMatType<Suit> {
	
	private final static List<SuitType> ENABLED = new ArrayList<>();
	private final static List<SuitType> VALUES = new ArrayList<>();
	
	public static List<SuitType> enabled() {
		return ENABLED;
	}
	
	public static List<SuitType> values() {
		return VALUES;
	}
	
	public static SuitType valueOf(String s) {
		for (SuitType suitType : VALUES) {
			if (suitType.getConfigName().equalsIgnoreCase(s)) return suitType;
		}
		return null;
	}
	
	public static void checkEnabled() {
		ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
	}
	
	/**
	 * The parts materials.
	 */
	private Material helmet, chestplate, leggings, boots;
	
	/**
	 * @param configName       The config path name.
	 * @param permissionSuffix The suffix of permission. (ultracosmetic.suits.{suffix}.{part})
	 * @param defaultDesc      The default description.
	 * @param h                The Helmet material.
	 * @param c                The Chestplate material.
	 * @param l                The Leggings material.
	 * @param b                The Boots material.
	 * @param clazz            The Suit Class
	 */
	SuitType(String configName, String permissionSuffix, String defaultDesc,
	         Material h, Material c, Material l, Material b, Class<? extends Suit> clazz, ServerVersion baseVersion) {
		super(Category.SUITS, configName, "ultracosmetics.suits." + permissionSuffix, defaultDesc, h, (byte) 0, clazz, baseVersion);
		this.boots = b;
		this.helmet = h;
		this.chestplate = c;
		this.leggings = l;
		
		VALUES.add(this);
	}
	
	/**
	 * Equips the Suit to a player.
	 *
	 * @param player    The receiver of the suit.
	 * @param armorSlot The Armor Slot.
	 * @return The suit Object equipped to the player.
	 */
	public Suit equip(UltraPlayer player, UltraCosmetics ultraCosmetics, ArmorSlot armorSlot) {
		Suit suit = null;
		try {
			suit = getClazz().getDeclaredConstructor(UltraPlayer.class, ArmorSlot.class, UltraCosmetics.class).newInstance(player, armorSlot, ultraCosmetics);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		suit.equip(armorSlot);
		return suit;
	}
	
	/**
	 * Get the Helmet material in menu
	 *
	 * @return The Helmet material in menu
	 */
	public Material getHelmet() {
		return helmet;
	}
	
	/**
	 * Get the Chestplate material in menu
	 *
	 * @return The Chestplate material in menu
	 */
	public Material getChestplate() {
		return chestplate;
	}
	
	/**
	 * Get the Leggings material in menu
	 *
	 * @return The Leggings material in menu
	 */
	public Material getLeggings() {
		return leggings;
	}
	
	/**
	 * Get the Boots material in menu
	 *
	 * @return The Boots material in menu
	 */
	public Material getBoots() {
		return boots;
	}
	
	public Material getMaterial(ArmorSlot armorSlot) {
		switch (armorSlot) {
			default:
				return getChestplate();
			case HELMET:
				return getHelmet();
			case LEGGINGS:
				return getLeggings();
			case BOOTS:
				return getBoots();
		}
	}
	
	@Override
	public String getName() {
		return getName(ArmorSlot.CHESTPLATE);
	}
	
	/**
	 * Get the SuitType's name in menu.
	 *
	 * @return The SuitType's name in menu.
	 */
	public String getName(ArmorSlot armorSlot) {
		return MessageManager.getMessage("Suits." + getConfigName() + "." + armorSlot.toString().toLowerCase() + "-name");
	}
	
	/**
	 * Get the permission required to toggle suit.
	 *
	 * @return The required permission to toggle the suittype.
	 */
	public String getPermission(ArmorSlot armorSlot) {
		return getPermission() + "." + armorSlot.toString().toLowerCase();
	}

	public static void register() {
		new SuitType("Rave", "rave", "&7&oSuch amazing colors!", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, SuitRave.class, ServerVersion.v1_8_R1);
		new SuitType("Astronaut", "astronaut", "&7&oHouston?", Material.GLASS, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, SuitAstronaut.class, ServerVersion.v1_8_R1);
		new SuitType("Diamond", "diamond", "&7&oShow your Mining skills\n&7&owith this amazing outfit!", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, SuitDiamond.class, ServerVersion.v1_8_R1);
		new SuitType("Santa", "santa", "&7&oBecome Santa and deliver presents!", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, SuitSanta.class, ServerVersion.v1_8_R1);
	}
}