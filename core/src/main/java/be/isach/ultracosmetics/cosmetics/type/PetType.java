package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.pets.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Sacha on 20/12/15.
 */
public final class PetType extends CosmeticMatType<Pet> {

    private final static List<PetType> ENABLED = new ArrayList<>();
    private final static List<PetType> VALUES = new ArrayList<>();

    public static List<PetType> enabled() {
        return ENABLED;
    }

    public static List<PetType> values() {
        return VALUES;
    }

    public static PetType valueOf(String s) {
        for (PetType petType : VALUES) {
            if (petType.getConfigName().equalsIgnoreCase(s)) return petType;
        }
        return null;
    }

    public static PetType getByName(String s) {
        try {
            return VALUES.stream().filter(value -> value.getName().equalsIgnoreCase(s)).findFirst().get();
        } catch (Exception exc) {
            return null;
        }
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    public static final PetType PIGGY = new PetType("ultracosmetics.pets.piggy", "Piggy", Material.PORK, (byte) 0, "&7&oOink! Oink!", EntityType.PIG, PetPiggy.class);
    public static final PetType SHEEP = new PetType("ultracosmetics.pets.sheep", "Sheep", Material.WOOL, (byte) 0, "&7&oBaaaa, baa", EntityType.SHEEP, PetSheep.class);
    public static final PetType EASTERBUNNY = new PetType("ultracosmetics.pets.easterbunny", "EasterBunny", Material.CARROT_ITEM, (byte) 0, "&7&oIs it Easter yet?", EntityType.RABBIT, PetEasterBunny.class);
    public static final PetType COW = new PetType("ultracosmetics.pets.cow", "Cow", Material.MILK_BUCKET, (byte) 0, "&7&oMoooo!", EntityType.COW, PetCow.class);
    public static final PetType MOOSHROOM = new PetType("ultracosmetics.pets.mooshroom", "Mooshroom", Material.RED_MUSHROOM, (byte) 0, "&7&oMoooo!", EntityType.MUSHROOM_COW, PetMooshroom.class);
    public static final PetType KITTY = new PetType("ultracosmetics.pets.kitty", "Kitty", Material.RAW_FISH, (byte) 0, "&7&oMeoooow", EntityType.OCELOT, PetKitty.class);
    public static final PetType DOG = new PetType("ultracosmetics.pets.dog", "Dog", Material.BONE, (byte) 0, "&7&oWoof!", EntityType.WOLF, PetDog.class);
    public static final PetType CHICK = new PetType("ultracosmetics.pets.chick", "Chick", Material.EGG, (byte) 0, "&7&oBwaaaaaaak!!", EntityType.CHICKEN, PetChick.class);
    public static final PetType WITHER = new PetType("ultracosmetics.pets.wither", "Wither", Material.SKULL_ITEM, (byte) 1, "&7&oWatch out for me..", EntityType.WITHER, PetWither.class);
    public static final PetType PUMPLING = new PetType("ultracosmetics.pets.pumpling", "Pumpling", Material.PUMPKIN, (byte) 0, "&7&oJust a little floating pumpkin", EntityType.ZOMBIE, UltraCosmeticsData.get().getVersionManager().getPets().getPumplingClass());
    public static final PetType CHRISTMASELF = new PetType("ultracosmetics.pets.christmaself", "ChristmasElf", Material.BEACON, (byte) 0, "&7&oI can make presents for you!", EntityType.VILLAGER, PetChristmasElf.class);

    private EntityType entityType;

    private PetType(String permission, String configName, Material material, byte data, String defaultDesc, EntityType entityType, Class<? extends Pet> clazz) {
        super(Category.PETS, configName, permission, defaultDesc, material, data, clazz);

        this.entityType = entityType;

        VALUES.add(this);
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public String getEntityName(Player player) {
        return MessageManager.getMessage("Pets." + getConfigName() + ".entity-displayname").replace("%playername%", player.getName());
    }

    @Override
    public String getName() {
        return MessageManager.getMessage("Pets." + getConfigName() + ".menu-name");
    }
}
