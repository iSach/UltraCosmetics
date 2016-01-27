package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.Particles;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 18/12/15.
 */
public enum ParticleEffectType {

    RAINCLOUD("raincloud", "RainCloud", 1, Particles.DRIP_WATER, Material.INK_SACK, (byte) 0, ParticleEffectRainCloud.class, "&7&oThe weather forecast\n&7&ois telling me it's raining."),
    SNOWCLOUD("snowcloud", "SnowCloud", 1, Particles.SNOW_SHOVEL, Material.SNOW_BALL, (byte) 0, ParticleEffectSnowCloud.class, "&7&oThe weather forecast\n&7&ois telling me it's snowing."),
    BLOODHELIX("bloodhelix", "BloodHelix", 1, Particles.REDSTONE, Material.REDSTONE, (byte) 0, ParticleEffectBloodHelix.class, "&7&oAncient legend says this magic\n&7&oempowers the blood of its user,\n&7&ogiving them godly powers.."),
    FROSTLORD("frostlord", "FrostLord", 1, Particles.SNOW_SHOVEL, Material.PACKED_ICE, (byte) 0, ParticleEffectFrostLord.class, "&7&oI am The Almighty Frostlord!"),
    FLAMERINGS("flamerings", "FlameRings", 1, Particles.FLAME, Material.BLAZE_POWDER, (byte) 0, ParticleEffectFlameRings.class, "&7&oWatch out! They are hot!"),
    INLOVE("inlove", "InLove", 1, Particles.HEART, Material.RED_ROSE, (byte) 0, ParticleEffectInLove.class, "&7&oOMG wow I'm in love!"),
    GREENSPARKS("greensparks", "GreenSparks", 1, Particles.VILLAGER_HAPPY, Material.EMERALD, (byte) 0, ParticleEffectGreenSparks.class, "&7&oLittle and green sparkly sparks!"),
    FROZENWALK("frozenwalk", "FrozenWalk", 1, Particles.SNOW_SHOVEL, Material.SNOW_BALL, (byte) 0, ParticleEffectFrozenWalk.class, "&7&oMy feet are so cold!"),
    MUSIC("music", "Music", 4, Particles.FLAME, Material.RECORD_7, (byte) 0, ParticleEffectMusic.class, "&7&oMuch music!"),
    ENCHANTED("enchanted", "Enchanted", 1, Particles.ENCHANTMENT_TABLE, Material.BOOK, (byte) 0, ParticleEffectEnchanted.class, "&7&oBecome an almighty enchanter!"),
    INFERNO("inferno", "Inferno", 1, Particles.FLAME, Material.NETHER_STALK, (byte) 0, ParticleEffectInferno.class, "&7&oEffect created by Satan himself!"),
    ANGELWINGS("angelwings", "AngelWings", 2, Particles.REDSTONE, Material.FEATHER, (byte) 0, ParticleEffectAngelWings.class, "&7&oBecome an angel!"),
    SUPERHERO("superhero", "SuperHero", 2, Particles.REDSTONE, Material.GLOWSTONE_DUST, (byte) 0, ParticleEffectSuperHero.class, "&7&oBecome Superman!"),
    SANTAHAT("santahat", "SantaHat", 2, Particles.REDSTONE, Material.BEACON, (byte) 378, ParticleEffectSantaHat.class, "&7&oBecome Santa!"),
    CRUSHEDCANDYCANE("crushedcandycane", "CrushedCandyCane", 1, Particles.ITEM_CRACK, Material.INK_SACK, (byte) 1, ParticleEffectCrushedCandyCane.class,
            "&7&oThere's no such thing as too much\n&7&oChristmas Candy. Do not listen\n&7&oto your dentist."),
    ENDERAURA("enderaura", "EnderAura", 1, Particles.PORTAL, Material.EYE_OF_ENDER, (byte) 0, ParticleEffectEnderAura.class, "&7&oThese mystic particle attach" +
            " to\n&7&oonly the most legendary of players!"),
    FLAMEFAIRY("flamefairy", "FlameFairy", 1, Particles.FLAME, Material.BLAZE_POWDER, (byte) 0, ParticleEffectFlameFairy.class, "&7&oHEY!!");

    private String permission, configName, description;
    private Particles effect;
    private Material material;
    private byte data;
    private int repeatDelay;
    public static List<ParticleEffectType> enabled = new ArrayList<>();
    private Class<? extends ParticleEffect> clazz;

    ParticleEffectType(String permission, String configName, int repeatDelay, Particles effect, Material material, byte data, Class<? extends ParticleEffect> clazz, String defaultDesc) {
        this.permission = "ultracosmetics.particleeffects." + permission;
        this.configName = configName;
        this.repeatDelay = repeatDelay;
        this.effect = effect;
        this.material = material;
        this.data = data;
        this.clazz = clazz;

        if (SettingsManager.getConfig().get("Particle-Effects." + configName + ".Description") == null) {
            this.description = defaultDesc;
            SettingsManager.getConfig().set("Particle-Effects." + configName + ".Description", getDescriptionWithColor(), "Description of this particle effect.");
        } else
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Particle-Effects." + configName + ".Description")));
    }

    public Material getMaterial() {
        return material;
    }

    public Particles getEffect() {
        return effect;
    }

    public String getConfigName() {
        return configName;
    }

    public byte getData() {
        return data;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public List<String> getDescriptionWithColor() {
        return Arrays.asList(description.split("\n"));
    }

    public String getName() {
        return MessageManager.getMessage("Particle-Effects." + configName + ".name");
    }

    public ParticleEffect equip(Player player) {
        ParticleEffect effect = null;
        try {
            effect = clazz.getDeclaredConstructor(UUID.class).newInstance(player == null ? null : player.getUniqueId());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return effect;
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n")) {
            desc.add(string.replace('&', '§'));
        }
        return desc;
    }

    public static List<ParticleEffectType> enabled() {
        return enabled;
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Particle-Effects." + getConfigName() + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Particle-Effects." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
    }

    public String getPermission() {
        return permission;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Particle-Effects." + configName + ".Enabled");
    }

}
