package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.version.VersionManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Represents an instance of a Christmas Elf pet summoned by a player.
 *
 * @author iSach
 * @since 11-29-2015
 */
public class PetChristmasElf extends Pet {
    private static List<ItemStack> presents = new ArrayList<>(Arrays.asList(
            getSkull("f5612dc7b86d71afc1197301c15fd979e9f39e7b1f41d8f1ebdf8115576e2e"),
            getSkull("6b4cde16a4014de0a7651f6067f12695bb5fed6feaec1e9413ca4271e7c819"),
            getSkull("d08ce7deba56b726a832b61115ca163361359c30434f7d5e3c3faa6fe4052"),
            getSkull("928e692d86e224497915a39583dbe38edffd39cbba457cc95a7ac3ea25d445"),
            getSkull("1b6730de7e5b941efc6e8cbaf5755f9421a20de871759682cd888cc4a81282"),
            getSkull("1ac1163f54dcbb0e8e31ac675696f2409299c5abbf6c3fe73bf1cfe91422e1"),
            getSkull("6cef9aa14e884773eac134a4ee8972063f466de678363cf7b1a21a85b7"),
            getSkull("aa074845885202e17ed5c4be4103733121235c5440ae3a1c49fbd39317b04d")));

    private Random r = new Random();

    public PetChristmasElf(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("christmaself"), presents.get(0));
    }

    @Override
    public void onUpdate() {
        final Item drop = entity.getWorld().dropItem(((Villager) entity).getEyeLocation(), presents.get(r.nextInt(presents.size())));
        drop.setPickupDelay(30000);
        drop.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        items.add(drop);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            drop.remove();
            items.remove(drop);
        }, 5);
    }

    private static ItemStack getSkull(String url) {
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
        skullMeta.setDisplayName(UltraCosmeticsData.get().getItemNoPickupString());
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = UltraCosmeticsData.get().getVersionManager().getEntityUtil().getEncodedData(url);
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        assert profileField != null;
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
