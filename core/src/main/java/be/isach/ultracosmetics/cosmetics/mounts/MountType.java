package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 18/12/15.
 */
public enum MountType {
    DRUGGEDHORSE("ultracosmetics.mounts.druggedhorse", "DruggedHorse", Material.SUGAR, (byte) 0, EntityType.HORSE, "&7&oThat is just too much!", MountDruggedHorse.class),
    INFERNALHORROR("ultracosmetics.mounts.infernalhorror", "InfernalHorror", Material.BONE, (byte) 0, EntityType.HORSE,
            "&7&oThis mount comes directly from... hell!", MountInfernalHorror.class),
    GLACIALSTEED("ultracosmetics.mounts.glacialsteed", "GlacialSteed", Material.PACKED_ICE, (byte) 0, EntityType.HORSE,
            "&7&oThis mount comes from North Pole!", MountGlacialSteed.class),
    WALKINGDEAD("ultracosmetics.mounts.walkingdead", "WalkingDead", Material.ROTTEN_FLESH, (byte) 0, EntityType.HORSE, "&7&oGraaaaw...", MountWalkingDead.class),
    MOUNTOFFIRE("ultracosmetics.mounts.mountoffire", "MountOfFire", Material.BLAZE_POWDER, (byte) 0, EntityType.HORSE, "&7&oThe mount of Hadès!", MountOfFire.class),
    MOUNTOFWATER("ultracosmetics.mounts.mountofwater", "MountOfWater", Material.INK_SACK, (byte) 4, EntityType.HORSE, "&7&oThe mount of Poséidon!", MountOfWater.class),
    ECOLOGISTHORSE("ultracosmetics.mounts.ecologisthorse", "EcologistHorse", Material.RED_ROSE, (byte) 0, EntityType.HORSE, "&7&oBecome ecologist!", MountEcologistHorse.class),
    SNAKE("ultracosmetics.mounts.snake", "Snake", Material.SEEDS, (byte) 0, EntityType.SHEEP, "&7&oWatch out! It may bite..", MountSnake.class),
    NYANSHEEP("ultracosmetics.mounts.nyansheep", "NyanSheep", Material.STAINED_GLASS, (byte) 9, EntityType.SHEEP,
            "&4&lNyan &6&lnyan &e&lnyan\n&a&lnyan &3&lnyan &9&lnyan", MountNyanSheep.class),
    DRAGON("ultracosmetics.mounts.dragon", "Dragon", Material.DRAGON_EGG, (byte) 0, EntityType.ENDER_DRAGON,
            "&7&oBecome a dragon rider!", MountDragon.class),
    SKYSQUID("ultracosmetics.mounts.skysquid", "SkySquid", Material.INK_SACK, (byte) 0, EntityType.SQUID, "&7&oWat.", UltraCosmetics.getInstance().getMounts().getSquidClass()),
    SLIME("ultracosmetics.mounts.slime", "Slime", Material.SLIME_BALL, (byte) 0, EntityType.SLIME, "&7&oSplat! Splat!", UltraCosmetics.getInstance().getMounts().getSlimeClass()),
    HYPECART("ultracosmetics.mounts.hypecart", "HypeCart", Material.MINECART, (byte) 0, EntityType.MINECART,
            "&7&oEver wanted to drive a F1?\n&7&oNow you can!", MountHypeCart.class),
    SPIDER("ultracosmetics.mounts.spider", "Spider", Material.WEB, (byte) 0, EntityType.SPIDER, "&7&oYOU are the spider jockey!", UltraCosmetics.getInstance().getMounts().getSpiderClass()),
    RUDOLPH("ultracosmetics.mounts.rudolph", "Rudolph", Material.DEAD_BUSH, (byte) 0, EntityType.HORSE,
            "&7&oWhat would be Christmas\n&7&owithout Rudolph the Reeinder?", MountRudolph.class),
    MOLTENSNAKE("ultracosmetics.mounts.moltensnake", "MoltenSnake", Material.MAGMA_CREAM, (byte) 0, EntityType.MAGMA_CUBE,
            "&7&oDeep under the Earth's surface, there\n&7&oexists a mythical species of Molten\n&7&oSnakes. This one will serve you eternally.", MountMoltenSnake.class),
    FLYINGSHIP("ultracosmetics.mounts.flyingship", "FlyingShip", Material.BOAT, (byte) 0, EntityType.BOAT, "&7&oBomb them all!", MountFlyingShip.class);


    private String permission, configName, description;
    private Material material;
    private byte data;
    private EntityType entityType;
    private Class<? extends Mount> clazz;
    public static List<MountType> mountTypes = new ArrayList<>();

    MountType(String permission, String configName, Material material, byte data, EntityType entityType, String defaultDescription, Class<? extends Mount> mountClass) {
        this.permission = permission;
        this.configName = configName;
        this.material = material;
        this.data = data;
        this.entityType = entityType;
        this.clazz = mountClass;

        if (SettingsManager.getConfig().get("Mounts." + configName + ".Description") == null) {
            this.description = defaultDescription;
            SettingsManager.getConfig().set("Mounts." + configName + ".Description", getDescriptionWithColor(), "Description of this mount.");
        } else {
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Mounts." + configName + ".Description")));
        }
    }

    public String getPermission() {
        return permission;
    }

    public boolean isEnabled() {
        if ((this == FLYINGSHIP || this == SKYSQUID) && (UltraCosmetics.getServerVersion().compareTo(ServerVersion.v1_9_R1) >= 0))
            return false;
        return SettingsManager.getConfig().getBoolean("Mounts." + configName + ".Enabled");
    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n")) {
            desc.add(string.replace('&', '§'));
        }
        return desc;
    }

    public static List<MountType> enabled() {
        return mountTypes;
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Mounts." + getConfigName() + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Mounts." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
    }

    public List<String> getDescriptionWithColor() {
        return Arrays.asList(description.split("\n"));
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getName(Player player) {
        return MessageManager.getMessage("Mounts." + getConfigName() + ".entity-displayname").replace("%playername%", player.getName());
    }

    public Mount equip(Player player) {
        Mount mount = null;
        try {
            mount = clazz.getDeclaredConstructor(UUID.class).newInstance(player == null ? null : player.getUniqueId());
            mount.equip();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return mount;
    }

    public String getMenuName() {
        return MessageManager.getMessage("Mounts." + getConfigName() + ".menu-name");
    }

    public String getConfigName() {
        return configName;
    }

    public Material getMaterial() {
        return material;
    }

    public Byte getData() {
        return data;
    }
}
