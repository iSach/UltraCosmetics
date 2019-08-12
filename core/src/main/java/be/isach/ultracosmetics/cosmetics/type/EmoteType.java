package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.emotes.Emote;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.TexturedSkullFactory;
import be.isach.ultracosmetics.util.UCMaterial;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Emote types.
 *
 * @author iSach
 * @since 07-17-2016
 */
public class EmoteType extends CosmeticMatType<Emote> {
	
	private final static List<EmoteType> ENABLED = new ArrayList<>();
	private final static List<EmoteType> VALUES = new ArrayList<>();
	
	public static List<EmoteType> enabled() {
		return ENABLED;
	}
	
	public static List<EmoteType> values() {
		return VALUES;
	}
	
	public static EmoteType valueOf(String s) {
		for (EmoteType emoteType : VALUES) {
			if (emoteType.getConfigName().equalsIgnoreCase(s)) return emoteType;
		}
		return null;
	}
	
	public static void checkEnabled() {
		ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
	}
	
	public static final EmoteType CRY = new EmoteType("ultracosmetics.emotes.cry", "Cry", "&7&oAre you really sad? :(", 1);
	public static final EmoteType ANGRY = new EmoteType("ultracosmetics.emotes.angry", "Angry", "&7&oShow your rage to the other players!", 1);
	public static final EmoteType HAPPY = new EmoteType("ultracosmetics.emotes.happy", "Happy", "&7&oDon't worry, be happy!", 1);
	public static final EmoteType CHEEKY = new EmoteType("ultracosmetics.emotes.cheeky", "Cheeky", "&7&oYou like being cheeky? Well, this emote is for you!", 1);
	public static final EmoteType LOVE = new EmoteType("ultracosmetics.emotes.love", "Love", "&7&oYou have beautiful eyes, do you know that?", 2);
	public static final EmoteType DEAL_WITH_IT = new EmoteType("ultracosmetics.emotes.dealwithit", "DealWithIt", "&7&oDo you feel like showing off? This emote is for you!", 3);
	public static final EmoteType COOL = new EmoteType("ultracosmetics.emotes.cool", "Cool", "&7&oKeep cool man!", 2);
	public static final EmoteType SURPRISED = new EmoteType("ultracosmetics.emotes.surprised", "Surprised", "&7&oOH LORD!!!", 1);
	public static final EmoteType WINK = new EmoteType("ultracosmetics.emotes.wink", "Wink", "&7&oHaving fun? Let someone know you are in on the joke", 2);
	
	static {

        /* CRY BEGIN */
		CRY.appendTexture("d6a4b88a297c78f9dd53ee8c7164dda0ded7e988382555bc7d89c901922b32e");
		CRY.appendTexture("196b8e272c54a422d9df36d85caff26624c733e7b3f6040d3e4c9cd6e");
		CRY.appendTexture("dec9aa9b3f46195ae9c7fea7c61148764a41e0d68dae41e82868d792b3c");
		CRY.appendTexture("be29dadb60c9096fab92ffa7749e30462e14a8afaf6de938d9c0a4d78781");
		CRY.appendTexture("c8aba1f49fbf8829859ddd8f7e5918155e7ddc78919768b6e6c536e5278c31");
		CRY.appendTexture("1073ba3f1ca1d1e4f7e1ec742ddcff8fb0d962bc5662d127622a3726e3bb66");
		CRY.appendTexture("952dcdb13f732342ef37cbf0902960984992f5e67289373054b00c2a1f7");
		CRY.appendTexture("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");
		CRY.appendTexture("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
		CRY.appendTexture("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");
		CRY.appendTexture("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e");
		CRY.appendTexture("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");
		CRY.appendTexture("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
		CRY.appendTexture("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");
		CRY.appendTexture("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e");
		CRY.appendTexture("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");
		CRY.appendTexture("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
		CRY.appendTexture("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");
		CRY.appendTexture("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e");
		CRY.appendTexture("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");
		CRY.appendTexture("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
		CRY.appendTexture("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
		CRY.appendTexture("b3f2bbd15ea14faf22b0698ec032f64eb13db379c8ee1ef78a9b27f2ae6d8662");

        /* CRY END */

        /* ANGRY BEGIN */
		
		ANGRY.appendTexture("7d41407363bcb46837538a63fdf7055278d42dc4aac639ed5794533bbd770");
		ANGRY.appendTexture("47bbf6d9f4c57556eef816c50eb75f9d158f53954957aabe6c2e14ffa6c90");
		ANGRY.appendTexture("a750127f1c3c71f6a5f5e9917a825e9235e1959b258ff29b6ff9771cb44");
		ANGRY.appendTexture("e95b20fb1fcfbef222062dd43eecbcb3871c528665f8ed675f42fc6e589a0b7");
		ANGRY.appendTexture("275c46184f9a85351d6ba618f8d1655cb5b71d6fc6ed3ccc462d916d376a8db");
		ANGRY.appendTexture("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
		ANGRY.appendTexture("d82738fc82bedaf3029612f1ec92fe0cf848e541c8e30dcf41efc04bea30ba");
		for (int i = 0; i < 5; i++) {
			ANGRY.appendTexture("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
			ANGRY.appendTexture("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da");
			ANGRY.appendTexture("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
			ANGRY.appendTexture("d82738fc82bedaf3029612f1ec92fe0cf848e541c8e30dcf41efc04bea30ba");
		}

        /* ANGRY END */


        /* HAPPY BEGIN */
		
		HAPPY.appendTexture("ef66c7485ccb853a6538122e45c8a4821fbe097f96a6060feb981f7a2bba890");
		HAPPY.appendTexture("a5d43eb0ec5f6de1d469b69680978a6dd7117772ee0d82ffdf08749e84df7ed");
		for (int i = 0; i < 7; i++)
			HAPPY.appendTexture("473e72cc371de25f3305665769dd7e9ff1161695252e799612580deeedd3");
		for (int i = 0; i < 15; i++)
			HAPPY.appendTexture("01b9def55876c41c17c815f88115f02c95f89620fbed6a6cb2d38d46fe05");

        /* HAPPY END */


        /* CHEEKY BEGIN */
		
		for (int i = 0; i < 4; i++)
			CHEEKY.appendTexture("319ec094258842725e41f985346a7f824af6fc6cf13fbe949c9c465a30bc99");
		CHEEKY.appendTexture("b7d533e65f2cae97afe334c81ecc97e2fa5b3e5d3ecf8b91bc39a5adb2e79a");
		CHEEKY.appendTexture("35a46f8334e49d273384eb72b2ac15e24a640d7648e4b28c348efce93dc97ab");
		CHEEKY.appendTexture("1d977753c3db742865ccf14c5c3f482eaf5721414750e6d3be96e1ae7c8291c4");
		CHEEKY.appendTexture("c4188e6d90f7769dae3a7277e2490d01b8017d74a725fd3aebbc82a911aa4e");
		CHEEKY.appendTexture("1d977753c3db742865ccf14c5c3f482eaf5721414750e6d3be96e1ae7c8291c4");
		CHEEKY.appendTexture("447dcf9dd283ad6d83942b6607a7ce45bee9cdfeefb849da29d661d03e7938");
		CHEEKY.appendTexture("de355559f4cd56118b4bc8b4697b625e1845b635790c07bf4924c8c7673a2e4");
		CHEEKY.appendTexture("207eef91a453a5151487c9d6b9d4c434db7f8a02a4caf18ef6f3358677f6");
		CHEEKY.appendTexture("c4188e6d90f7769dae3a7277e2490d01b8017d74a725fd3aebbc82a911aa4e");
		CHEEKY.appendTexture("1d977753c3db742865ccf14c5c3f482eaf5721414750e6d3be96e1ae7c8291c4");
		CHEEKY.appendTexture("447dcf9dd283ad6d83942b6607a7ce45bee9cdfeefb849da29d661d03e7938");
		CHEEKY.appendTexture("de355559f4cd56118b4bc8b4697b625e1845b635790c07bf4924c8c7673a2e4");
		CHEEKY.appendTexture("207eef91a453a5151487c9d6b9d4c434db7f8a02a4caf18ef6f3358677f6");
		for (int i = 0; i < 10; i++)
			CHEEKY.appendTexture("c4188e6d90f7769dae3a7277e2490d01b8017d74a725fd3aebbc82a911aa4e");

        /* CHEEKY END */


        /* LOVE BEGIN */
		
		LOVE.appendTexture("a080c9bfc64aeb5aed2acaa885d6fcbbd5e5ddf468956d3f1b1e455774d48893");
		LOVE.appendTexture("a4d678bb120fbd3beacaf36bdb117766a63d7c2d96a6e85a8ef5a2b13e166");
		LOVE.appendTexture("42737e99e4c0596a3712e7711baecae8d1ddb774ac1cf531896862380753e16");
		LOVE.appendTexture("a4d678bb120fbd3beacaf36bdb117766a63d7c2d96a6e85a8ef5a2b13e166");
		LOVE.appendTexture("a080c9bfc64aeb5aed2acaa885d6fcbbd5e5ddf468956d3f1b1e455774d48893");
		LOVE.appendTexture("a4d678bb120fbd3beacaf36bdb117766a63d7c2d96a6e85a8ef5a2b13e166");
		LOVE.appendTexture("42737e99e4c0596a3712e7711baecae8d1ddb774ac1cf531896862380753e16");
		LOVE.appendTexture("a4d678bb120fbd3beacaf36bdb117766a63d7c2d96a6e85a8ef5a2b13e166");
		LOVE.appendTexture("a080c9bfc64aeb5aed2acaa885d6fcbbd5e5ddf468956d3f1b1e455774d48893");

        /* LOVE END */

        /* DEAL WITH IT BEGIN */
		
		DEAL_WITH_IT.appendTexture("72bb8ba79648718fe80687ed4df2b9e284e732583e05658e227efd7fdf80f4");
		DEAL_WITH_IT.appendTexture("29b5b1f2c92a1283456f608b29ec3617191aba2bd31bd4b4b08e6cba6806227");
		DEAL_WITH_IT.appendTexture("7959ef5fabb3f83fb19bba6ca67bb97758eec60235cf46e71d834b237337c4");
		DEAL_WITH_IT.appendTexture("6313411e97963d104322218967a85a5d691330bad5f7192e3781d9565ebbdf");
		DEAL_WITH_IT.appendTexture("fa3f7f2f6970d32db284261520c8c441fe4b3268ac0c99aeb4a5248656bd");

        /* DEAL WITH IT END */

        /* COOL BEGIN */
		
		COOL.appendTexture("a21e6dbfd74a1859ddbae3380fc1ab71f2389745945fc92329b164635bd14f");
		COOL.appendTexture("3733db9a94bfe15cdbb7ca5832c85cfada98ad2c839934766bdc41f977b5c163");
		for (int i = 0; i < 4; i++)
			COOL.appendTexture("766b3eef3c726ecb816c43839189eeb8e36382e3e5fe41128372785185a322");

        /* COOL END */

        /* SURPRISED BEGIN */
		
		SURPRISED.appendTexture("6b7f24bb6a4585de8f42303161bded5c8398ce375631be149460d6835aec44e");
		SURPRISED.appendTexture("33c760f660d447846ab6b3d5a914c4b01f10672b63d4311d468b6dc28ba0e3");
		SURPRISED.appendTexture("382d15e94182206025973ff1928f4456bf7abaff737942d54b1c5699892c");
		SURPRISED.appendTexture("9d641bd33180c53dcc77e3d4c665935e63011d87ae9796a2ae7bd334cd64");
		for (int i = 0; i < 8; i++)
			SURPRISED.appendTexture("4c3b089e446f065dd9059519c85c45aebb53891be3c3a7ed5b5eb61a96747");

        /* SURPRISED END */
        
		/* WINK BEGIN */
		
		WINK.appendTexture("ef66c7485ccb853a6538122e45c8a4821fbe097f96a6060feb981f7a2bba890");
		WINK.appendTexture("a5d43eb0ec5f6de1d469b69680978a6dd7117772ee0d82ffdf08749e84df7ed");
		for (int i = 0; i < 2; i++)
			WINK.appendTexture("473e72cc371de25f3305665769dd7e9ff1161695252e799612580deeedd3");
		for (int i = 0; i < 4; i++)
			WINK.appendTexture("f4ea2d6f939fefeff5d122e63dd26fa8a427df90b2928bc1fa89a8252a7e");

		/* WINK END */
		
	}
	
	private List<ItemStack> frames;
	private int ticksPerFrame;
	
	public EmoteType(String permission, String configName, String defaultDesc, int ticksPerFrame) {
		super(Category.EMOTES, configName, permission, defaultDesc, UCMaterial.PLAYER_HEAD, Emote.class, ServerVersion.v1_8_R1);
		this.ticksPerFrame = ticksPerFrame;
		this.frames = new ArrayList<>();
		
		VALUES.add(this);
	}
	
	@Override
	public Emote equip(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
		if (ultraPlayer.getBukkitPlayer().getInventory().getHelmet() != null) {
			if (ultraPlayer.getCurrentHat() != null) {
				ultraPlayer.removeHat();
			} else if (ultraPlayer.getCurrentEmote() != null) {
				ultraPlayer.removeEmote();
			} else {
				ultraPlayer.sendMessage(MessageManager.getMessage("Emotes.Must-Remove-Helmet"));
				return null;
			}
		}
		Emote cosmetic = null;
		try {
			cosmetic = getClazz().getDeclaredConstructor(UltraPlayer.class, EmoteType.class, UltraCosmetics.class).newInstance(ultraPlayer, this, ultraCosmetics);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		cosmetic.equip();
		return cosmetic;
	}
	
	public void appendTexture(String texture) {
		frames.add(TexturedSkullFactory.createSkull(texture));
	}
	
	@Override
	public String getName() {
		return MessageManager.getMessage("Emotes." + getConfigName() + ".Name");
	}
	
	public List<ItemStack> getFrames() {
		return frames;
	}
	
	public int getMaxFrames() {
		return frames.size();
	}
	
	public int getTicksPerFrame() {
		return ticksPerFrame;
	}
}
