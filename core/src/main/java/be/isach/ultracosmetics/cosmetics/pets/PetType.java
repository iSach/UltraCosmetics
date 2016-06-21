package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 20/12/15.
 */
public enum PetType {
    PIGGY("ultracosmetics.pets.piggy", "Piggy", Material.MONSTER_EGG, (byte) 90, "&7&oOink! Oink!", EntityType.PIG, PetPiggy.class),
    SHEEP("ultracosmetics.pets.sheep", "Sheep", Material.WOOL, (byte) 0, "&7&oBaaaa, baa", EntityType.SHEEP, PetSheep.class),
    EASTERBUNNY("ultracosmetics.pets.easterbunny", "EasterBunny", Material.CARROT_ITEM, (byte) 0, "&7&oIs it Easter yet?", EntityType.RABBIT, PetEasterBunny.class),
    COW("ultracosmetics.pets.cow", "Cow", Material.MILK_BUCKET, (byte) 0, "&7&oMoooo!", EntityType.COW, PetCow.class),
    KITTY("ultracosmetics.pets.kitty", "Kitty", Material.RAW_FISH, (byte) 0, "&7&oMeoooow", EntityType.OCELOT, PetKitty.class),
    DOG("ultracosmetics.pets.dog", "Dog", Material.BONE, (byte) 0, "&7&oWoof!", EntityType.WOLF, PetDog.class),
    CHICK("ultracosmetics.pets.chick", "Chick", Material.EGG, (byte) 0, "&7&oBwaaaaaaak!!", EntityType.CHICKEN, PetChick.class),
    WITHER("ultracosmetics.pets.wither", "Wither", Material.SKULL_ITEM, (byte) 1, "&7&oWatch out for me..", EntityType.WITHER, PetWither.class),
    PUMPLING("ultracosmetics.pets.pumpling", "Pumpling", Material.PUMPKIN, (byte) 0, "&7&oJust a little floating pumpkin", EntityType.ZOMBIE, UltraCosmetics.getInstance().getPets().getPumplingClass()),
    CHRISTMASELF("ultracosmetics.pets.christmaself", "ChristmasElf", Material.MONSTER_EGG, (byte) 120, "&7&oI can make presents for you!", EntityType.VILLAGER, PetChristmasElf.class);

    public static List<PetType> enabled = new ArrayList();
    private String permission;
    private String configName;
    private String description;
    private Material material;
    private byte data;
    private EntityType entityType;
    private Class<? extends Pet> clazz;

    PetType(String permission, String configName, Material material, byte data, String defaultDesc, EntityType entityType, Class<? extends Pet> clazz) {
        this.permission = permission;
        this.configName = configName;
        this.material = material;
        this.data = data;
        this.entityType = entityType;
        this.clazz = clazz;

        if (SettingsManager.getConfig().get(new StringBuilder().append("Pets.").append(configName).append(".Description").toString()) == null) {
            this.description = defaultDesc;
            UltraCosmetics.config.addDefault(new StringBuilder().append("Pets.").append(configName).append(".Description").toString(), getDescriptionColored(), "description of this pet.");
        } else {
            this.description = fromList((List) SettingsManager.getConfig().get(new StringBuilder().append("Pets.").append(configName).append(".Description").toString()));
        }
    }

    public Pet equip(Player player) {
        Pet pet = null;
        try {
            pet = this.clazz.getDeclaredConstructor(new Class[]{UUID.class}).newInstance(player == null ? null : player.getUniqueId());
            pet.equip();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return pet;
    }

    public Material getMaterial() {
        return this.material;
    }

    public byte getData() {
        return this.data;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public String getEntityName(Player player) {
        return MessageManager.getMessage(new StringBuilder().append("Pets.").append(this.configName).append(".entity-displayname").toString()).replace("%playername%", player.getName());
    }

    public String getMenuName() {
        return MessageManager.getMessage(new StringBuilder().append("Pets.").append(this.configName).append(".menu-name").toString());
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean(new StringBuilder().append("Pets.").append(this.configName).append(".Enabled").toString());
    }

    public static List<PetType> enabled() {
        return enabled;
    }

    public String getConfigName() {
        return this.configName;
    }

    public List<String> getDescription() {
        List desc = new ArrayList();
        for (String string : this.description.split("\n"))
            desc.add(string.replace('&', '§'));
        return desc;
    }

    public List<String> getDescriptionColored() {
        return Arrays.asList(this.description.split("\n"));
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean(new StringBuilder().append("Pets.").append(getConfigName()).append(".Show-Description").toString());
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean(new StringBuilder().append("Pets.").append(getConfigName()).append(".Can-Be-Found-In-Treasure-Chests").toString());
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++)
            stringBuilder.append(new StringBuilder().append(description.get(i)).append(i < description.size() - 1 ? "\n" : "").toString());
        return stringBuilder.toString();
    }
}
