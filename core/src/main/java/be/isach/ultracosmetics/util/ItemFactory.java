package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
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

    public static ItemStack create(Material material, String displayName, String... lore) {
        return create(material, (byte)0x0, displayName, lore);
    }

    public static void fillInventory(Inventory inventory) {
        if (SettingsManager.getConfig().getBoolean("Fill-Blank-Slots-With-Item.Enabled")) {
            if (fillerItem == null) {
                MaterialData materialData = getMaterialData(SettingsManager.getConfig().getString("Fill-Blank-Slots-With-Item.Item"));
                ItemStack itemStack = materialData.toItemStack(1);
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

    public static MaterialData createFromConfig(String path) {
        String config = SettingsManager.getConfig().getString(path);
        return getMaterialData(config);
    }

    private static MaterialData getMaterialData(String name) {
        return new MaterialData(Integer.parseInt(name.split(":")[0]),
                (name.split(":").length > 1 ? (byte) Integer.parseInt(name.split(":")[1]) : (byte) 0));
    }

    public static ItemStack createSkull(String url, String name) {
        ItemStack head = create(Material.SKULL_ITEM, (byte) 3, name);

        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
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
}
