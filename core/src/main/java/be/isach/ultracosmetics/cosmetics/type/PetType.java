package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.pets.*;
import be.isach.ultracosmetics.util.ServerVersion;
import com.cryptomorin.xseries.XMaterial;
import be.isach.ultracosmetics.version.GetForVersion;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Pet types.
 *
 * @author iSach
 * @since 12-20-2015
 */
public final class PetType extends CosmeticEntType<Pet> {

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
        Optional<PetType> optional = VALUES.stream().filter(value -> value.getConfigName().equalsIgnoreCase(s)).findFirst();
        if (!optional.isPresent()) return null;
        return optional.get();
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    private PetType(String configName, XMaterial material, String defaultDesc, EntityType entityType, Class<? extends Pet> clazz) {
        super(Category.PETS, configName, defaultDesc, material, entityType, clazz);

        VALUES.add(this);
    }

    public String getEntityName(Player player) {
        return MessageManager.getMessage("Pets." + getConfigName() + ".entity-displayname").replace("%playername%", player.getName());
    }

    @Override
    public String getName() {
        return MessageManager.getMessage("Pets." + getConfigName() + ".menu-name");
    }

    public static void register() {
        ServerVersion serverVersion = UltraCosmeticsData.get().getServerVersion();

        new PetType("Piggy", XMaterial.PORKCHOP, "&7&oOink! Oink!", EntityType.PIG, PetPiggy.class);
        new PetType("EasterBunny", XMaterial.CARROT, "&7&oIs it Easter yet?", EntityType.RABBIT, PetEasterBunny.class);
        new PetType("Cow", XMaterial.MILK_BUCKET, "&7&oMoooo!", EntityType.COW, PetCow.class);
        new PetType("Mooshroom", XMaterial.RED_MUSHROOM, "&7&oMoooo!", EntityType.MUSHROOM_COW, PetMooshroom.class);
        new PetType("Dog", XMaterial.BONE, "&7&oWoof!", EntityType.WOLF, PetDog.class);
        new PetType("Chick", XMaterial.EGG, "&7&oBwaaaaaaak!!", EntityType.CHICKEN, PetChick.class);
        new PetType("Pumpling", XMaterial.PUMPKIN, "&7&oJust a little floating pumpkin", EntityType.ZOMBIE, UltraCosmeticsData.get().getVersionManager().getPets().getPumplingClass());
        new PetType("ChristmasElf", XMaterial.BEACON, "&7&oI can make presents for you!", EntityType.VILLAGER, PetChristmasElf.class);
        new PetType("IronGolem", XMaterial.IRON_INGOT, "&7&oI like flowers", EntityType.IRON_GOLEM, PetIronGolem.class);
        new PetType("Snowman", XMaterial.SNOWBALL, "&7&oPew pew pew", EntityType.SNOWMAN, PetSnowman.class);
        new PetType("Villager", XMaterial.EMERALD, "&7&oHmmmmmmmmm", EntityType.VILLAGER, PetVillager.class);
        new PetType("Bat", XMaterial.COAL, "&7&oI prefer dark areas", EntityType.BAT, PetBat.class);
        new PetType("Sheep", XMaterial.WHITE_WOOL, "&7&oBaaaa, baa", EntityType.SHEEP, PetSheep.class);
        new PetType("Wither", XMaterial.WITHER_SKELETON_SKULL, "&7&oWatch out for me..", EntityType.WITHER, PetWither.class);
        /*Slime disabled because its just constantly jumping in one direction instead of following the player*/
        /*new PetType("Slime", XMaterial.SLIME_BALL, "&7&oSquish...", EntityType.SLIME, PetSlime.class);*/
        new PetType("Silverfish", XMaterial.GRAY_DYE, "&7&oLurking in the walls...", EntityType.SILVERFISH, PetSilverfish.class);
        new PetType("Blaze", XMaterial.BLAZE_ROD, "&7&oFlying and hot!", EntityType.BLAZE, PetBlaze.class);
        new PetType("Creeper", XMaterial.GUNPOWDER, "&7&oLikes blowing up your favorite Stuff...", EntityType.CREEPER, PetCreeper.class);
        new PetType("Enderman", XMaterial.ENDER_PEARL, "&7&oDont look at it or it will hunt you!", EntityType.ENDERMAN, PetEnderman.class);
        new PetType("Skeleton", XMaterial.BOW, "&7&oWatch out, it will try to shoot you!", EntityType.SKELETON, PetSkeleton.class);
        new PetType("Zombie", XMaterial.ROTTEN_FLESH, "&7&oQuick! Hide your Villagers!", EntityType.ZOMBIE, PetZombie.class);
        
        if (serverVersion.isAtLeast(ServerVersion.v1_19_R1)) {
            new PetType("Frog", XMaterial.LILY_PAD, "&7&oDoesn't like Fireflies!", EntityType.FROG, PetFrog.class);
            new PetType("Warden", XMaterial.SCULK_SHRIEKER, "&7&oThe scariest Mob in Minecraft!", EntityType.WARDEN, PetWarden.class);
            new PetType("Allay", XMaterial.ALLAY_SPAWN_EGG, "&7&oA new Friend!", EntityType.ALLAY, PetAllay.class);
        }
        
        if (serverVersion.isAtLeast(ServerVersion.v1_17_R1)) {
            new PetType("Axolotl", XMaterial.AXOLOTL_BUCKET, "&7&oSooo Cute!", EntityType.AXOLOTL, PetAxolotl.class);
            /*Temporarily using WHEAT as material for the Goat until https://minecraft.fandom.com/wiki/Goat_Horn comes to Java edition*/
            new PetType("Goat", XMaterial.WHEAT, "&7&oBAAAA!", EntityType.GOAT, PetGoat.class);
        }
        
        if (serverVersion.isAtLeast(ServerVersion.v1_16_R3)) {
            new PetType("Piglin", XMaterial.GOLD_INGOT, "&7&oDeals with Gold!", EntityType.PIGLIN, PetPiglin.class);
            new PetType("Bee", XMaterial.HONEYCOMB, "&7&o*bzzzz* *bzzzz*", EntityType.BEE, PetBee.class);
            new PetType("Panda", XMaterial.BAMBOO, "&7&oLikes Bamboo!", EntityType.PANDA, PetPanda.class);
            new PetType("Fox", XMaterial.SWEET_BERRIES, "&7&oWhat does the fox say?", EntityType.FOX, PetFox.class);
            new PetType("Kitty", XMaterial.TROPICAL_FISH, "&7&oMeoooow", EntityType.CAT, PetKitty.class);
            new PetType("Horse", XMaterial.LEATHER_HORSE_ARMOR, "&7&o*fhrrrrhh*", EntityType.HORSE, PetHorse.class);
        } else {
            new PetType("Kitty", XMaterial.TROPICAL_FISH, "&7&oMeoooow", EntityType.OCELOT, PetKitty.class);
            new PetType("Horse", XMaterial.LEATHER, "&7&o*fhrrrrhh*", EntityType.HORSE, PetHorse.class);
        }

        if (serverVersion.isAtLeast(ServerVersion.v1_12_R1)) {
            new PetType("PolarBear", XMaterial.SNOW_BLOCK, "&7&oI prefer cold areas", GetForVersion.entityType("POLAR_BEAR"), PetPolarBear.class);
            new PetType("Llama", XMaterial.RED_WOOL, "&7&oNeed me to carry anything?", GetForVersion.entityType("LLAMA"), PetLlama.class);
            new PetType("Parrot", XMaterial.COOKIE, "&7&oPolly want a cracker?", GetForVersion.entityType("PARROT"), PetParrot.class);
            /*Vex disabled because its just not following the player at all (Besides teleport)*/
            /*new PetType("Vex", XMaterial.IRON_SWORD, "&7&oYAAHH Ehehhehe!", GetForVersion.entityType("VEX"), PetVex.class);*/
        }
    }
}
