package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A cosmetic type.
 *
 * @author iSach
 * @since 07-05-2016
 */
public abstract class CosmeticType<T extends Cosmetic> {
	
	private String configName;
	private String permission;
	private String descriptionAsString;
	private Class<? extends T> clazz;
	private Category category;
	
	public CosmeticType(Category category, String configName, String permission, String description, Class clazz) {
		this.configName = configName;
		this.permission = permission;
		this.descriptionAsString = description;
		this.clazz = clazz;
		this.category = category;
		
		if (SettingsManager.getConfig().get(getCategory().getConfigPath() + "." + configName + ".Description") == null) {
			setDescriptionAsString(description);
			SettingsManager.getConfig().set(getCategory().getConfigPath() + "." + configName + ".Description", getDescriptionColored(), "Description of this cosmetic.");
		} else
			setDescriptionAsString(fromList(SettingsManager.getConfig().getStringList(category.getConfigPath() + "." + configName + ".Description")));
	}
	
	public T equip(UltraPlayer player, UltraCosmetics ultraCosmetics) {
		T cosmetic = null;
		try {
			cosmetic = getClazz().getDeclaredConstructor(UltraPlayer.class, UltraCosmetics.class).newInstance(player, ultraCosmetics);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		cosmetic.equip();
		return cosmetic;
	}
	
	public boolean isEnabled() {
		return !(this == GadgetType.ETHEREALPEARL
		         && UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_11_R1) && SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + configName + ".Enabled");
	}
	
	public String getName() {
		return MessageManager.getMessage(category.getConfigPath() + "." + configName + ".name");
	}
	
	public String getConfigName() {
		return configName;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public String getDescriptionAsString() {
		return descriptionAsString;
	}
	
	public Class<? extends T> getClazz() {
		return clazz;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setDescriptionAsString(String descriptionAsString) {
		this.descriptionAsString = descriptionAsString;
	}
	
	/**
	 * Transforms the description from a String to a list.
	 * Without colors.
	 *
	 * @return The description as a list.
	 */
	public List<String> getDescription() {
		List<String> desc = new ArrayList<>();
		for (String string : getDescriptionAsString().split("\n"))
			desc.add(ChatColor.translateAlternateColorCodes('&', string));
		return desc;
	}
	
	/**
	 * Transforms the description from a String to a list.
	 * With colors.
	 *
	 * @return The description as a list.
	 */
	public List<String> getDescriptionColored() {
		return Arrays.asList(getDescriptionAsString().split("\n"));
	}
	
	/**
	 * Check if the SuitType should show a description.
	 *
	 * @return {@code true} if it should show a description, otherwise {@code false}.
	 */
	public boolean showsDescription() {
		return SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + getConfigName() + ".Show-Description");
	}
	
	/**
	 * Check if the SuitType can be found in Treasure Chests.
	 *
	 * @return {@code true} if it can be found in treasure chests, otherwise {@code false}.
	 */
	public boolean canBeFound() {
		return SettingsManager.getConfig().getBoolean(category.getConfigPath() + "." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
	}
	
	/**
	 * Get the description as a String from list.
	 *
	 * @param description The Description as a list.
	 * @return The description as a String.
	 */
	private String fromList(List<String> description) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < description.size(); i++)
			stringBuilder.append(description.get(i)).append(i < description.size() - 1 ? "\n" : "");
		return stringBuilder.toString();
	}
	
	/**
	 * Override toString method to show Cosmetic name.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return getConfigName().toUpperCase();
	}
}
