package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.version.VersionManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.util
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class TexturedSkullFactory {

    public static ItemStack createSkull(String url) {
        url = "http://textures.minecraft.net/texture/" + url;
        ItemStack skull;
        if (VersionManager.IS_VERSION_1_13) {
            skull = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } else {
            skull = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Emote");
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = UltraCosmeticsData.get().getVersionManager().getEntityUtil().getEncodedData(url);
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Method setProfileMethod = null;
        try {
            setProfileMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
        } catch (NoSuchMethodException | SecurityException ignored) {}
        // see ItemFactory#createSkull(String, String)
        try {
            if (setProfileMethod == null) {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } else {
                setProfileMethod.setAccessible(true);
                setProfileMethod.invoke(skullMeta, profile);
            }
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

}