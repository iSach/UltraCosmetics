package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.morphs.*;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sacha on 19/12/15.
 */
public class MorphType extends CosmeticMatType<Morph> {

    private final static List<MorphType> ENABLED = new ArrayList<>();
    private final static List<MorphType> VALUES = new ArrayList<>();

    public static List<MorphType> enabled() {
        return ENABLED;
    }

    public static List<MorphType> values() {
        return VALUES;
    }

    public static MorphType valueOf(String s) {
        for (MorphType morphType : VALUES) {
            if (morphType.getConfigName().equalsIgnoreCase(s)) return morphType;
        }
        return null;
    }

    public static MorphType getByName(String s) {
        try {
            return VALUES.stream().filter(value -> value.getName().equalsIgnoreCase(s)).findFirst().get();
        } catch (Exception exc) {
            return null;
        }
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    public static final MorphType BAT = new MorphType("ultracosmetics.morphs.bat", "Bat", Material.COAL, (byte) 0, DisguiseType.BAT, "&7&oBecome a bat!", MorphBat.class);
    public static final MorphType BLAZE = new MorphType("ultracosmetics.morphs.blaze", "Blaze", Material.BLAZE_POWDER, (byte) 0, DisguiseType.BLAZE, "&7&oIt might be hot on there..", MorphBlaze.class);
    public static final MorphType CHICKEN = new MorphType("ultracosmetics.morphs.chicken", "Chicken", Material.EGG, (byte) 0, DisguiseType.CHICKEN, "&7&oBecome a chicken!", MorphChicken.class);
    public static final MorphType PIG = new MorphType("ultracosmetics.morphs.pig", "Pig", Material.PORK, (byte) 0, DisguiseType.PIG, "&7&oMust hold your carrot!", MorphPig.class);
    public static final MorphType ENDERMAN = new MorphType("ultracosmetics.morphs.enderman", "Enderman", Material.ENDER_PEARL, (byte) 0, DisguiseType.ENDERMAN, "&7&oI go from there to\n" + "&7&othere, then you lost me..", MorphEnderman.class);
    public static final MorphType SLIME = new MorphType("ultracosmetics.morphs.slime", "Slime", Material.SLIME_BALL, (byte) 0, DisguiseType.SLIME, "&7&oSplat splat!", MorphSlime.class);
    public static final MorphType CREEPER = new MorphType("ultracosmetics.morphs.creeper", "Creeper", Material.SULPHUR, (byte) 0, DisguiseType.CREEPER, "&7&oHey What'sssssss up?", MorphCreeper.class);
    public static final MorphType WITHERSKELETON = new MorphType("ultracosmetics.morphs.witherskeleton", "WitherSkeleton", Material.SKULL_ITEM, (byte) 1, DisguiseType.WITHER_SKELETON, "&7&oJust a regular skeleton..\n&7&obut from the Nether!", MorphWitherSkeleton.class);
    public static final MorphType SNOWNMAN = new MorphType("ultracosmetics.morphs.snowman", "Snowman", Material.SNOW_BALL, (byte) 0, DisguiseType.SNOWMAN, "&7&oBecome Olaf!", MorphSnowman.class);
    public static final MorphType ELDERGUARDIAN = new MorphType("ultracosmetics.morphs.elderguardian", "ElderGuardian", Material.PRISMARINE_CRYSTALS, (byte) 0, DisguiseType.valueOf("ELDER_GUARDIAN"), "&7&oBecome an Elder Guardian!!", UltraCosmeticsData.get().getVersionManager().getMorphs().getElderGuardianClass());

    /**
     * Disguise Type of the morph.
     */
    private DisguiseType disguiseType;

    private MorphType(String permission, String configName, Material material, byte data, DisguiseType disguiseType, String defaultDesc, Class<? extends Morph> clazz) {
        super(Category.MORPHS, configName, permission, defaultDesc, material, data, clazz);
        this.disguiseType = disguiseType;

        VALUES.add(this);
    }

    /**
     * Get the skill message.
     *
     * @return The skill message of the morph.
     */
    public String getSkill() {
        return MessageManager.getMessage("Morphs." + getConfigName() + ".skill");
    }

    /**
     * Get the morph Disguise Type.
     *
     * @return
     */
    public DisguiseType getDisguiseType() {
        return disguiseType;
    }
}
