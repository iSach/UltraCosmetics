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
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hat types.
 *
 * @author iSach
 * @since 10-15-2015
 */
public class HatType extends CosmeticType<Hat> {

    private static final List<HatType> ENABLED = new ArrayList<>();
    private static final List<HatType> VALUES = new ArrayList<>();
    private static final String URL_PREFIX = "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/";
    private static final String URL_SUFFIX = "\"}}}";

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

    private HatType(String texture, String configName, String defaultDesc) {
        super(Category.HATS, configName, defaultDesc, XMaterial.PLAYER_HEAD, Hat.class);
        String url = URL_PREFIX + texture + URL_SUFFIX;
        String base64 = Base64.getEncoder().encodeToString(url.getBytes());
        this.itemStack = ItemFactory.createSkull(base64, ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Hat");

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
        new HatType("3e8aad673157c92317a88b1f86f5271f1cd7397d7fc8ec3281f733f751634", "Astronaut", "&7&oHouston, we have got a problem.");
        new HatType("636e26c44659e8148ed58aa79e4d60db595f426442116f81b5415c2446ed8", "Scared", "&7&oOh god, that scared me!");
        new HatType("3e1debc73231f8ed4b69d5c3ac1b1f18f3656a8988e23f2e1bdbc4e85f6d46a", "Angel", "&7&oDid it hurt when you fell from heaven?");
        new HatType("f720df911c052377065408db78a25c678f791eb944c063935ae86dbe51c71b", "Embarassed", "&7&oI'm kinda embarassed by that.");
        new HatType("545bd18a2aaf469fad72e52cde6cfb02bfbaa5bfed2a8151277f779ebcdcec1", "Kissy", "&7&oWanna kiss?");
        new HatType("14968ac5af3146826fa2b0d4dd114fda197f8b28f4750553f3f88836a21fac9", "Sad", "&7&oI'm so sad.");
        new HatType("868f4cef949f32e33ec5ae845f9c56983cbe13375a4dec46e5bbfb7dcb6", "Cool", "&7&oI'm such a cool guy.");
        new HatType("bc2b9b9ae622bd68adff7180f8206ec4494abbfa130e94a584ec692e8984ab2", "Surprised", "&7&oWow, did not expect that!");
        new HatType("b371e4e1cf6a1a36fdae27137fd9b8748e6169299925f9af2be301e54298c73", "Dead", "&7&oGot rekt");
        new HatType("1f1b875de49c587e3b4023ce24d472ff27583a1f054f37e73a1154b5b5498", "Crying", "&7&oI cry everytime");
        new HatType("5059d59eb4e59c31eecf9ece2f9cf3934e45c0ec476fc86bfaef8ea913ea710", "BigSmile", "&7&oBecause I'm really happy!");
        new HatType("f4ea2d6f939fefeff5d122e63dd26fa8a427df90b2928bc1fa89a8252a7e", "Wink", "&7&oYou know what I mean ;)");
        new HatType("3baabe724eae59c5d13f442c7dc5d2b1c6b70c2f83364a488ce5973ae80b4c3", "Derp", "&7&oDerp Derp Derping all around");
        new HatType("52e98165deef4ed621953921c1ef817dc638af71c1934a4287b69d7a31f6b8", "Smile", "&7&oBecause I'm happy");
        new HatType("bba8459145d83ffc44ad58c3260e74ca5a0f634c7eeb59a1ad3234849c933c", "Iron", "&7&oAs hard as iron!");
        new HatType("b6d1ce697e9dbaa4ccf642516aaa5981332dac1d331afee2ee3dcc89efdedb", "Gold", "&7&oMy precious!");
        new HatType("c01461973634525196ecc757693b171ada4ef24aa92836f42ea11bd79c3a502d", "Diamond", "&7&oThis is really strong!");
        new HatType("aa868ce917c09af8e4c350a5807041f6509bf2b89aca45e591fbbd7d4b117d", "Piston", "&7&oHave you got the redstone?");
        new HatType("8514d225b262d847c7e557b474327dcef758c2c5882e41ee6d8c5e9cd3bc914", "CommandBlock", "&7&oControl the world with it!");
        new HatType("4ceeb77d4d25724a9caf2c7cdf2d88399b1417c6b9ff5213659b653be4376e3", "Music", "&7&oYou are so musical.");
        new HatType("01433be242366af126da434b8735df1eb5b3cb2cede39145974e9c483607bac", "Squid", "&7&oBloop Bloop!");
        new HatType("1638469a599ceef7207537603248a9ab11ff591fd378bea4735b346a7fae893", "Chicken", "&7&oBwwaaaaaaaaaaaakkkkk!");
        new HatType("621668ef7cb79dd9c22ce3d1f3f4cb6e2559893b6df4a469514e667c16aa4", "Pig", "&7&oOink Oink!");
        new HatType("b78ef2e4cf2c41a2d14bfde9caff10219f5b1bf5b35a49eb51c6467882cb5f0", "Blaze", "&7&oWatch out for the fire!");
        new HatType("f31f9ccc6b3e32ecf13b8a11ac29cd33d18c95fc73db8a66c5d657ccb8be70", "Sheep", "&7&oBaaaa, baa");
        new HatType("89091d79ea0f59ef7ef94d7bba6e5f17f2f7d4572c44f90f76c4819a714", "Golem", "&7&oI'm your guard.");
        new HatType("7a59bb0a7a32965b3d90d8eafa899d1835f424509eadd4e6b709ada50b9cf", "Enderman", "&7&oNow I'm here, now I'm there!");
        new HatType("dba8d8e53d8a5a75770b62cce73db6bab701cc3de4a9b654d213d54af9615", "Mario", "&7&oIt's-a me! Mario!");
        new HatType("ff1533871e49ddab8f1ca82edb1153a5e2ed3764fd1ce029bf829f4b3caac3", "Luigi", "&7&oLuigi time!");
        new HatType("f256f71735ef458581c9dacf394185eed9b33cb6ec5cd594a57153a8b566560", "Batman", "&7&oI'm Batman!");
        new HatType("6f68d509b5d1669b971dd1d4df2e47e19bcb1b33bf1a7ff1dda29bfc6f9ebf", "Chest", "&7&oOpen, and close");
        new HatType("11f54ff9bb42851912aa87a1bda5b7cd9814ccccfbe225fdda887ad6180d9", "Skull", "&7&oWho is headless now?");
        new HatType("68d2183640218ab330ac56d2aab7e29a9790a545f691619e38578ea4a69ae0b6", "Ghost", "&7&o2spooky4u");
        new HatType("0289d4b4c96295915f068b99c27d394273f9f264fc968c5d5c47df2f5be2", "JackOLantern", "&7&oA little pumpkin");
        new HatType("86dbc1debc57438a5de4ba915151382abc3d8f1318e2a35e78dfb30f04bc467", "ScaryClown", "&7&oHope you are not scared of clowns.");
        new HatType("2d61ccbfdcdf8941adaf76c6c0e0182d2c8bbb5dc18f374895652bc661b6ed", "Santa", "&7&oHo ho ho! Merry Christmas!");
        new HatType("98e334e4bee04264759a766bc1955cfaf3f56201428fafec8d4bf1bb36ae6", "Snowman", "&7&oI don't have a skull.. or bones");
        new HatType("f0afa4fffd10863e76c698da2c9c9e799bcf9ab9aa37d8312881734225d3ca", "Present", "&7&oFrom Santa, to you!");
        new HatType("82ab6c79c63b8334b2c03b6f736acf61aced5c24f2ba72b777d77f28e8c", "Elf", "&7&oI work for Santa!");
        new HatType("36d1fabdf3e342671bd9f95f687fe263f439ddc2f1c9ea8ff15b13f1e7e48b9", "Bedrock", "&7&oUnbreakable!");
        new HatType("a7d5eb0aea5d61ba3ff4996416a90096a9d77609ebcd3b308f906ae888a45f6d", "RedCrewmate", "&7&oRed sus!");
        new HatType("6670bc5f045830094054aebc75b2ed37fc55f524d979d81ef61f3de5c217d0ca", "BlueCrewmate", "&7&oBlue sus!");
        new HatType("4e633480d4bfbeaa049d013ed5746d9f5df9495d0bae1d9a70d5e2649bc264f", "GreenCrewmate", "&7&oGreen sus!");
        new HatType("7d3ef1564636889fe3acd3bb264efd752c90d4c6b23b00a3ed6c2d7f5e822775", "CyanCrewmate", "&7&oCyan sus!");
        new HatType("e58e56c765e34423ad2877840ab7c5688b44939c537c202363a4f1b5b7580dc8", "LimeCrewmate", "&7&oLime sus!");
        new HatType("ab6b12c1b862b68936e8aee7a248c3e252e88b1fcff05700fce1c959120a229d", "YellowCrewmate", "&7&oYellow sus!");
        new HatType("d910e30441cb829b4ee8ca1c0444c1fac6d94ace5a5c17ce46d4ef6cd93b23a9", "OrangeCrewmate", "&7&oOrange sus!");
        new HatType("68b818677be3c2079937137f50d555c161703d07e99cc708b8b5f4112938281", "PurpleCrewmate", "&7&oPurple sus!");
        new HatType("24e95bdd5151222561370bb67ad4bb0366410f9186dd00ca4d45c6feb8419eac", "BlackCrewmate", "&7&oBlack sus!");
        new HatType("e994f7b302612ac3231d41f0e6d78a3082db3bd667d0a9c5bcf12ced8f9405bc", "WhiteCrewmate", "&7&oWhite sus!");
        new HatType("feb20b93453a82018e2d4063b084035a5fe55a8a175da4ce1adbc6ec40ebe272", "PinkCrewmate", "&7&oPink sus!");
        new HatType("c359cc1b468bba51707d0b6e4d229da550bcd8bcbd4fcff2720540a85681b17b", "BrownCrewmate", "&7&oBrown sus!");
        new HatType("f3487d457f9062d787a3e6ce1c4664bf7402ec67dd111256f19b38ce4f670", "Bread", "&7&oFreshly baked");
        new HatType("955d611a878e821231749b2965708cad942650672db09e26847a88e2fac2946", "Cheese", "&7&oSay cheese!");
        new HatType("347f4f5a74c6691280cd80e7148b49b2ce17dcf64fd55368627f5d92a976a6a8", "Pancakes", "&7&oBetter with syrup!");
        new HatType("f9136514f342e7c5208a1422506a866158ef84d2b249220139e8bf6032e193", "Cake", "&7&oMmmm, cake.");
        new HatType("b592cf9f42a5a8c995968493fdd1b11e0b69aad6473ff45384abe58b7fc7c7", "Cookie", "&7&oChocolate chips included!");
        new HatType("4cc3f781c923a2887f14c1eea11050166966f2602578401f1451e6097b979df", "CandyCane", "&7&oA bit of tooth-breaking");
        new HatType("819f948d17718adace5dd6e050c586229653fef645d7113ab94d17b639cc466", "Chocolate", "&7&oDon't feed your dog with this!");
        new HatType("1ed55260dccc8da59338c75e41d544a2e1e7dbef31a69fe42c01b3298bf2d", "WhiteChocolate", "&7&oA bit more creamy");
        new HatType("cbb311f3ba1c07c3d1147cd210d81fe11fd8ae9e3db212a0fa748946c3633", "Apple", "&7&oNot the company");
        new HatType("c3fed514c3e238ca7ac1c94b897ff6711b1dbe50174afc235c8f80d029", "Melon", "&7&oDon't smash it with your head");
        new HatType("fec415d702f3292a82f1471c8794cf63122d449d28ab886d4dc58fafd66", "CarvedPumpkin", "&7&oWho turned this thing off?");
        new HatType("cbc826aaafb8dbf67881e68944414f13985064a3f8f044d8edfb4443e76ba", "Strawberry", "&7&oDo these grow on trees?");
        new HatType("e9b0e969cf3fcced36b712350ffb46d8ed761fe5efb10e3b6a9795e6656da97", "Coconut", "&7&oParadise");
        new HatType("98ced74a22021a535f6bce21c8c632b273dc2d9552b71a38d57269b3538cf", "Taco", "&7&oLet's go for some tacos!");
        new HatType("e7ba22d5df21e821a6de4b8c9d373a3aa187d8ae74f288a82d2b61f272e5", "Bacon", "&7&oEverything's better with bacon.");
        new HatType("a0eacac41a9eaf051376ef2f959701e1bbe1bf4aa6715adc34b6dc29a13ea9", "Fries", "&7&oHamburger's best friend");
        new HatType("a6ef1c25f516f2e7d6f7667420e33adcf3cdf938cb37f9a41a8b35869f569b", "Hamburger", "&7&oThe classic fastfood!");
        new HatType("1497b147cfae52205597f72e3c4ef52512e9677020e4b4fa7512c3c6acdd8c1", "Popcorn", "&7&oThe classic movie snack!");
        new HatType("d07b8c51acec2a508bb2fa652fb6e4a08b19485159a099f5982ccb88df1fe27e", "WhiteDonut", "&7&oLike a donut, but in white!");
        new HatType("837c9b82b186656e9f6363a2a1c6a4b5b93cfa9ef4dad6f16b94ebb5e362678", "PinkDonut", "&7&oHomer's favorites");
        new HatType("59da54ff366e738e31de92919986abb4d50ca944fa9926af63758b7448f18", "ChocolateDonut", "&7&oA better donut!");
        new HatType("d53c1e87e537f1ab2774ddafb83439b336f4a777b47ad82bcb30d5fcbdf9bc", "Pie", "&7&oI like pie");
        new HatType("9c60da2944a177dd08268fbec04e40812d1d929650be66529b1ee5e1e7eca", "A", "&7&oThe first letter in the alphabet!");
        new HatType("8041f5e86983d36eaec4e167b2bbb5a3727607cde88f7555ca1b522a039bb", "B", "&7&oThe second letter in the alphabet!");
        new HatType("d945996c8ae91e376196d4dc676fec31feac790a2f195b2981a703ca1d16cb6", "C", "&7&oThe third letter in the alphabet!");
        new HatType("1641150f481e8492f7128c948996254d2d91fc90f5a8ff4d8ac5c39a6a88a", "D", "&7&oThe fourth letter in the alphabet!");
        new HatType("db251487ff8eef2ebc7a57dab6e3d9f1db7fc926ddc66fea14afe3dff15a45", "E", "&7&oThe fifth letter in the alphabet!");
        new HatType("7e433656b443668ed03dac8c442722a2a41221be8bb48e23b35bd8c2e59f63", "F", "&7&oThe sixth letter in the alphabet!");
        new HatType("995863b73637605feacbb173b77d5e155e65204c78d5c7911f738f28deb60", "G", "&7&oThe seventh letter in the alphabet!");
        new HatType("3c1d358d927074289cc26bff5b1240746f9f4f0cc46f942f5981c6595f72dd", "H", "&7&oThe eighth letter in the alphabet!");
        new HatType("8f2295865bda4e47979d36b8a887a75a13b034e6988f78670b64a1e6442c", "I", "&7&oThe ninth letter in the alphabet!");
        new HatType("e34462b55d7f5823680ad13f2adbd7d1ed46ba5101017ed4b37aeeeb775d", "J", "&7&oThe tenth letter in the alphabet!");
        new HatType("773325a935c067b6ef227367f62ca4bf49f67adb9f6da32091e2d32c5dde328", "K", "&7&oThe eleventh letter in the alphabet!");
        new HatType("25a1e3328c571aa495d9c5f494815cca176c3acb184feb5a7b9c96ce8e52fce", "L", "&7&oThe twelfth letter in the alphabet!");
        new HatType("d467bf6be95e5c8e9d01977a2f0c487ed5b0de5c87963a2eb15411c442fb2b", "M", "&7&oThe thirteenth letter in the alphabet!");
        new HatType("823e434d6395fe7e63492431bdee5782bd5ee5bc8cab7559467bdd1f93b925a", "N", "&7&oThe fourteenth letter in the alphabet!");
        new HatType("88445466bdc5ad5bcea82239c4e1b510f6ea5262d82d8a96d7291c342fb89", "O", "&7&oThe fifteenth letter in the alphabet!");
        new HatType("f9de601dee3ffeca4d54595f844201d0ed2091acec4548c696bb16a8a158f6", "P", "&7&oThe sixteenth letter in the alphabet!");
        new HatType("66ca769bde25d4cc41e19e42adc35ab4c1557b76af232649acc9967ff198f13", "Q", "&7&oThe seventeenth letter in the alphabet!");
        new HatType("67a188805162ca5dd4f4649c661d3f6d23c42662aef01645b1a97f78b3f13219", "R", "&7&oThe eighteenth letter in the alphabet!");
        new HatType("60d09dfd9f5de6243233e0e3325b6c3479335e7ccf13f2448d4e1f7fc4a0df", "S", "&7&oThe nineteenth letter in the alphabet!");
        new HatType("64c75619b91d241f678350ad9237c134c5e08d87d6860741ede306a4ef91", "T", "&7&oThe twentieth letter in the alphabet!");
        new HatType("e9f6d2c6d5285f882ae55d1e91b8f9efdfc9b377208bf4c83f88dd156415e", "U", "&7&oThe twenty-first letter in the alphabet!");
        new HatType("dce27a153635f835237d85c6bf74f5b1f2e638c48fee8c83038d0558d41da7", "V", "&7&oThe twenty-second letter in the alphabet!");
        new HatType("aedcf4ffcb53b56d42baac9d0dfb118e343462327442dd9b29d49f50a7d38b", "W", "&7&oThe twenty-third letter in the alphabet!");
        new HatType("83618ff1217640bec5b525fa2a8e671c75d2a7d7cb2ddc31d79d9d895eab1", "X", "&7&oThe twenty-fourth letter in the alphabet!");
        new HatType("d9c1d29a38bcf113b7e8c34e148a79f9fe41edf41aa8b1de873bb1d433b3861", "Y", "&7&oThe twenty-fifth letter in the alphabet!");
        new HatType("b9295734195d2c7fa389b98757e9686ce6437c16c58bdf2b4cd538389b5912", "Z", "&7&oThe twenty-sixth letter in the alphabet!");
    }
}
