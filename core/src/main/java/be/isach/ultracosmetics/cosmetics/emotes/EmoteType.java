package be.isach.ultracosmetics.cosmetics.emotes;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ItemFactory;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
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

    public static final EmoteType CRY = new EmoteType("ultracosmetics.emotes.cry", "Cry", "Are you really sad? :(");
    public static final EmoteType ANGRY = new EmoteType("ultracosmetics.emotes.angry", "Angry", "Show your rage to the other players!");
    public static final EmoteType HAPPY = new EmoteType("ultracosmetics.emotes.happy", "Happy", "Don't worry, be happy!");
    public static final EmoteType CHEEKY = new EmoteType("ultracosmetics.emotes.cheeky", "Cheeky", "You like being cheeky? Well, this emote is for you!");
    public static final EmoteType SURPRISED = new EmoteType("ultracosmetics.emotes.surprised", "Surprised", "OH LORD!");
    public static final EmoteType LOVE = new EmoteType("ultracosmetics.emotes.love", "Love", "You have beautiful eyes, do you know that?");
    public static final EmoteType TIRED = new EmoteType("ultracosmetics.emotes.tired", "Tired", "I think you've been playing here a bit too much..");

    private List<ItemStack> frames;

    private static Class<Emote> CLASS = Emote.class;

    private String permission;
    private String configName;
    private String defaultDesc;
    private String description;

    static {

        /* CRY BEGIN */
        CRY.addTextureToAnimation("9eabeda496e0684f80d88d4b5a71d65afaf9faf184fa4a77f81ed24c6da0f4");
        CRY.addTextureToAnimation("196b8e272c54a422d9df36d85caff26624c733e7b3f6040d3e4c9cd6e");
        CRY.addTextureToAnimation("dec9aa9b3f46195ae9c7fea7c61148764a41e0d68dae41e82868d792b3c");
        CRY.addTextureToAnimation("be29dadb60c9096fab92ffa7749e30462e14a8afaf6de938d9c0a4d78781");
        CRY.addTextureToAnimation("c8aba1f49fbf8829859ddd8f7e5918155e7ddc78919768b6e6c536e5278c31");
        CRY.addTextureToAnimation("1073ba3f1ca1d1e4f7e1ec742ddcff8fb0d962bc5662d127622a3726e3bb66");
        CRY.addTextureToAnimation("952dcdb13f732342ef37cbf0902960984992f5e67289373054b00c2a1f7");
        CRY.addTextureToAnimation("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");
        CRY.addTextureToAnimation("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
        CRY.addTextureToAnimation("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");
        CRY.addTextureToAnimation("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e");
        CRY.addTextureToAnimation("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");
        CRY.addTextureToAnimation("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
        CRY.addTextureToAnimation("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");
        CRY.addTextureToAnimation("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e");
        CRY.addTextureToAnimation("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");
        CRY.addTextureToAnimation("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
        CRY.addTextureToAnimation("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");
        CRY.addTextureToAnimation("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e");
        CRY.addTextureToAnimation("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");
        CRY.addTextureToAnimation("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64");
        CRY.addTextureToAnimation("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4");
        CRY.addTextureToAnimation("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab");

        /* CRY END */

        /* ANGRY BEGIN */

        ANGRY.addTextureToAnimation("513f7eb9fcf9926bf7b94049aef5efdb7bbe70bcc74f3f6618e12dc181d627");
        ANGRY.addTextureToAnimation("47bbf6d9f4c57556eef816c50eb75f9d158f53954957aabe6c2e14ffa6c90");
        ANGRY.addTextureToAnimation("a750127f1c3c71f6a5f5e9917a825e9235e1959b258ff29b6ff9771cb44");
        ANGRY.addTextureToAnimation("e95b20fb1fcfbef222062dd43eecbcb3871c528665f8ed675f42fc6e589a0b7");
        ANGRY.addTextureToAnimation("275c46184f9a85351d6ba618f8d1655cb5b71d6fc6ed3ccc462d916d376a8db");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da");
        ANGRY.addTextureToAnimation("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1");
        ANGRY.addTextureToAnimation("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856");

        /* ANGRY END */
    }

    public EmoteType(String permission, String configName, String defaultDesc) {
        this.permission = permission;
        this.configName = configName;
        this.frames = new ArrayList<>();

        if (SettingsManager.getConfig().get("Emotes." + configName + ".Description") == null) {
            this.description = defaultDesc;
            UltraCosmetics.config.addDefault("Emotes." + configName + ".Description", getDescriptionColored(), new String[]{"description of this emote."});
        } else {
            this.description = fromList((List<String>) SettingsManager.getConfig().get(new StringBuilder().append("Emotes.").append(configName).append(".Description").toString()));
        }
        VALUES.add(this);
    }

    public Emote equip(Player player) {
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

    public void addTextureToAnimation(String texture) {
        if (this == null) {
            texture = new String(Base64.decodeBase64(texture));
//            System.out.println(texture);
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = null;
            try {
                jsonObj = (JSONObject) parser.parse(texture);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = (JSONObject) jsonObj.get("textures");
            JSONObject jsonObjecet = (JSONObject) jsonObject.get("SKIN");
            System.out.println(jsonObjecet.get("url").toString().replace("http://textures.minecraft.net/texture/", ""));
            try {
                String urll = "http://textures.minecraft.net/texture/" + texture;
                URL url = new URL(urll);
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();
                FileOutputStream fos = new FileOutputStream("/Users/Sacha/Documents/emotes_textures/ANGRY_" + frames.size() + ".png");
                fos.write(response);
                fos.close();
            } catch (IOException e) {
//                e.printStackTrace();
            }

        }
        frames.add(getSkull(texture));
    }

    public String getName() {
        return MessageManager.getMessage("Emotes." + configName + ".Name");
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++)
            stringBuilder.append((String) description.get(i) + (i < description.size() - 1 ? "\n" : ""));
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
}
