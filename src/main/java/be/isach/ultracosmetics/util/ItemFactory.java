package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.v1_8_R3.ItemGlower_1_8_R3;
import be.isach.ultracosmetics.util.v1_9_R1.ItemGlower_1_9_R1;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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

    public static ItemStack create(Material material, byte data, String displayName, String... lore) {
        ItemStack itemStack = new MaterialData(material, data).toItemStack(1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        if (lore != null) {
            List<String> finalLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList();
            for (String s : lore)
                if (s != null)
                    finalLore.add(s.replace("&", "§"));
            itemMeta.setLore(finalLore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void fillInventory(Inventory inventory) {
        if (SettingsManager.getConfig().getBoolean("Fill-Blank-Slots-With-Item.Enabled")) {
            MaterialData materialData = getMaterialData(UltraCosmetics.config.getString("Fill-Blank-Slots-With-Item.Item"));
            ItemStack itemStack = materialData.toItemStack(1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§7");
            itemStack.setItemMeta(itemMeta);
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null
                        || inventory.getItem(i).getType() == Material.AIR)
                    inventory.setItem(i, itemStack);
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

    public static ItemStack createSkull(String urlToFormat) {
        String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + urlToFormat;
        ItemStack head = create(Material.SKULL_ITEM, (byte) 3, "§8§oHat");

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
        switch (UltraCosmetics.getServerVersion()) {
            default:
                return ItemGlower_1_8_R3.glow(item);
            case v1_9_R1:
                return ItemGlower_1_9_R1.glow(item);
        }
    }
}
