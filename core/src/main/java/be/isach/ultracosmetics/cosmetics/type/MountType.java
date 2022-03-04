package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.mounts.*;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.XMaterial;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A cosmetic type.
 *
 * @author iSach
 * @since 12-18-2015
 */
public class MountType extends CosmeticEntType<Mount<?>> {

    private final static List<MountType> ENABLED = new ArrayList<>();
    private final static List<MountType> VALUES = new ArrayList<>();

    public static List<MountType> enabled() {
        return ENABLED;
    }

    public static List<MountType> values() {
        return VALUES;
    }

    public static MountType valueOf(String s) {
        for (MountType mountType : VALUES) {
            if (mountType.getConfigName().equalsIgnoreCase(s)) return mountType;
        }
        return null;
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(MountType::isEnabled).collect(Collectors.toList()));
    }

    private final int repeatDelay;
    private final List<XMaterial> defaultBlocks;
    private final double defaultSpeed;
    private final double movementSpeed;

    private MountType(String permission, String configName, XMaterial material, EntityType entityType, String defaultDescription, int repeatDelay, List<XMaterial> defaultBlocks, double defaultSpeed, Class<? extends Mount<?>> mountClass) {
        super(Category.MOUNTS, configName, permission, defaultDescription, material, entityType, mountClass, ServerVersion.earliest());
        this.repeatDelay = repeatDelay;
        this.defaultBlocks = defaultBlocks;
        this.defaultSpeed = defaultSpeed;
        this.movementSpeed = SettingsManager.getConfig().getDouble("Mounts." + configName + ".Speed", defaultSpeed);
        VALUES.add(this);
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public double getDefaultMovementSpeed() {
        return defaultSpeed;
    }

    public String getMenuName() {
        return MessageManager.getMessage("Mounts." + getConfigName() + ".menu-name");
    }

    @Override
    public String getName() {
        return getMenuName();
    }

    public String getName(Player player) {
        return MessageManager.getMessage("Mounts." + getConfigName() + ".entity-displayname").replace("%playername%", player.getName());
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public List<XMaterial> getDefaultBlocks() {
        return defaultBlocks;
    }

    public boolean doesPlaceBlocks() {
        return defaultBlocks != null;
    }

    public static void register() {
        new MountType("ultracosmetics.mounts.druggedhorse", "DruggedHorse", XMaterial.SUGAR, EntityType.HORSE, "&7&oThat is just too much!", 2, null, 1.1, MountDruggedHorse.class);
        new MountType("ultracosmetics.mounts.glacialsteed", "GlacialSteed", XMaterial.PACKED_ICE, EntityType.HORSE, "&7&oThis mount comes from North Pole!", 2, Arrays.asList(XMaterial.SNOW_BLOCK), 0.4, MountGlacialSteed.class);
        new MountType("ultracosmetics.mounts.mountoffire", "MountOfFire", XMaterial.BLAZE_POWDER, EntityType.HORSE, "&7&oThe mount of Hades!", 2, Arrays.asList(XMaterial.ORANGE_TERRACOTTA, XMaterial.YELLOW_TERRACOTTA, XMaterial.RED_TERRACOTTA), 0.4, MountOfFire.class);
        new MountType("ultracosmetics.mounts.snake", "Snake", XMaterial.WHEAT_SEEDS, EntityType.SHEEP, "&7&oWatch out! It may bite..", 2, null, 0.3, MountSnake.class);
        new MountType("ultracosmetics.mounts.dragon", "Dragon", XMaterial.DRAGON_EGG, EntityType.ENDER_DRAGON, "&7&oBecome a dragon rider!", 1, null, 0.7, MountDragon.class);
        new MountType("ultracosmetics.mounts.slime", "Slime", XMaterial.SLIME_BALL, EntityType.SLIME, "&7&oSplat! Splat!", 2, null, 0.8, UltraCosmeticsData.get().getVersionManager().getModule().getSlimeClass());
        new MountType("ultracosmetics.mounts.hypecart", "HypeCart", XMaterial.MINECART, EntityType.MINECART, "&7&oEver wanted to drive a F1?\n&7&oNow you can!", 1, null, 0, MountHypeCart.class);
        new MountType("ultracosmetics.mounts.spider", "Spider", XMaterial.COBWEB, EntityType.SPIDER, "&7&oYOU are the spider jockey!", 2, null, 0.4, UltraCosmeticsData.get().getVersionManager().getModule().getSpiderClass());
        new MountType("ultracosmetics.mounts.moltensnake", "MoltenSnake", XMaterial.MAGMA_CREAM, EntityType.MAGMA_CUBE, "&7&oDeep under the Earth's surface, there\n&7&oexists a mythical species of Molten\n&7&oSnakes. This one will serve you eternally.", 1, null, 0.4, MountMoltenSnake.class);

        new MountType("ultracosmetics.mounts.mountofwater", "MountOfWater", XMaterial.LIGHT_BLUE_DYE, EntityType.HORSE, "&7&oThe mount of Poseidon!", 2, Arrays.asList(XMaterial.LIGHT_BLUE_TERRACOTTA, XMaterial.CYAN_TERRACOTTA, XMaterial.BLUE_TERRACOTTA), 0.4, MountOfWater.class);
        new MountType("ultracosmetics.mounts.nyansheep", "NyanSheep", XMaterial.CYAN_DYE, EntityType.SHEEP, "&4&lNyan &6&lnyan &e&lnyan\n&a&lnyan &3&lnyan &9&lnyan", 1, null, 0.4, MountNyanSheep.class);
        new MountType("ultracosmetics.mounts.ecologisthorse", "EcologistHorse", XMaterial.GREEN_DYE, EntityType.HORSE, "&7&oBecome ecologist!", 2, Arrays.asList(XMaterial.LIME_TERRACOTTA, XMaterial.GREEN_TERRACOTTA), 0.4, MountEcologistHorse.class);
        
        new MountType("ultracosmetics.mounts.rudolph", "Rudolph", XMaterial.DEAD_BUSH, UltraCosmeticsData.get().getVersionManager().getMounts().getRudolphType(), "&7&oWhat would be Christmas\n&7&owithout Rudolph the Reindeer?", 2, null, 0.4, UltraCosmeticsData.get().getVersionManager().getMounts().getRudolphClass());
        new MountType("ultracosmetics.mounts.walkingdead", "WalkingDead", XMaterial.ROTTEN_FLESH, UltraCosmeticsData.get().getVersionManager().getMounts().getWalkingDeadType(), "&7&oGraaaaw...", 2, null, 0.4, UltraCosmeticsData.get().getVersionManager().getMounts().getWalkingDeadClass());
        new MountType("ultracosmetics.mounts.infernalhorror", "InfernalHorror", XMaterial.BONE, UltraCosmeticsData.get().getVersionManager().getMounts().getHorrorType(), "&7&oThis mount comes directly from... hell!", 2, null, 0.4, UltraCosmeticsData.get().getVersionManager().getMounts().getHorrorClass());
    }
}
