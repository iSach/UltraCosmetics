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

    private PetType(String permission, String configName, XMaterial material, String defaultDesc, EntityType entityType, Class<? extends Pet> clazz, ServerVersion baseVersion) {
        super(Category.PETS, configName, permission, defaultDesc, material, entityType, clazz, baseVersion);

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

        new PetType("ultracosmetics.pets.piggy", "Piggy", XMaterial.PORKCHOP, "&7&oOink! Oink!", EntityType.PIG, PetPiggy.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.easterbunny", "EasterBunny", XMaterial.CARROT, "&7&oIs it Easter yet?", EntityType.RABBIT, PetEasterBunny.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.cow", "Cow", XMaterial.MILK_BUCKET, "&7&oMoooo!", EntityType.COW, PetCow.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.mooshroom", "Mooshroom", XMaterial.RED_MUSHROOM, "&7&oMoooo!", EntityType.MUSHROOM_COW, PetMooshroom.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.dog", "Dog", XMaterial.BONE, "&7&oWoof!", EntityType.WOLF, PetDog.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.chick", "Chick", XMaterial.EGG, "&7&oBwaaaaaaak!!", EntityType.CHICKEN, PetChick.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.pumpling", "Pumpling", XMaterial.PUMPKIN, "&7&oJust a little floating pumpkin", EntityType.ZOMBIE, UltraCosmeticsData.get().getVersionManager().getPets().getPumplingClass(), ServerVersion.earliest());
        new PetType("ultracosmetics.pets.christmaself", "ChristmasElf", XMaterial.BEACON, "&7&oI can make presents for you!", EntityType.VILLAGER, PetChristmasElf.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.irongolem", "IronGolem", XMaterial.IRON_INGOT, "&7&oI like flowers", EntityType.IRON_GOLEM, PetIronGolem.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.snowman", "Snowman", XMaterial.SNOWBALL, "&7&oPew pew pew", EntityType.SNOWMAN, PetSnowman.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.villager", "Villager", XMaterial.EMERALD, "&7&oHmmmmmmmmm", EntityType.VILLAGER, PetVillager.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.bat", "Bat", XMaterial.COAL, "&7&oI prefer dark areas", EntityType.BAT, PetBat.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.sheep", "Sheep", XMaterial.WHITE_WOOL, "&7&oBaaaa, baa", EntityType.SHEEP, PetSheep.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.wither", "Wither", XMaterial.WITHER_SKELETON_SKULL, "&7&oWatch out for me..", EntityType.WITHER, PetWither.class, ServerVersion.earliest());
        /*Slime disabled because its just constantly jumping in one direction instead of following the player*/
        /*new PetType("ultracosmetics.pets.slime", "Slime", XMaterial.SLIME_BALL, "&7&oSquish...", EntityType.SLIME, PetSlime.class, ServerVersion.earliest());*/
        new PetType("ultracosmetics.pets.silverfish", "Silverfish", XMaterial.GRAY_DYE, "&7&oLurking in the walls...", EntityType.SILVERFISH, PetSilverfish.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.horse", "Horse", XMaterial.LEATHER_HORSE_ARMOR, "&7&o*fhrrrrhh*", EntityType.HORSE, PetHorse.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.blaze", "Blaze", XMaterial.BLAZE_ROD, "&7&oFlying and hot!", EntityType.BLAZE, PetBlaze.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.creeper", "Creeper", XMaterial.GUNPOWDER, "&7&oLikes blowing up your favorite Stuff...", EntityType.CREEPER, PetCreeper.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.enderman", "Enderman", XMaterial.ENDER_PEARL, "&7&oDont look at it or it will hunt you!", EntityType.ENDERMAN, PetEnderman.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.skeleton", "Skeleton", XMaterial.BOW, "&7&oWatch out, it will try to shoot you!", EntityType.SKELETON, PetSkeleton.class, ServerVersion.earliest());
        new PetType("ultracosmetics.pets.zombie", "Zombie", XMaterial.ROTTEN_FLESH, "&7&oQuick! Hide your Villagers!", EntityType.ZOMBIE, PetZombie.class, ServerVersion.earliest());
        
        if (serverVersion.isAtLeast(ServerVersion.v1_17_R1)) {
            /*Axolotl disabled because it walks way too fast*/
            /*new PetType("ultracosmetics.pets.axolotl", "Axolotl", XMaterial.AXOLOTL_BUCKET, "&7&oSooo Cute!", EntityType.AXOLOTL, PetAxolotl.class, ServerVersion.v1_17_R1);*/
            /*Temporarily using WHEAT as material for the Goat until https://minecraft.fandom.com/wiki/Goat_Horn comes to Java edition*/
            new PetType("ultracosmetics.pets.goat", "Goat", XMaterial.WHEAT, "&7&oBAAAA!", EntityType.GOAT, PetGoat.class, ServerVersion.v1_17_R1);
        }
        
        if (serverVersion.isAtLeast(ServerVersion.v1_16_R3)) {
            new PetType("ultracosmetics.pets.piglin", "Piglin", XMaterial.GOLD_INGOT, "&7&oDeals with Gold!", EntityType.PIGLIN, PetPiglin.class, ServerVersion.v1_16_R3);
        }
        
        if (serverVersion.isAtLeast(ServerVersion.v1_15_R1)) {
            new PetType("ultracosmetics.pets.bee", "Bee", XMaterial.HONEYCOMB, "&7&o*bzzzz* *bzzzz*", EntityType.BEE, PetBee.class, ServerVersion.v1_15_R1);
        }
        
        if (serverVersion.isAtLeast(ServerVersion.v1_14_R1)) {
            new PetType("ultracosmetics.pets.panda", "Panda", XMaterial.BAMBOO, "&7&oLikes Bamboo!", EntityType.PANDA, PetPanda.class, ServerVersion.v1_14_R1);
            new PetType("ultracosmetics.pets.fox", "Fox", XMaterial.SWEET_BERRIES, "&7&oWhat does the fox say?", EntityType.FOX, PetFox.class, ServerVersion.v1_14_R1);
        }
        if (serverVersion.isAtLeast(ServerVersion.v1_14_R1)) {
            new PetType("ultracosmetics.pets.kitty", "Kitty", XMaterial.TROPICAL_FISH, "&7&oMeoooow", EntityType.CAT, PetKitty.class, ServerVersion.v1_14_R1);
        } else {
            new PetType("ultracosmetics.pets.kitty", "Kitty", XMaterial.TROPICAL_FISH, "&7&oMeoooow", EntityType.OCELOT, PetKitty.class, ServerVersion.earliest());
        }

        if (serverVersion.isAtLeast(ServerVersion.v1_12_R1)) {
            new PetType("ultracosmetics.pets.polarbear", "PolarBear", XMaterial.SNOW_BLOCK, "&7&oI prefer cold areas", GetForVersion.entityType("POLAR_BEAR"), PetPolarBear.class, ServerVersion.v1_12_R1);
            new PetType("ultracosmetics.pets.llama", "Llama", XMaterial.RED_WOOL, "&7&oNeed me to carry anything?", GetForVersion.entityType("LLAMA"), PetLlama.class, ServerVersion.v1_12_R1);
            new PetType("ultracosmetics.pets.parrot", "Parrot", XMaterial.COOKIE, "&7&oPolly want a cracker?", GetForVersion.entityType("PARROT"), PetParrot.class, ServerVersion.v1_12_R1);
            /*Vex disabled because its just not following the player at all (Besides teleport)*/
            /*new PetType("ultracosmetics.pets.vex", "Vex", XMaterial.IRON_SWORD, "&7&oYAAHH Ehehhehe!", GetForVersion.entityType("VEX"), PetVex.class, ServerVersion.v1_12_R1);*/
        }
    }
}
