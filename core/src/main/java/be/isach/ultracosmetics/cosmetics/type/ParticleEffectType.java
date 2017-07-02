package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.particleeffects.*;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Particle effect types.
 *
 * @author iSach
 * @since 12-18-2015
 */
public class ParticleEffectType extends CosmeticMatType<ParticleEffect> {
	
	private final static List<ParticleEffectType> ENABLED = new ArrayList<>();
	private final static List<ParticleEffectType> VALUES = new ArrayList<>();
	
	public static List<ParticleEffectType> enabled() {
		return ENABLED;
	}
	
	public static List<ParticleEffectType> values() {
		return VALUES;
	}
	
	public static ParticleEffectType valueOf(String s) {
		for (ParticleEffectType particleEffectType : VALUES) {
			if (particleEffectType.getConfigName().equalsIgnoreCase(s)) return particleEffectType;
		}
		return null;
	}
	
	public static void checkEnabled() {
		ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
	}
	
	public static final ParticleEffectType RAINCLOUD = new ParticleEffectType("ultracosmetics.particleeffects.raincloud", "RainCloud", 1, Particles.DRIP_WATER, Material.INK_SACK, (byte) 0, ParticleEffectRainCloud.class, "&7&oThe weather forecast\n&7&ois telling me it's raining.", ServerVersion.v1_8_R1);
	public static final ParticleEffectType SNOWCLOUD = new ParticleEffectType("ultracosmetics.particleeffects.snowcloud", "SnowCloud", 1, Particles.SNOW_SHOVEL, Material.SNOW_BALL, (byte) 0, ParticleEffectSnowCloud.class, "&7&oThe weather forecast\n&7&ois telling me it's snowing.", ServerVersion.v1_8_R1);
	public static final ParticleEffectType BLOODHELIX = new ParticleEffectType("ultracosmetics.particleeffects.bloodhelix", "BloodHelix", 1, Particles.REDSTONE, Material.REDSTONE, (byte) 0, ParticleEffectBloodHelix.class, "&7&oAncient legend says this magic\n&7&oempowers the blood of its user,\n&7&ogiving them godly powers..", ServerVersion.v1_8_R1);
	public static final ParticleEffectType FROSTLORD = new ParticleEffectType("ultracosmetics.particleeffects.frostlord", "FrostLord", 1, Particles.SNOW_SHOVEL, Material.PACKED_ICE, (byte) 0, ParticleEffectFrostLord.class, "&7&oI am The Almighty Frostlord!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType FLAMERINGS = new ParticleEffectType("ultracosmetics.particleeffects.flamerings", "FlameRings", 1, Particles.FLAME, Material.BLAZE_POWDER, (byte) 0, ParticleEffectFlameRings.class, "&7&oWatch out! They are hot!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType INLOVE = new ParticleEffectType("ultracosmetics.particleeffects.inlove", "InLove", 1, Particles.HEART, Material.RED_ROSE, (byte) 0, ParticleEffectInLove.class, "&7&oOMG wow I'm in love!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType GREENSPARKS = new ParticleEffectType("ultracosmetics.particleeffects.greensparks", "GreenSparks", 1, Particles.VILLAGER_HAPPY, Material.EMERALD, (byte) 0, ParticleEffectGreenSparks.class, "&7&oLittle and green sparkly sparks!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType FROZENWALK = new ParticleEffectType("ultracosmetics.particleeffects.frozenwalk", "FrozenWalk", 1, Particles.SNOW_SHOVEL, Material.SNOW_BALL, (byte) 0, ParticleEffectFrozenWalk.class, "&7&oMy feet are so cold!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType MUSIC = new ParticleEffectType("ultracosmetics.particleeffects.music", "Music", 4, Particles.FLAME, Material.RECORD_7, (byte) 0, ParticleEffectMusic.class, "&7&oMuch music!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType ENCHANTED = new ParticleEffectType("ultracosmetics.particleeffects.enchanted", "Enchanted", 1, Particles.ENCHANTMENT_TABLE, Material.BOOK, (byte) 0, ParticleEffectEnchanted.class, "&7&oBecome an almighty enchanter!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType INFERNO = new ParticleEffectType("ultracosmetics.particleeffects.inferno", "Inferno", 1, Particles.FLAME, Material.NETHER_STALK, (byte) 0, ParticleEffectInferno.class, "&7&oEffect created by Satan himself!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType ANGELWINGS = new ParticleEffectType("ultracosmetics.particleeffects.angelwings", "AngelWings", 2, Particles.REDSTONE, Material.FEATHER, (byte) 0, ParticleEffectAngelWings.class, "&7&oBecome an angel!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType SUPERHERO = new ParticleEffectType("ultracosmetics.particleeffects.superhero", "SuperHero", 2, Particles.REDSTONE, Material.GLOWSTONE_DUST, (byte) 0, ParticleEffectSuperHero.class, "&7&oBecome Superman!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType SANTAHAT = new ParticleEffectType("ultracosmetics.particleeffects.santahat", "SantaHat", 2, Particles.REDSTONE, Material.BEACON, (byte) 0, ParticleEffectSantaHat.class, "&7&oBecome Santa!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType CRUSHEDCANDYCANE = new ParticleEffectType("ultracosmetics.particleeffects.crushedcandycane", "CrushedCandyCane", 1, Particles.ITEM_CRACK, Material.INK_SACK, (byte) 1, ParticleEffectCrushedCandyCane.class, "&7&oThere's no such thing as too much\n&7&oChristmas Candy. Do not listen\n&7&oto your dentist.", ServerVersion.v1_8_R1);
	public static final ParticleEffectType ENDERAURA = new ParticleEffectType("ultracosmetics.particleeffects.enderaura", "EnderAura", 1, Particles.PORTAL, Material.EYE_OF_ENDER, (byte) 0, ParticleEffectEnderAura.class, "&7&oThese mystic particle attach" + " to\n&7&oonly the most legendary of players!", ServerVersion.v1_8_R1);
	public static final ParticleEffectType FLAMEFAIRY = new ParticleEffectType("ultracosmetics.particleeffects.flamefairy", "FlameFairy", 1, Particles.FLAME, Material.BLAZE_POWDER, (byte) 0, ParticleEffectFlameFairy.class, "&7&oHEY!!", ServerVersion.v1_8_R1);
	
	private Particles effect;
	private int repeatDelay;
	
	private ParticleEffectType(String permission, String configName, int repeatDelay, Particles effect, Material material, byte data, Class<? extends ParticleEffect> clazz, String defaultDesc, ServerVersion baseVersion) {
		super(Category.EFFECTS, configName, permission, defaultDesc, material, data, clazz, baseVersion);
		this.repeatDelay = repeatDelay;
		this.effect = effect;
		
		VALUES.add(this);
	}
	
	public Particles getEffect() {
		return effect;
	}
	
	public int getRepeatDelay() {
		return repeatDelay;
	}
}
