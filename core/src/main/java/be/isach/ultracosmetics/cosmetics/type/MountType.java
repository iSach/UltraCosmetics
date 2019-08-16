package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.mounts.*;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.UCMaterial;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A cosmetic type.
 *
 * @author iSach
 * @since 12-18-2015
 */
public class MountType extends CosmeticEntType<Mount> {

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

    private int repeatDelay;

    MountType(String permission, String configName, UCMaterial material, EntityType entityType, String defaultDescription, int repeatDelay, Class<? extends Mount> mountClass, ServerVersion baseVersion) {
        super(Category.MOUNTS, configName, permission, defaultDescription, material, entityType, mountClass, baseVersion);
        this.repeatDelay = repeatDelay;

        VALUES.add(this);
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

    public static void register() {
        new MountType("ultracosmetics.mounts.druggedhorse", "DruggedHorse", UCMaterial.SUGAR, EntityType.HORSE, "&7&oThat is just too much!", 2, MountDruggedHorse.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.infernalhorror", "InfernalHorror", UCMaterial.BONE, UltraCosmeticsData.get().getVersionManager().getMounts().getHorrorType(), "&7&oThis mount comes directly from... hell!", 2, UltraCosmeticsData.get().getVersionManager().getMounts().getHorrorClass(), ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.glacialsteed", "GlacialSteed", UCMaterial.PACKED_ICE, EntityType.HORSE, "&7&oThis mount comes from North Pole!", 2, MountGlacialSteed.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.walkingdead", "WalkingDead", UCMaterial.ROTTEN_FLESH, UltraCosmeticsData.get().getVersionManager().getMounts().getWalkingDeadType(), "&7&oGraaaaw...", 2, UltraCosmeticsData.get().getVersionManager().getMounts().getWalkingDeadClass(), ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.mountoffire", "MountOfFire", UCMaterial.BLAZE_POWDER, EntityType.HORSE, "&7&oThe mount of Hadès!", 2, MountOfFire.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.snake", "Snake", UCMaterial.WHEAT_SEEDS, EntityType.SHEEP, "&7&oWatch out! It may bite..", 2, MountSnake.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.dragon", "Dragon", UCMaterial.DRAGON_EGG, EntityType.ENDER_DRAGON, "&7&oBecome a dragon rider!", 1, MountDragon.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.slime", "Slime", UCMaterial.SLIME_BALL, EntityType.SLIME, "&7&oSplat! Splat!", 2, UltraCosmeticsData.get().getVersionManager().getMounts().getSlimeClass(), ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.hypecart", "HypeCart", UCMaterial.MINECART, EntityType.MINECART, "&7&oEver wanted to drive a F1?\n&7&oNow you can!", 1, MountHypeCart.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.spider", "Spider", UCMaterial.COBWEB, EntityType.SPIDER, "&7&oYOU are the spider jockey!", 2, UltraCosmeticsData.get().getVersionManager().getMounts().getSpiderClass(), ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.rudolph", "Rudolph", UCMaterial.DEAD_BUSH, UltraCosmeticsData.get().getVersionManager().getMounts().getRudolphType(), "&7&oWhat would be Christmas\n&7&owithout Rudolph the Reeinder?", 2, UltraCosmeticsData.get().getVersionManager().getMounts().getRudolphClass(), ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.moltensnake", "MoltenSnake", UCMaterial.MAGMA_CREAM, EntityType.MAGMA_CUBE, "&7&oDeep under the Earth's surface, there\n&7&oexists a mythical species of Molten\n&7&oSnakes. This one will serve you eternally.", 1, MountMoltenSnake.class, ServerVersion.v1_8_R1);

        new MountType("ultracosmetics.mounts.mountofwater", "MountOfWater", UCMaterial.LIGHT_BLUE_DYE, EntityType.HORSE, "&7&oThe mount of Poséidon!", 2, MountOfWater.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.nyansheep", "NyanSheep", UCMaterial.CYAN_DYE, EntityType.SHEEP, "&4&lNyan &6&lnyan &e&lnyan\n&a&lnyan &3&lnyan &9&lnyan", 1, MountNyanSheep.class, ServerVersion.v1_8_R1);
        new MountType("ultracosmetics.mounts.ecologisthorse", "EcologistHorse", UCMaterial.GREEN_DYE, EntityType.HORSE, "&7&oBecome ecologist!", 2, MountEcologistHorse.class, ServerVersion.v1_8_R1);

    }
}
