package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.version.VersionManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class ItemFactory {
	public static ItemStack fillerItem;
	
	public static ItemStack create(Material material, byte data, String displayName, String... lore) {
		ItemStack itemStack = new MaterialData(material, data).toItemStack(1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		if (lore != null) {
			List<String> finalLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList();
			for (String s : lore)
				if (s != null)
					finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
			itemMeta.setLore(finalLore);
		}
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	public static ItemStack createColored(String oldMaterialName, byte data, String displayName, String... lore) {
		ItemStack itemStack;
		if (VersionManager.IS_VERSION_1_13) {
			itemStack = new ItemStack(BlockUtils.getBlockByColor(oldMaterialName, data), 1);
		} else {
			itemStack = new MaterialData(BlockUtils.getOldMaterial(oldMaterialName), data).toItemStack(1);
		}
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		if (lore != null) {
			List<String> finalLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList();
			for (String s : lore)
				if (s != null)
					finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
			itemMeta.setLore(finalLore);
		}
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	public static ItemStack create(Material material, String displayName, String... lore) {
		return create(material, (byte) 0x0, displayName, lore);
	}
	
	public static ItemStack rename(ItemStack itemstack, String displayName) {
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName(displayName);
		itemstack.setItemMeta(meta);
		return itemstack;
	}
	
	
	public static ItemStack rename(ItemStack itemstack, String displayName, String... lore) {
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName(displayName);
		if (lore != null) {
			List<String> finalLore = meta.hasLore() ? meta.getLore() : new ArrayList();
			for (String s : lore)
				if (s != null)
					finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
			meta.setLore(finalLore);
		}
		itemstack.setItemMeta(meta);
		return itemstack;
	}
	
	public static void fillInventory(Inventory inventory) {
		if (SettingsManager.getConfig().getBoolean("Fill-Blank-Slots-With-Item.Enabled")) {
			if (fillerItem == null) {
				ItemStack itemStack = getItemStackFromConfig("Fill-Blank-Slots-With-Item.Item");
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(ChatColor.GRAY + "");
				itemStack.setItemMeta(itemMeta);
				fillerItem = itemStack;
			}
			for (int i = 0; i < inventory.getSize(); i++) {
				if (inventory.getItem(i) == null
				    || inventory.getItem(i).getType() == Material.AIR)
					inventory.setItem(i, fillerItem);
			}
		}
	}
	
	public static Material fromId(int id) {
		for (Material m : EnumSet.allOf(Material.class)) {
			if (m.getId() == id) {
				return m;
			}
		}
		return Material.AIR;
	}
	
	public static ItemStack getItemStackFromConfig(String path) {
		String fromConfig = UltraCosmeticsData.get().getPlugin().getConfig().getString(path);
		int id = Integer.parseInt(fromConfig.split(":")[0]);
		byte data = fromConfig.split(":").length > 1 ? (byte) Integer.parseInt(fromConfig.split(":")[1]) : (byte) 0;
		Material m = Material.AIR;
		for (Material mat : EnumSet.allOf(Material.class)) {
			if (mat.getId() == id) {
				m = mat;
			}
		}
		return new ItemStack(m, 1, data);
	}
	
	public static ItemStack createSkull(String url, String name) {
		ItemStack head;
		if (VersionManager.IS_VERSION_1_13) {
			head = create(Material.getMaterial("PLAYER_HEAD"), name);
		} else {
			head = create(Material.valueOf("SKULL_ITEM"), (byte) 3, name);
		}
		
		if (url.isEmpty()) return head;
		
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		headMeta.setOwner("Notch");
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", url));
		Field profileField;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}
	
	public static ItemStack createColouredLeather(Material armourPart, int red, int green, int blue) {
		ItemStack itemStack = new ItemStack(armourPart);
		LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
		leatherArmorMeta.setColor(Color.fromRGB(red, green, blue));
		itemStack.setItemMeta(leatherArmorMeta);
		return itemStack;
	}
	
	public static ItemStack addGlow(ItemStack item) {
		return UltraCosmeticsData.get().getVersionManager().getItemGlower().glow(item);
	}

	public static boolean areSame(ItemStack a, ItemStack b) {
		if(a.getType() != b.getType()) {
			return false;
		}

		if(a.getData().getData() != b.getData().getData()) {
			return false;
		}
		if((a.hasItemMeta() && !b.hasItemMeta())
			|| (!a.hasItemMeta() && b.hasItemMeta())) {
			return false;
		}
		if(!a.hasItemMeta() && !b.hasItemMeta()) {
			return true;
		}
		ItemMeta am = a.getItemMeta();
		ItemMeta bm = b.getItemMeta();

		if(!am.getDisplayName().equalsIgnoreCase(bm.getDisplayName())) {
			return false;
		}
		return true;
	}
	
	public static boolean haveSameName(ItemStack a, ItemStack b) {
		if (a.hasItemMeta() && b.hasItemMeta()) {
			if (a.getItemMeta().hasDisplayName() && b.getItemMeta().hasDisplayName()) {
				return a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName());
			}
		}
		return false;
	}
}
