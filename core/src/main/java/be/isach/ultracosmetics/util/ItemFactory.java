package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sacha on 03/08/15.
 */
public class ItemFactory {
    // for some reason I don't understand, there's no Tag or XTag for dyes
    private static final List<XMaterial> DYES = new ArrayList<>(16);
    private static final List<XMaterial> STAINED_GLASS = new ArrayList<>(16);
    private static final FixedMetadataValue UNPICKABLE_META = new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), true);

    static {
        for (XMaterial mat : XMaterial.VALUES) {
            if (mat.name().endsWith("_DYE")) {
                DYES.add(mat);
            } else if (mat.name().endsWith("_STAINED_GLASS")) {
                STAINED_GLASS.add(mat);
            }
        }
        ItemStack itemStack = getItemStackFromConfig("Fill-Blank-Slots-With-Item.Item");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "");
        itemStack.setItemMeta(itemMeta);
        fillerItem = itemStack;
    }

    private static boolean noticePrinted = false;
    public static final ItemStack fillerItem;

    public static ItemStack create(XMaterial material, String displayName, String... lore) {
        return rename(material.parseItem(), displayName, lore);
    }

    public static ItemStack rename(ItemStack itemstack, String displayName) {
        return rename(itemstack, displayName, (String[])null);
    }

    public static ItemStack rename(ItemStack itemstack, String displayName, String... lore) {
        ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            List<String> finalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            for (String s : lore) {
                if (s != null) {
                    finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            meta.setLore(finalLore);
        }
        itemstack.setItemMeta(meta);
        return itemstack;
    }

    public static Item setUnpickable(Item item) {
        item.setMetadata("UNPICKABLEUP", UNPICKABLE_META);
        return item;
    }

    public static Item spawnUnpickableItem(ItemStack stack, Location loc, Vector velocity) {
        Item item = loc.getWorld().dropItem(loc, stack);
        item.setVelocity(velocity);
        setUnpickable(item);
        return item;
    }

    public static Item createUnpickableItemDirectional(XMaterial material, Player player, double scale) {
        return spawnUnpickableItem(material.parseItem(), player.getEyeLocation(), player.getLocation().getDirection().multiply(scale));
    }

    public static Item createUnpickableItemVariance(XMaterial material, Location loc, Random random, double variance) {
        return spawnUnpickableItem(material.parseItem(), loc, new Vector(random.nextDouble() - 0.5, random.nextDouble() / 2.0, random.nextDouble() - 0.5).multiply(variance));
    }

    public static void fillInventory(Inventory inventory) {
        if (SettingsManager.getConfig().getBoolean("Fill-Blank-Slots-With-Item.Enabled")) {
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null
                        || inventory.getItem(i).getType() == Material.AIR)
                    inventory.setItem(i, fillerItem);
            }
        }
    }

    public static ItemStack getItemStackFromConfig(String path) {
        XMaterial mat = getFromConfigInternal(path);
        if (mat != null) return mat.parseItem();
        return create(XMaterial.BEDROCK, "&cError parsing material", "&cFailed to parse material");
    }

    public static XMaterial getXMaterialFromConfig(String path) {
        XMaterial mat = getFromConfigInternal(path);
        return mat == null ? XMaterial.BEDROCK : mat;
    }

    public static XMaterial getNullableXMaterialFromConfig(String path) {
        return getFromConfigInternal(path);
    }

    public static List<XMaterial> getXMaterialListFromConfig(String path) {
        List<XMaterial> mats = new ArrayList<>();
        CustomConfiguration cc = UltraCosmeticsData.get().getPlugin().getConfig();
        for (String matString : cc.getStringList(path)) {
            XMaterial.matchXMaterial(matString).ifPresent(m -> mats.add(m));
        }
        return mats;
    }

    private static XMaterial getFromConfigInternal(String path) {
        String fromConfig = UltraCosmeticsData.get().getPlugin().getConfig().getString(path);
        if (fromConfig == null) return null;
        if (MathUtils.isInteger(fromConfig) || fromConfig.contains(":")) {
            if (!noticePrinted) {
                UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.ERROR, "UltraCosmetics no longer supports numeric IDs, please replace it with a material name.");
                noticePrinted = true;
            }
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.ERROR, "Offending config path: " + path);
            return null;
        }
        // null if not found
        return XMaterial.matchXMaterial(fromConfig).orElse(null);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack createSkull(String url, String name) {
        ItemStack head = create(XMaterial.PLAYER_HEAD, name);

        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        // TODO: is this required?
        headMeta.setOwner("Notch");
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Method setProfileMethod = null;
        try {
            setProfileMethod = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
        } catch (NoSuchMethodException | SecurityException ignored) {}
        try {
            // if available, we use setProfile(GameProfile) so that it sets both the profile field and the
            // serialized profile field for us. If the serialized profile field isn't set
            // ItemStack#isSimilar() and ItemStack#equals() throw an error.
            if (setProfileMethod == null) {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } else {
                setProfileMethod.setAccessible(true);
                setProfileMethod.invoke(headMeta, profile);
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SecurityException | InvocationTargetException e1) {
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

    public static void addGlow(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
    }

    public static boolean haveSameName(ItemStack a, ItemStack b) {
        if (a.hasItemMeta() && b.hasItemMeta()) {
            if (a.getItemMeta().hasDisplayName() && b.getItemMeta().hasDisplayName()) {
                return a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName());
            }
        }
        return false;
    }

    private static XMaterial randomXMaterial(List<XMaterial> mats) {
        return mats.get(ThreadLocalRandom.current().nextInt(mats.size()));
    }

    private static ItemStack randomStack(List<XMaterial> mats) {
        return randomXMaterial(mats).parseItem();
    }

    public static ItemStack getRandomDye() {
        return randomStack(DYES);
    }

    public static ItemStack getRandomStainedGlass() {
        return randomStack(STAINED_GLASS);
    }

    public static ItemStack randomItemFromTag(XTag<XMaterial> tag) {
        return randomFromTag(tag).parseItem();
    }

    public static XMaterial randomFromTag(XTag<XMaterial> tag) {
        // copy tag values into temporary ArrayList because getting random values from a Set is hard
        return randomXMaterial(new ArrayList<>(tag.getValues()));
    }
}
