package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hat types.
 * 
 * @author 	iSach
 * @since 	10-15-2015
 */
public class HatType extends CosmeticMatType<Hat> {

    private final static List<HatType> ENABLED = new ArrayList<>();
    private final static List<HatType> VALUES = new ArrayList<>();

    public static List<HatType> enabled() {
        return ENABLED;
    }

    public static List<HatType> values() {
        return VALUES;
    }

    public static HatType valueOf(String s) {
        for (HatType hat : VALUES) {
            if (hat.getConfigName().equalsIgnoreCase(s)) return hat;
        }
        return null;
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    public static final HatType ASTRONAUT = new HatType("M2U4YWFkNjczMTU3YzkyMzE3YTg4YjFmODZmNTI3MWYxY2Q3Mzk3ZDdmYzhlYzMyODFmNzMzZjc1MTYzNCJ9fX0=", "Astronaut", "&7&oHouston, we have got a problem.");
    public static final HatType SCARED = new HatType("NjM2ZTI2YzQ0NjU5ZTgxNDhlZDU4YWE3OWU0ZDYwZGI1OTVmNDI2NDQyMTE2ZjgxYjU0MTVjMjQ0NmVkOCJ9fX0=", "Scared", "&7&oOh gawd, that scared me!");
    public static final HatType ANGEL = new HatType("M2UxZGViYzczMjMxZjhlZDRiNjlkNWMzYWMxYjFmMThmMzY1NmE4OTg4ZTIzZjJlMWJkYmM0ZTg1ZjZkNDZhIn19fQ==", "Angel", "&7&oDid it hurt when you fell from heaven?");
    public static final HatType EMBARASSED = new HatType("ZjcyMGRmOTExYzA1MjM3NzA2NTQwOGRiNzhhMjVjNjc4Zjc5MWViOTQ0YzA2MzkzNWFlODZkYmU1MWM3MWIifX19", "Embarassed", "&7&oI am kinda embarassed by that.");
    public static final HatType KISSY = new HatType("NTQ1YmQxOGEyYWFmNDY5ZmFkNzJlNTJjZGU2Y2ZiMDJiZmJhYTViZmVkMmE4MTUxMjc3Zjc3OWViY2RjZWMxIn19fQ==", "Kissy", "&7&oWanna kiss?");
    public static final HatType SAD = new HatType("MTQ5NjhhYzVhZjMxNDY4MjZmYTJiMGQ0ZGQxMTRmZGExOTdmOGIyOGY0NzUwNTUzZjNmODg4MzZhMjFmYWM5In19fQ==", "Sad", "&7&oI am so sad.");
    public static final HatType COOL = new HatType("ODY4ZjRjZWY5NDlmMzJlMzNlYzVhZTg0NWY5YzU2OTgzY2JlMTMzNzVhNGRlYzQ2ZTViYmZiN2RjYjYifX19", "Cool", "&7&oI am such a cool guy.");
    public static final HatType SURPRISED = new HatType("YmMyYjliOWFlNjIyYmQ2OGFkZmY3MTgwZjgyMDZlYzQ0OTRhYmJmYTEzMGU5NGE1ODRlYzY5MmU4OTg0YWIyIn19fQ==", "Surprised", "&7&oWow, did not expect that!");
    public static final HatType DEAD = new HatType("YjM3MWU0ZTFjZjZhMWEzNmZkYWUyNzEzN2ZkOWI4NzQ4ZTYxNjkyOTk5MjVmOWFmMmJlMzAxZTU0Mjk4YzczIn19fQ==", "Dead", "&7&ogot rekt");
    public static final HatType CRYING = new HatType("MWYxYjg3NWRlNDljNTg3ZTNiNDAyM2NlMjRkNDcyZmYyNzU4M2ExZjA1NGYzN2U3M2ExMTU0YjViNTQ5OCJ9fX0=", "Crying", "&7&oi cri evrytiem");
    public static final HatType BIGSMILE = new HatType("NTA1OWQ1OWViNGU1OWMzMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzEwIn19fQ==", "BigSmile", "&7&oUh, because I am really happy!");
    public static final HatType WINK = new HatType("ZjRlYTJkNmY5MzlmZWZlZmY1ZDEyMmU2M2RkMjZmYThhNDI3ZGY5MGIyOTI4YmMxZmE4OWE4MjUyYTdlIn19fQ==", "Wink", "&7&oYou know what I mean ;)");
    public static final HatType DERP = new HatType("M2JhYWJlNzI0ZWFlNTljNWQxM2Y0NDJjN2RjNWQyYjFjNmI3MGMyZjgzMzY0YTQ4OGNlNTk3M2FlODBiNGMzIn19fQ==", "Derp", "&7&oDerp Derp Derping all around");
    public static final HatType SMILE = new HatType("NTJlOTgxNjVkZWVmNGVkNjIxOTUzOTIxYzFlZjgxN2RjNjM4YWY3MWMxOTM0YTQyODdiNjlkN2EzMWY2YjgifX19", "Smile", "&7&oUh, because I am happy");
    public static final HatType IRON = new HatType("YmJhODQ1OTE0NWQ4M2ZmYzQ0YWQ1OGMzMjYwZTc0Y2E1YTBmNjM0YzdlZWI1OWExYWQzMjM0ODQ5YzkzM2MifX19", "Iron", "&7&oAs hard as iron!");
    public static final HatType GOLD = new HatType("YjZkMWNlNjk3ZTlkYmFhNGNjZjY0MjUxNmFhYTU5ODEzMzJkYWMxZDMzMWFmZWUyZWUzZGNjODllZmRlZGIifX19", "Gold", "&7&oMy precious!");
    public static final HatType DIAMOND = new HatType("YzAxNDYxOTczNjM0NTI1MTk2ZWNjNzU3NjkzYjE3MWFkYTRlZjI0YWE5MjgzNmY0MmVhMTFiZDc5YzNhNTAyZCJ9fX0=", "Diamond", "&7&oThis is really strong!");
    public static final HatType PISTON = new HatType("YWE4NjhjZTkxN2MwOWFmOGU0YzM1MGE1ODA3MDQxZjY1MDliZjJiODlhY2E0NWU1OTFmYmJkN2Q0YjExN2QifX19", "Piston", "&7&oHave you got the redstone?");
    public static final HatType COMMANDBLOCK = new HatType("ODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ==", "CommandBlock", "&7&oControl the world with it!");
    public static final HatType MUSIC = new HatType("NGNlZWI3N2Q0ZDI1NzI0YTljYWYyYzdjZGYyZDg4Mzk5YjE0MTdjNmI5ZmY1MjEzNjU5YjY1M2JlNDM3NmUzIn19fQ==", "Music", "&7&oYou are so musical.");
    public static final HatType SQUID = new HatType("MDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ==", "Squid", "&7&oBloop Bloop!");
    public static final HatType CHICKEN = new HatType("MTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ==", "Chicken", "&7&oBwwaaaaaaaaaaaakkkkk!");
    public static final HatType PIG = new HatType("NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0=", "Pig", "&7&oOink Oink!");
    public static final HatType BLAZE = new HatType("Yjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ==", "Blaze", "&7&oWatch out for the fire!");
    public static final HatType SHEEP = new HatType("ZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19", "Sheep", "&7&oBaaaa, baa");
    public static final HatType GOLEM = new HatType("ODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19", "Golem", "&7&oI am your guard.");
    public static final HatType ENDERMAN = new HatType("N2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=", "Enderman", "&7&oNow I am here, now I am there.");
    public static final HatType MARIO = new HatType("ZGJhOGQ4ZTUzZDhhNWE3NTc3MGI2MmNjZTczZGI2YmFiNzAxY2MzZGU0YTliNjU0ZDIxM2Q1NGFmOTYxNSJ9fX0=", "Mario", "&7&oIt is me! Mario!");
    public static final HatType LUIGI = new HatType("ZmYxNTMzODcxZTQ5ZGRhYjhmMWNhODJlZGIxMTUzYTVlMmVkMzc2NGZkMWNlMDI5YmY4MjlmNGIzY2FhYzMifX19", "Luigi", "&7&oLuigi time!");
    public static final HatType BATMAN = new HatType("ZjI1NmY3MTczNWVmNDU4NTgxYzlkYWNmMzk0MTg1ZWVkOWIzM2NiNmVjNWNkNTk0YTU3MTUzYThiNTY2NTYwIn19fQ==", "Batman", "&7&oI am batman!");
    public static final HatType CHEST = new HatType("NmY2OGQ1MDliNWQxNjY5Yjk3MWRkMWQ0ZGYyZTQ3ZTE5YmNiMWIzM2JmMWE3ZmYxZGRhMjliZmM2ZjllYmYifX19", "Chest", "&7&oOpen, and close");
    public static final HatType SKULL = new HatType("MTFmNTRmZjliYjQyODUxOTEyYWE4N2ExYmRhNWI3Y2Q5ODE0Y2NjY2ZiZTIyNWZkZGE4ODdhZDYxODBkOSJ9fX0=", "Skull", "&7&oWho iss headless now?");
    public static final HatType GHOST = new HatType("NjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0=", "Ghost", "&7&o2spooky4u");
    public static final HatType JACKOLANTERN = new HatType("MDI4OWQ0YjRjOTYyOTU5MTVmMDY4Yjk5YzI3ZDM5NDI3M2Y5ZjI2NGZjOTY4YzVkNWM0N2RmMmY1YmUyIn19fQ==", "JackOLantern", "&7&oA little pumkin");
    public static final HatType SCARYCLOW = new HatType("ODZkYmMxZGViYzU3NDM4YTVkZTRiYTkxNTE1MTM4MmFiYzNkOGYxMzE4ZTJhMzVlNzhkZmIzMGYwNGJjNDY3In19fQ==", "ScaryClown", "&7&oHope you are not scared of clowns.");
    public static final HatType SANTA = new HatType("MmQ2MWNjYmZkY2RmODk0MWFkYWY3NmM2YzBlMDE4MmQyYzhiYmI1ZGMxOGYzNzQ4OTU2NTJiYzY2MWI2ZWQifX19", "Santa", "&7&oOh oh oh! Merry Christmas!");
    public static final HatType SNOWMAN = new HatType("OThlMzM0ZTRiZWUwNDI2NDc1OWE3NjZiYzE5NTVjZmFmM2Y1NjIwMTQyOGZhZmVjOGQ0YmYxYmIzNmFlNiJ9fX0=", "Snowman", "&7&oI don't have a skull.. or bones");
    public static final HatType PRESENT = new HatType("ZjBhZmE0ZmZmZDEwODYzZTc2YzY5OGRhMmM5YzllNzk5YmNmOWFiOWFhMzdkODMxMjg4MTczNDIyNWQzY2EifX19", "Present", "&7&oFrom Santa, to you!");
    public static final HatType ELF = new HatType("ODJhYjZjNzljNjNiODMzNGIyYzAzYjZmNzM2YWNmNjFhY2VkNWMyNGYyYmE3MmI3NzdkNzdmMjhlOGMifX19", "Elf", "&7&oI work for Santa!");
    public static final HatType BEDROCK = new HatType("MzZkMWZhYmRmM2UzNDI2NzFiZDlmOTVmNjg3ZmUyNjNmNDM5ZGRjMmYxYzllYThmZjE1YjEzZjFlN2U0OGI5In19fQ==", "Bedrock", "&7&oUnbreakable!");

    /**
     * STATIC list of all the enabled hats.
     */
    public static List<HatType> enabled = new ArrayList<>();

    /**
     * The HatType ItemStack
     */
    private ItemStack itemStack;

    HatType(String str, String configName, String defaultDesc) {
        super(Category.HATS, configName, "ultracosmetics.hats." + configName.toLowerCase(), defaultDesc, Material.SKULL_ITEM, (byte) 3, Hat.class);
        this.itemStack = ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + str, ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Hat");

        VALUES.add(this);
    }

    @Override
    public Hat equip(UltraPlayer player, UltraCosmetics ultraCosmetics) {
        Hat cosmetic = null;
        try {
            cosmetic = getClazz().getDeclaredConstructor(UltraCosmetics.class, UltraPlayer.class, HatType.class).newInstance(ultraCosmetics, player, this);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        cosmetic.equip();
        return cosmetic;
    }

    /**
     * Gets the HatType ItemStack.
     *
     * @return the HatType ItemStack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Gets the HatType Name (in Menu).
     *
     * @return the HatType Name (in Menu).
     */
    @Override
    public String getName() {
        return MessageManager.getMessage("Hats." + getConfigName() + ".Name");
    }
}