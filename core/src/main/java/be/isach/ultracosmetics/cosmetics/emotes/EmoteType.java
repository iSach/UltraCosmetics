package be.isach.ultracosmetics.cosmetics.emotes;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics.emotes
 * Created by: Sacha
 * Created on: 17th June, 2016
 * at 02:45
 */
public class EmoteType {

    public static final List<EmoteType> ENABLED = new ArrayList<>();
    public static final List<EmoteType> VALUES = new ArrayList<>();

    public static final EmoteType CRY = new EmoteType("ultracosmetics.emotes.cry", "Cry", "&7&oAre you really sad? :(");
    public static final EmoteType ANGRY = new EmoteType("ultracosmetics.emotes.angry", "Angry", "&7&oShow your rage to the other players!");
    public static final EmoteType HAPPY = new EmoteType("ultracosmetics.emotes.happy", "Happy", "&7&oDon't worry, be happy!");
    public static final EmoteType CHEEKY = new EmoteType("ultracosmetics.emotes.cheeky", "Cheeky", "&7&oYou like being cheeky? Well, this emote is for you!");
    public static final EmoteType LOVE = new EmoteType("ultracosmetics.emotes.love", "Love", "&7&oYou have beautiful eyes, do you know that?");

    private List<ItemStack> frames;

    private static Class<Emote> CLASS = Emote.class;

    private String permission;
    private String configName;
    private String description;

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
    }

    public EmoteType(String permission, String configName, String defaultDesc) {
        this.permission = permission;
        this.configName = configName;
        this.frames = new ArrayList<>();

        if (SettingsManager.getConfig().get("Emotes." + configName + ".Description") == null) {
            this.description = defaultDesc;
            UltraCosmetics.config.addDefault("Emotes." + configName + ".Description", getDescriptionColored(), "description of this emote.");
        } else {
            this.description = fromList((List<String>) SettingsManager.getConfig().get(new StringBuilder().append("Emotes.").append(configName).append(".Description").toString()));
        }
        VALUES.add(this);
    }

    public Emote equip(Player player) {
        if (player.getInventory().getHelmet() != null) {
            if(UltraCosmetics.getCustomPlayer(player).currentHat != null) {
                UltraCosmetics.getCustomPlayer(player).removeHat();
            } else {
                player.sendMessage(MessageManager.getMessage("Emotes.Must-Remove-Helmet"));
                return null;
            }
        }
        Emote emote = null;
        try {
            emote = CLASS.getDeclaredConstructor(UUID.class, EmoteType.class).newInstance(player == null ? null : player.getUniqueId(), this);
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return emote;
    }

    public void appendTexture(String texture) {
        frames.add(getSkull(texture));
    }

    public String getName() {
        return MessageManager.getMessage("Emotes." + configName + ".Name");
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++)
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        return stringBuilder.toString();
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Emotes." + configName + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Emotes." + configName + ".Can-Be-Found-In-Treasure-Chests");
    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : this.description.split("\n"))
            desc.add(string.replace('&', '§'));
        return desc;
    }

    public List<String> getDescriptionColored() {
        return Arrays.asList(this.description.split("\n"));
    }

    public String getPermission() {
        return permission;
    }

    public String getConfigName() {
        return configName;
    }

    public static List<EmoteType> enabled() {
        return ENABLED;
    }

    public static List<EmoteType> values() {
        return VALUES;
    }

    public List<ItemStack> getFrames() {
        return frames;
    }

    public int getMaxFrames() {
        return frames.size();
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Emotes." + configName + ".Enabled");
    }

    public ItemStack getSkull(String url) {
        url = "http://textures.minecraft.net/texture/" + url;
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName("§8§oEmote");
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        assert profileField != null;
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static EmoteType valueOf(String name) {
        for(EmoteType emoteType : values()) {
            if(emoteType.toString().toLowerCase().equals(name.toLowerCase())) return emoteType;
        }
        return null;
    }
}
