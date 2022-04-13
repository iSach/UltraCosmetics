package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.HatType;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.cosmetics.type.SuitCategory;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.menu.Menus;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Cosmetic category enum.
 *
 * @author iSach
 * @since 06-20-2016
 */
public enum Category {

    PETS("Pets", "%petname%", "pe", k -> k.getPetsMenu(), () -> PetType.enabled()),
    GADGETS("Gadgets", "%gadgetname%", "g", k -> k.getGadgetsMenu(), () -> GadgetType.enabled()),
    EFFECTS("Particle-Effects", "%effectname%", "ef", k -> k.getEffectsMenu(), () -> ParticleEffectType.enabled()),
    MOUNTS("Mounts", "%mountname%", "mou", k -> k.getMountsMenu(), () -> MountType.enabled()),
    MORPHS("Morphs", "%morphname%", "mor", k -> k.getMorphsMenu(), () -> MorphType.enabled()),
    HATS("Hats", "%hatname%", "h", k -> k.getHatsMenu(), () -> HatType.enabled()),
    SUITS("Suits", "%suitname%", "s", k -> k.getSuitsMenu(), () -> SuitType.enabled()),
    EMOTES("Emotes", "%emotename%", "e", k -> k.getEmotesMenu(), () -> EmoteType.enabled());

    public static int enabledSize() {
        return enabled().size();
    }

    public static List<Category> enabled() {
        return Arrays.stream(values()).filter(Category::isEnabled).collect(Collectors.toList());
    }

    public static Category fromString(String name) {
        String lowerName = name.toLowerCase();
        for (Category cat : values()) {
            if (lowerName.startsWith(cat.prefix)) {
                return cat;
            }
        }
        return null;
    }

    public CosmeticType<?> valueOfType(String name) {
        if (name == null) return null;
        switch(this) {
        case EFFECTS:
            return ParticleEffectType.valueOf(name);
        case EMOTES:
            return EmoteType.valueOf(name);
        case GADGETS:
            return GadgetType.valueOf(name);
        case HATS:
            return HatType.valueOf(name);
        case MORPHS:
            return MorphType.valueOf(name);
        case MOUNTS:
            return MountType.valueOf(name);
        case PETS:
            return PetType.valueOf(name);
        case SUITS:
            // at least return something
            return SuitCategory.valueOf(name).getHelmet();
        }
        return null;
    }

    /**
     * The config path name.
     */
    private final String configPath;

    private final String chatPlaceholder;
    private final String prefix;
    private final Function<Menus,CosmeticMenu<?>> menuFunc;
    private final Supplier<List<? extends CosmeticType<?>>> enabledFunc;

    /**
     * Category of Cosmetic.
     *
     * @param configPath       The config path name.
     * @param chatPlaceholder
     * @param prefix TODO
     */
    private Category(String configPath, String chatPlaceholder, String prefix, Function<Menus,CosmeticMenu<?>> menuFunc, Supplier<List<? extends CosmeticType<?>>> enabledFunc) {
        this.configPath = configPath;
        this.chatPlaceholder = chatPlaceholder;
        this.prefix = prefix;
        this.menuFunc = menuFunc;
        this.enabledFunc = enabledFunc;
    }

    /**
     * Gets the ItemStack in Main Menu.
     *
     * @return The ItemStack in Main Menu.
     */
    public ItemStack getItemStack() {
        ItemStack is;
        if (SettingsManager.getConfig().contains("Categories." + configPath + ".Main-Menu-Item")) {
            is = ItemFactory.getItemStackFromConfig("Categories." + configPath + ".Main-Menu-Item");
        } else {
            is = ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA" +
                    "6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA1OWQ1OWViNGU1OWM" +
                    "zMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzE" +
                    "wIn19fQ==", ChatColor.DARK_GRAY + "" + ChatColor.ITALIC);
        }
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(MessageManager.getMessage("Menu." + configPath + ".Button.Name"));
        is.setItemMeta(itemMeta);
        return is;
    }

    /**
     * Checks if the category is enabled.
     *
     * @return {@code true} if enabled, otherwise {@code false}.
     */
    public boolean isEnabled() {
        return !(this == MORPHS && !Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
                && SettingsManager.getConfig().getBoolean("Categories-Enabled." + configPath);
    }

    /**
     * Checks if the category should have a back arrow in its menu.
     *
     * @return {@code true} if has arrow, otherwise {@code false}
     */
    public boolean hasGoBackArrow() {
        return !(!UltraCosmeticsData.get().areTreasureChestsEnabled()
                && enabledSize() == 1)
                && (boolean) (SettingsManager.getConfig().get("Categories." + configPath + ".Go-Back-Arrow"));
    }

    /**
     * @return Config Path.
     */
    public String getConfigPath() {
        return configPath;
    }

    public String getConfigName() {
        // Like configPath but value is different for Category.EFFECTS
        return name().substring(0, 1) + name().substring(1).toLowerCase();
    }

    public String getActivateTooltip() {
        return MessageManager.getMessage("Menu." + configPath + ".Button.Tooltip-Equip");
    }

    public String getDeactivateTooltip() {
        return MessageManager.getMessage("Menu." + configPath + ".Button.Tooltip-Unequip");
    }

    public String getChatPlaceholder() {
        return chatPlaceholder;
    }

    public String getActivateMessage() {
        return MessageManager.getMessage(configPath + ".Equip");
    }

    public String getDeactivateMessage() {
        return MessageManager.getMessage(configPath + ".Unequip");
    }

    public CosmeticMenu<?> getMenu(Menus menus) {
        return menuFunc.apply(menus);
    }

    public List<? extends CosmeticType<?>> getEnabled() {
        return enabledFunc.get();
    }
}
