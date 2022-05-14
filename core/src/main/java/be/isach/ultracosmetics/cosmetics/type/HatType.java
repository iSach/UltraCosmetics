package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hat types.
 *
 * @author iSach
 * @since 10-15-2015
 */
public class HatType extends CosmeticType<Hat> {

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

    /**
     * The HatType ItemStack
     */
    private final ItemStack itemStack;

    private HatType(String str, String configName, String defaultDesc) {
        super(Category.HATS, configName, defaultDesc, XMaterial.PLAYER_HEAD, Hat.class);
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
            return null;
        }
        cosmetic.equip();
        return cosmetic;
    }

    /**
     * Gets the HatType ItemStack.
     *
     * @return the HatType ItemStack.
     */
    @Override
    public ItemStack getItemStack() {
        return itemStack.clone();
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

    public static void register() {
        new HatType("M2U4YWFkNjczMTU3YzkyMzE3YTg4YjFmODZmNTI3MWYxY2Q3Mzk3ZDdmYzhlYzMyODFmNzMzZjc1MTYzNCJ9fX0=", "Astronaut", "&7&oHouston, we have got a problem.");
        new HatType("NjM2ZTI2YzQ0NjU5ZTgxNDhlZDU4YWE3OWU0ZDYwZGI1OTVmNDI2NDQyMTE2ZjgxYjU0MTVjMjQ0NmVkOCJ9fX0=", "Scared", "&7&oOh gawd, that scared me!");
        new HatType("M2UxZGViYzczMjMxZjhlZDRiNjlkNWMzYWMxYjFmMThmMzY1NmE4OTg4ZTIzZjJlMWJkYmM0ZTg1ZjZkNDZhIn19fQ==", "Angel", "&7&oDid it hurt when you fell from heaven?");
        new HatType("ZjcyMGRmOTExYzA1MjM3NzA2NTQwOGRiNzhhMjVjNjc4Zjc5MWViOTQ0YzA2MzkzNWFlODZkYmU1MWM3MWIifX19", "Embarassed", "&7&oI am kinda embarassed by that.");
        new HatType("NTQ1YmQxOGEyYWFmNDY5ZmFkNzJlNTJjZGU2Y2ZiMDJiZmJhYTViZmVkMmE4MTUxMjc3Zjc3OWViY2RjZWMxIn19fQ==", "Kissy", "&7&oWanna kiss?");
        new HatType("MTQ5NjhhYzVhZjMxNDY4MjZmYTJiMGQ0ZGQxMTRmZGExOTdmOGIyOGY0NzUwNTUzZjNmODg4MzZhMjFmYWM5In19fQ==", "Sad", "&7&oI am so sad.");
        new HatType("ODY4ZjRjZWY5NDlmMzJlMzNlYzVhZTg0NWY5YzU2OTgzY2JlMTMzNzVhNGRlYzQ2ZTViYmZiN2RjYjYifX19", "Cool", "&7&oI am such a cool guy.");
        new HatType("YmMyYjliOWFlNjIyYmQ2OGFkZmY3MTgwZjgyMDZlYzQ0OTRhYmJmYTEzMGU5NGE1ODRlYzY5MmU4OTg0YWIyIn19fQ==", "Surprised", "&7&oWow, did not expect that!");
        new HatType("YjM3MWU0ZTFjZjZhMWEzNmZkYWUyNzEzN2ZkOWI4NzQ4ZTYxNjkyOTk5MjVmOWFmMmJlMzAxZTU0Mjk4YzczIn19fQ==", "Dead", "&7&ogot rekt");
        new HatType("MWYxYjg3NWRlNDljNTg3ZTNiNDAyM2NlMjRkNDcyZmYyNzU4M2ExZjA1NGYzN2U3M2ExMTU0YjViNTQ5OCJ9fX0=", "Crying", "&7&oi cri evrytiem");
        new HatType("NTA1OWQ1OWViNGU1OWMzMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzEwIn19fQ==", "BigSmile", "&7&oUh, because I am really happy!");
        new HatType("ZjRlYTJkNmY5MzlmZWZlZmY1ZDEyMmU2M2RkMjZmYThhNDI3ZGY5MGIyOTI4YmMxZmE4OWE4MjUyYTdlIn19fQ==", "Wink", "&7&oYou know what I mean ;)");
        new HatType("M2JhYWJlNzI0ZWFlNTljNWQxM2Y0NDJjN2RjNWQyYjFjNmI3MGMyZjgzMzY0YTQ4OGNlNTk3M2FlODBiNGMzIn19fQ==", "Derp", "&7&oDerp Derp Derping all around");
        new HatType("NTJlOTgxNjVkZWVmNGVkNjIxOTUzOTIxYzFlZjgxN2RjNjM4YWY3MWMxOTM0YTQyODdiNjlkN2EzMWY2YjgifX19", "Smile", "&7&oUh, because I am happy");
        new HatType("YmJhODQ1OTE0NWQ4M2ZmYzQ0YWQ1OGMzMjYwZTc0Y2E1YTBmNjM0YzdlZWI1OWExYWQzMjM0ODQ5YzkzM2MifX19", "Iron", "&7&oAs hard as iron!");
        new HatType("YjZkMWNlNjk3ZTlkYmFhNGNjZjY0MjUxNmFhYTU5ODEzMzJkYWMxZDMzMWFmZWUyZWUzZGNjODllZmRlZGIifX19", "Gold", "&7&oMy precious!");
        new HatType("YzAxNDYxOTczNjM0NTI1MTk2ZWNjNzU3NjkzYjE3MWFkYTRlZjI0YWE5MjgzNmY0MmVhMTFiZDc5YzNhNTAyZCJ9fX0=", "Diamond", "&7&oThis is really strong!");
        new HatType("YWE4NjhjZTkxN2MwOWFmOGU0YzM1MGE1ODA3MDQxZjY1MDliZjJiODlhY2E0NWU1OTFmYmJkN2Q0YjExN2QifX19", "Piston", "&7&oHave you got the redstone?");
        new HatType("ODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ==", "CommandBlock", "&7&oControl the world with it!");
        new HatType("NGNlZWI3N2Q0ZDI1NzI0YTljYWYyYzdjZGYyZDg4Mzk5YjE0MTdjNmI5ZmY1MjEzNjU5YjY1M2JlNDM3NmUzIn19fQ==", "Music", "&7&oYou are so musical.");
        new HatType("MDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ==", "Squid", "&7&oBloop Bloop!");
        new HatType("MTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ==", "Chicken", "&7&oBwwaaaaaaaaaaaakkkkk!");
        new HatType("NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0=", "Pig", "&7&oOink Oink!");
        new HatType("Yjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ==", "Blaze", "&7&oWatch out for the fire!");
        new HatType("ZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19", "Sheep", "&7&oBaaaa, baa");
        new HatType("ODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19", "Golem", "&7&oI am your guard.");
        new HatType("N2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=", "Enderman", "&7&oNow I am here, now I am there.");
        new HatType("ZGJhOGQ4ZTUzZDhhNWE3NTc3MGI2MmNjZTczZGI2YmFiNzAxY2MzZGU0YTliNjU0ZDIxM2Q1NGFmOTYxNSJ9fX0=", "Mario", "&7&oIt is me! Mario!");
        new HatType("ZmYxNTMzODcxZTQ5ZGRhYjhmMWNhODJlZGIxMTUzYTVlMmVkMzc2NGZkMWNlMDI5YmY4MjlmNGIzY2FhYzMifX19", "Luigi", "&7&oLuigi time!");
        new HatType("ZjI1NmY3MTczNWVmNDU4NTgxYzlkYWNmMzk0MTg1ZWVkOWIzM2NiNmVjNWNkNTk0YTU3MTUzYThiNTY2NTYwIn19fQ==", "Batman", "&7&oI am batman!");
        new HatType("NmY2OGQ1MDliNWQxNjY5Yjk3MWRkMWQ0ZGYyZTQ3ZTE5YmNiMWIzM2JmMWE3ZmYxZGRhMjliZmM2ZjllYmYifX19", "Chest", "&7&oOpen, and close");
        new HatType("MTFmNTRmZjliYjQyODUxOTEyYWE4N2ExYmRhNWI3Y2Q5ODE0Y2NjY2ZiZTIyNWZkZGE4ODdhZDYxODBkOSJ9fX0=", "Skull", "&7&oWho iss headless now?");
        new HatType("NjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0=", "Ghost", "&7&o2spooky4u");
        new HatType("MDI4OWQ0YjRjOTYyOTU5MTVmMDY4Yjk5YzI3ZDM5NDI3M2Y5ZjI2NGZjOTY4YzVkNWM0N2RmMmY1YmUyIn19fQ==", "JackOLantern", "&7&oA little pumkin");
        new HatType("ODZkYmMxZGViYzU3NDM4YTVkZTRiYTkxNTE1MTM4MmFiYzNkOGYxMzE4ZTJhMzVlNzhkZmIzMGYwNGJjNDY3In19fQ==", "ScaryClown", "&7&oHope you are not scared of clowns.");
        new HatType("MmQ2MWNjYmZkY2RmODk0MWFkYWY3NmM2YzBlMDE4MmQyYzhiYmI1ZGMxOGYzNzQ4OTU2NTJiYzY2MWI2ZWQifX19", "Santa", "&7&oOh oh oh! Merry Christmas!");
        new HatType("OThlMzM0ZTRiZWUwNDI2NDc1OWE3NjZiYzE5NTVjZmFmM2Y1NjIwMTQyOGZhZmVjOGQ0YmYxYmIzNmFlNiJ9fX0=", "Snowman", "&7&oI don't have a skull.. or bones");
        new HatType("ZjBhZmE0ZmZmZDEwODYzZTc2YzY5OGRhMmM5YzllNzk5YmNmOWFiOWFhMzdkODMxMjg4MTczNDIyNWQzY2EifX19", "Present", "&7&oFrom Santa, to you!");
        new HatType("ODJhYjZjNzljNjNiODMzNGIyYzAzYjZmNzM2YWNmNjFhY2VkNWMyNGYyYmE3MmI3NzdkNzdmMjhlOGMifX19", "Elf", "&7&oI work for Santa!");
        new HatType("MzZkMWZhYmRmM2UzNDI2NzFiZDlmOTVmNjg3ZmUyNjNmNDM5ZGRjMmYxYzllYThmZjE1YjEzZjFlN2U0OGI5In19fQ==", "Bedrock", "&7&oUnbreakable!");
        new HatType("ZjM0ODdkNDU3ZjkwNjJkNzg3YTNlNmNlMWM0NjY0YmY3NDAyZWM2N2RkMTExMjU2ZjE5YjM4Y2U0ZjY3MCJ9fX0=", "Bread", "&7&oFreshly baked");
        new HatType("OTU1ZDYxMWE4NzhlODIxMjMxNzQ5YjI5NjU3MDhjYWQ5NDI2NTA2NzJkYjA5ZTI2ODQ3YTg4ZTJmYWMyOTQ2In19fQ==", "Cheese", "&7&oSay cheese!");
        new HatType("MzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0=", "Pancakes", "&7&oBetter with syrup!");
        new HatType("ZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19", "Cake", "&7&oMmmm, cake.");
        new HatType("YjU5MmNmOWY0MmE1YThjOTk1OTY4NDkzZmRkMWIxMWUwYjY5YWFkNjQ3M2ZmNDUzODRhYmU1OGI3ZmM3YzcifX19", "Cookie", "&7&oChocolate chips included!");
        new HatType("NGNjM2Y3ODFjOTIzYTI4ODdmMTRjMWVlYTExMDUwMTY2OTY2ZjI2MDI1Nzg0MDFmMTQ1MWU2MDk3Yjk3OWRmIn19fQ==", "CandyCane", "&7&oA bit of tooth-breaking");
        new HatType("ODE5Zjk0OGQxNzcxOGFkYWNlNWRkNmUwNTBjNTg2MjI5NjUzZmVmNjQ1ZDcxMTNhYjk0ZDE3YjYzOWNjNDY2In19fQ==", "Chocolate", "&7&oDon't feed your dog with this!");
        new HatType("MWVkNTUyNjBkY2NjOGRhNTkzMzhjNzVlNDFkNTQ0YTJlMWU3ZGJlZjMxYTY5ZmU0MmMwMWIzMjk4YmYyZCJ9fX0=", "WhiteChocolate", "&7&oA bit more creamy");
        new HatType("Y2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "Apple", "&7&oNot the company");
        new HatType("YzNmZWQ1MTRjM2UyMzhjYTdhYzFjOTRiODk3ZmY2NzExYjFkYmU1MDE3NGFmYzIzNWM4ZjgwZDAyOSJ9fX0=", "Melon", "&7&oDon't smash it with your head");
        new HatType("ZmVjNDE1ZDcwMmYzMjkyYTgyZjE0NzFjODc5NGNmNjMxMjJkNDQ5ZDI4YWI4ODZkNGRjNThmYWZkNjYifX19", "CarvedPumpkin", "&7&oWho turned this thing off?");
        new HatType("Y2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0=", "Strawberry", "&7&oDoes this things grow in trees?");
        new HatType("ZTliMGU5NjljZjNmY2NlZDM2YjcxMjM1MGZmYjQ2ZDhlZDc2MWZlNWVmYjEwZTNiNmE5Nzk1ZTY2NTZkYTk3In19fQ==", "Coconut", "&7&oParadise");
        new HatType("OThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0=", "Taco", "&7&oLet's go for some tacos!");
        new HatType("ZTdiYTIyZDVkZjIxZTgyMWE2ZGU0YjhjOWQzNzNhM2FhMTg3ZDhhZTc0ZjI4OGE4MmQyYjYxZjI3MmU1In19fQ==", "Bacon", "&7&oEverything's better with bacon.");
        new HatType("YTBlYWNhYzQxYTllYWYwNTEzNzZlZjJmOTU5NzAxZTFiYmUxYmY0YWE2NzE1YWRjMzRiNmRjMjlhMTNlYTkifX19", "Fries", "&7&oHamburger's best friend");
        new HatType("YTZlZjFjMjVmNTE2ZjJlN2Q2Zjc2Njc0MjBlMzNhZGNmM2NkZjkzOGNiMzdmOWE0MWE4YjM1ODY5ZjU2OWIifX19", "Hamburger", "&7&oAre this things made with real meat?");
        new HatType("MTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ==", "Popcorn", "&7&oBring some to the discussion");
        new HatType("ZDA3YjhjNTFhY2VjMmE1MDhiYjJmYTY1MmZiNmU0YTA4YjE5NDg1MTU5YTA5OWY1OTgyY2NiODhkZjFmZTI3ZSJ9fX0=", "WhiteDonut", "&7&oSo much pleasure in such a small thing");
        new HatType("ODM3YzliODJiMTg2NjU2ZTlmNjM2M2EyYTFjNmE0YjViOTNjZmE5ZWY0ZGFkNmYxNmI5NGViYjVlMzYyNjc4In19fQ==", "PinkDonut", "&7&oHomer's favorites");
        new HatType("NTlkYTU0ZmYzNjZlNzM4ZTMxZGU5MjkxOTk4NmFiYjRkNTBjYTk0NGZhOTkyNmFmNjM3NThiNzQ0OGYxOCJ9fX0=", "ChocolateDonut", "&7&oThe same as the others but chocolatey");
        new HatType("ZDUzYzFlODdlNTM3ZjFhYjI3NzRkZGFmYjgzNDM5YjMzNmY0YTc3N2I0N2FkODJiY2IzMGQ1ZmNiZGY5YmMifX19", "Pie", "&7&oi like pie");
        new HatType("OWM2MGRhMjk0NGExNzdkZDA4MjY4ZmJlYzA0ZTQwODEyZDFkOTI5NjUwYmU2NjUyOWIxZWU1ZTFlN2VjYSJ9fX0=", "A", "&7&oThe first letter in the alphabet!");
        new HatType("ODA0MWY1ZTg2OTgzZDM2ZWFlYzRlMTY3YjJiYmI1YTM3Mjc2MDdjZGU4OGY3NTU1Y2ExYjUyMmEwMzliYiJ9fX0==", "B", "&7&oThe second letter in the alphabet!");
        new HatType("ZDk0NTk5NmM4YWU5MWUzNzYxOTZkNGRjNjc2ZmVjMzFmZWFjNzkwYTJmMTk1YjI5ODFhNzAzY2ExZDE2Y2I2In19fQ===", "C", "&7&oThe third letter in the alphabet!");
        new HatType("MTY0MTE1MGY0ODFlODQ5MmY3MTI4Yzk0ODk5NjI1NGQyZDkxZmM5MGY1YThmZjRkOGFjNWMzOWE2YTg4YSJ9fX0=", "D", "&7&9The fourth letter in the alphabet!");
        new HatType("ZGIyNTE0ODdmZjhlZWYyZWJjN2E1N2RhYjZlM2Q5ZjFkYjdmYzkyNmRkYzY2ZmVhMTRhZmUzZGZmMTVhNDUifX19", "E", "&7&9The fifth letter in the alphabet!");
        new HatType("N2U0MzM2NTZiNDQzNjY4ZWQwM2RhYzhjNDQyNzIyYTJhNDEyMjFiZThiYjQ4ZTIzYjM1YmQ4YzJlNTlmNjMifX19", "F", "&7&9The sixth letter in the alphabet!");
        new HatType("OTk1ODYzYjczNjM3NjA1ZmVhY2JiMTczYjc3ZDVlMTU1ZTY1MjA0Yzc4ZDVjNzkxMWY3MzhmMjhkZWI2MCJ9fX0=", "G", "&7&9The seventh letter in the alphabet!");
        new HatType("M2MxZDM1OGQ5MjcwNzQyODljYzI2YmZmNWIxMjQwNzQ2ZjlmNGYwY2M0NmY5NDJmNTk4MWM2NTk1ZjcyZGQifX19", "H", "&7&9The eighth letter in the alphabet!");
        new HatType("OGYyMjk1ODY1YmRhNGU0Nzk3OWQzNmI4YTg4N2E3NWExM2IwMzRlNjk4OGY3ODY3MGI2NGExZTY0NDJjIn19fQ==", "I", "&7&9The ninth letter in the alphabet!");
        new HatType("ZTM0NDYyYjU1ZDdmNTgyMzY4MGFkMTNmMmFkYmQ3ZDFlZDQ2YmE1MTAxMDE3ZWQ0YjM3YWVlZWI3NzVkIn19fQ==", "J", "&7&9The tenth letter in the alphabet!");
        new HatType("NzczMzI1YTkzNWMwNjdiNmVmMjI3MzY3ZjYyY2E0YmY0OWY2N2FkYjlmNmRhMzIwOTFlMmQzMmM1ZGRlMzI4In19fQ==", "K", "&7&9The eleventh letter in the alphabet!");
        new HatType("MjVhMWUzMzI4YzU3MWFhNDk1ZDljNWY0OTQ4MTVjY2ExNzZjM2FjYjE4NGZlYjVhN2I5Yzk2Y2U4ZTUyZmNlIn19fQ==", "L", "&7&9The twelfth letter in the alphabet!");
        new HatType("ZDQ2N2JmNmJlOTVlNWM4ZTlkMDE5NzdhMmYwYzQ4N2VkNWIwZGU1Yzg3OTYzYTJlYjE1NDExYzQ0MmZiMmIifX19", "M", "&7&9The thirteenth letter in the alphabet!");
        new HatType("ODIzZTQzNGQ2Mzk1ZmU3ZTYzNDkyNDMxYmRlZTU3ODJiZDVlZTViYzhjYWI3NTU5NDY3YmRkMWY5M2I5MjVhIn19fQ==", "N", "&7&9The fourteenth letter in the alphabet!");
        new HatType("ODg0NDU0NjZiZGM1YWQ1YmNlYTgyMjM5YzRlMWI1MTBmNmVhNTI2MmQ4MmQ4YTk2ZDcyOTFjMzQyZmI4OSJ9fX0=", "O", "&7&9The fifteenth letter in the alphabet!");
        new HatType("ZjlkZTYwMWRlZTNmZmVjYTRkNTQ1OTVmODQ0MjAxZDBlZDIwOTFhY2VjNDU0OGM2OTZiYjE2YThhMTU4ZjYifX19", "P", "&7&9The sixteenth letter in the alphabet!");
        new HatType("NjZjYTc2OWJkZTI1ZDRjYzQxZTE5ZTQyYWRjMzVhYjRjMTU1N2I3NmFmMjMyNjQ5YWNjOTk2N2ZmMTk4ZjEzIn19fQ==", "Q", "&7&9The seventeenth letter in the alphabet!");
        new HatType("NjdhMTg4ODA1MTYyY2E1ZGQ0ZjQ2NDljNjYxZDNmNmQyM2M0MjY2MmFlZjAxNjQ1YjFhOTdmNzhiM2YxMzIxOSJ9fX0=", "R", "&7&9The eighteenth letter in the alphabet!");
        new HatType("NjBkMDlkZmQ5ZjVkZTYyNDMyMzNlMGUzMzI1YjZjMzQ3OTMzNWU3Y2NmMTNmMjQ0OGQ0ZTFmN2ZjNGEwZGYifX19", "S", "&7&9The nineteenth letter in the alphabet!");
        new HatType("NjRjNzU2MTliOTFkMjQxZjY3ODM1MGFkOTIzN2MxMzRjNWUwOGQ4N2Q2ODYwNzQxZWRlMzA2YTRlZjkxIn19fQ==", "T", "&7&9The twentieth letter in the alphabet!");
        new HatType("ZTlmNmQyYzZkNTI4NWY4ODJhZTU1ZDFlOTFiOGY5ZWZkZmM5YjM3NzIwOGJmNGM4M2Y4OGRkMTU2NDE1ZSJ9fX0=", "U", "&7&9The twenty-first letter in the alphabet!");
        new HatType("ZGNlMjdhMTUzNjM1ZjgzNTIzN2Q4NWM2YmY3NGY1YjFmMmU2MzhjNDhmZWU4YzgzMDM4ZDA1NThkNDFkYTcifX19", "V", "&7&9The twenty-second letter in the alphabet!");
        new HatType("YWVkY2Y0ZmZjYjUzYjU2ZDQyYmFhYzlkMGRmYjExOGUzNDM0NjIzMjc0NDJkZDliMjlkNDlmNTBhN2QzOGIifX19", "W", "&7&9The twenty-third letter in the alphabet!");
        new HatType("ODM2MThmZjEyMTc2NDBiZWM1YjUyNWZhMmE4ZTY3MWM3NWQyYTdkN2NiMmRkYzMxZDc5ZDlkODk1ZWFiMSJ9fX0=", "X", "&7&9The twenty-fourth letter in the alphabet!");
        new HatType("ZDljMWQyOWEzOGJjZjExM2I3ZThjMzRlMTQ4YTc5ZjlmZTQxZWRmNDFhYThiMWRlODczYmIxZDQzM2IzODYxIn19fQ==", "Y", "&7&9The twenty-fifth letter in the alphabet!");
        new HatType("YjkyOTU3MzQxOTVkMmM3ZmEzODliOTg3NTdlOTY4NmNlNjQzN2MxNmM1OGJkZjJiNGNkNTM4Mzg5YjU5MTIifX19", "Z", "&7&9The twenty-sixth letter in the alphabet!");
    }
}
