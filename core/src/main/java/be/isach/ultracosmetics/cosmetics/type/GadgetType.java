package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.*;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.UCMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gadget types.
 *
 * @author iSach
 * @since 12-01-2015
 */
public class GadgetType extends CosmeticMatType<Gadget> {

    private final static List<GadgetType> ENABLED = new ArrayList<>();
    private final static List<GadgetType> VALUES = new ArrayList<>();

    public static List<GadgetType> enabled() {
        return ENABLED;
    }

    public static List<GadgetType> values() {
        return VALUES;
    }

    public static GadgetType valueOf(String s) {
        for (GadgetType gadgetType : VALUES) {
            if (gadgetType.getConfigName().equalsIgnoreCase(s)) return gadgetType;
        }
        return null;
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    private double countdown;
    private boolean affectPlayers;

    GadgetType(UCMaterial material, double defaultCountdown, String permission, String configName, String defaultDesc, Class<? extends Gadget> clazz, ServerVersion baseVersion) {
        super(Category.GADGETS, configName, permission, defaultDesc, material, clazz, baseVersion);

        this.affectPlayers = SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Affect-Players");

        if (SettingsManager.getConfig().get("Gadgets." + configName + ".Cooldown") == null) {
            this.countdown = defaultCountdown;
            SettingsManager.getConfig().set("Gadgets." + configName + ".Cooldown", defaultCountdown);
        } else {
            this.countdown = Double.valueOf(String.valueOf(SettingsManager.getConfig().get("Gadgets." + configName + ".Cooldown")));
        }

        VALUES.add(this);
    }

    public boolean requiresAmmo() {
        return SettingsManager.getConfig().getBoolean("Gadgets." + getConfigName() + ".Ammo.Enabled");
    }

    public boolean affectPlayers() {
        return affectPlayers;
    }

    public double getCountdown() {
        return countdown;
    }

    public static void register() {
        new GadgetType(UCMaterial.IRON_HORSE_ARMOR, 8, "ultracosmetics.gadgets.batblaster", "BatBlaster", "&7&oLaunch waves of annoying bats\n&7&oto people you don't like!", GadgetBatBlaster.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.COOKED_CHICKEN, 6, "ultracosmetics.gadgets.chickenator", "Chickenator", "&7&oShoot, boom, KFC.", GadgetChickenator.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.BEACON, 45, "ultracosmetics.gadgets.discoball", "DiscoBall", "&7&oJust, dance!", GadgetDiscoBall.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.ENDER_PEARL, 2, "ultracosmetics.gadgets.etherealpearl", "EtherealPearl", "&7&oTake a ride through the skies" + "\n&7&oon your very own Ethereal Pearl!", GadgetEtherealPearl.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.TRIPWIRE_HOOK, 5, "ultracosmetics.gadgets.fleshhook", "FleshHook", "&7&oMake new friends by throwing a hook" + "\n&7&ointo their face and pulling them\n&7&otowards you!", GadgetFleshHook.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.MELON, 2, "ultracosmetics.gadgets.melonthrower", "MelonThrower", "&7&oDeliciously fun!", GadgetMelonThrower.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.PACKED_ICE, 12, "ultracosmetics.gadgets.blizzardblaster", "BlizzardBlaster", "&7&oLet it go!", GadgetBlizzardBlaster.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.COMPARATOR, 2, "ultracosmetics.gadgets.portalgun", "PortalGun", "&7&oThe cake is a lie!", GadgetPortalGun.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.SHEARS, 25, "ultracosmetics.gadgets.explosivesheep", "ExplosiveSheep", "&7&oAre you sure it's supposed\n&7&oto flicker like that?", GadgetExplosiveSheep.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.DIAMOND_HORSE_ARMOR, 0.5, "ultracosmetics.gadgets.paintballgun", "PaintballGun", "&7&oPEW PEW PEW PEW!!!", GadgetPaintballGun.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.IRON_AXE, 8, "ultracosmetics.gadgets.thorhammer", "ThorHammer", "&7&oGet the real Mjölnir", GadgetThorHammer.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.ENDER_EYE, 30, "ultracosmetics.gadgets.antigravity", "AntiGravity", "&7&oYou don't like gravity?" + "\n&7&oThen, this gadget is made for you!", GadgetAntiGravity.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.FIREWORK_STAR, 15, "ultracosmetics.gadgets.smashdown", "SmashDown", "&7&oAND HIS NAME IS... JOHN CENA!!", GadgetSmashDown.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.FIREWORK_ROCKET, 60, "ultracosmetics.gadgets.rocket", "Rocket", "&7&oHouston, we have got a problem..", GadgetRocket.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.WATER_BUCKET, 12, "ultracosmetics.gadgets.tsunami", "Tsunami", "&9&oTSUNAMI!!\n&7&oJUMP!\n&7&oLet's go!", GadgetTsunami.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.TNT, 10, "ultracosmetics.gadgets.tnt", "TNT", "&7&oBlow some people up!\n&7&oKABOOM!", GadgetTNT.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.BLAZE_ROD, 4, "ultracosmetics.gadgets.fungun", "FunGun", "&7&oWoow! So much fun in a gun!", GadgetFunGun.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.LEAD, 60, "ultracosmetics.gadgets.parachute", "Parachute", "&7&oGERONIMOooo!", GadgetParachute.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.DIAMOND_HOE, 3, "ultracosmetics.gadgets.quakegun", "QuakeGun", "&7&oGet a real Rail Gun" + "\n&7&oand strike players and mobs!", GadgetQuakeGun.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.SKELETON_SKULL, 45, "ultracosmetics.gadgets.ghostparty", "GhostParty", "&7&oWho Ya Gonna Call?\n&f&lGHOST &4&lBUSTERS!", GadgetGhostParty.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.FIREWORK_ROCKET, 0.2, "ultracosmetics.gadgets.firework", "Firework", "&7&oNeed to celebrate?\n&7&oUse fireworks!", GadgetFirework.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.FERN, 20, "ultracosmetics.gadgets.christmastree", "ChristmasTree", "&7&oHere is a Christmas" + "\n&7&oTree for you!", GadgetChristmasTree.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.ICE, 8, "ultracosmetics.gadgets.freezecannon", "FreezeCannon", "&7&oTransform the floor into a rink!", GadgetFreezeCannon.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.SNOWBALL, 0.5, "ultracosmetics.gadgets.snowball", "Snowball", "&7&oJoin in on the festive fun by\n" + "&7&othrowing snow at people!", GadgetSnowball.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.GOLDEN_CARROT, 2, "ultracosmetics.gadgets.partypopper", "PartyPopper", "&7&oCelebrate by blasting confetti into\n&7&opeoples' eyes!", GadgetPartyPopper.class, ServerVersion.v1_8_R1);

        new GadgetType(UCMaterial.LIGHT_BLUE_WOOL, 25, "ultracosmetics.gadgets.colorbomb", "ColorBomb", "&7&oA colorful bomb!", GadgetColorBomb.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.BLUE_WOOL, 75, "ultracosmetics.gadgets.trampoline", "Trampoline", "&7&oConstructs a trampoline!" + "\n&7&othat sends you and your\n&7&ofriends into air!", GadgetTrampoline.class, ServerVersion.v1_8_R1);
        new GadgetType(UCMaterial.BLACK_TERRACOTTA, 35, "ultracosmetics.gadgets.blackhole", "BlackHole", "&7&oYou should not get caught by it..", GadgetBlackHole.class, ServerVersion.v1_8_R1);

        /*if (VersionManager.IS_VERSION_1_13) {
			new GadgetType(BlockUtils.getBlockByColor("WOOL", (byte) 3), (byte) 3, 25, "ultracosmetics.gadgets.colorbomb", "ColorBomb", "&7&oA colorful bomb!", GadgetColorBomb.class, ServerVersion.v1_13_R1);
			new GadgetType(BlockUtils.getBlockByColor("WOOL", (byte) 11), (byte) 11, 75, "ultracosmetics.gadgets.trampoline", "Trampoline", "&7&oConstructs a trampoline!" + "\n&7&othat sends you and your\n&7&ofriends into air!", GadgetTrampoline.class, ServerVersion.v1_13_R1);
			new GadgetType(BlockUtils.getBlockByColor("STAINED_CLAY", (byte) 15), (byte) 15, 35, "ultracosmetics.gadgets.blackhole", "BlackHole", "&7&oYou should not get caught by it..", GadgetBlackHole.class, ServerVersion.v1_13_R1);
		} else {
			new GadgetType(BlockUtils.getBlockByColor("WOOL", (byte) 3), (byte) 3, 25, "ultracosmetics.gadgets.colorbomb", "ColorBomb", "&7&oA colorful bomb!", GadgetColorBomb.class, ServerVersion.v1_8_R1);
			new GadgetType(BlockUtils.getBlockByColor("WOOL", (byte) 11), (byte) 11, 75, "ultracosmetics.gadgets.trampoline", "Trampoline", "&7&oConstructs a trampoline!" + "\n&7&othat sends you and your\n&7&ofriends into air!", GadgetTrampoline.class, ServerVersion.v1_8_R1);
			new GadgetType(BlockUtils.getBlockByColor("STAINED_CLAY", (byte) 15), (byte) 15, 35, "ultracosmetics.gadgets.blackhole", "BlackHole", "&7&oYou should not get caught by it..", GadgetBlackHole.class, ServerVersion.v1_8_R1);
		}*/

    }
}
