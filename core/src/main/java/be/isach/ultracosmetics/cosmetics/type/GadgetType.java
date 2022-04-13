package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.*;
import be.isach.ultracosmetics.util.ServerVersion;
import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gadget types.
 *
 * @author iSach
 * @since 12-01-2015
 */
public class GadgetType extends CosmeticType<Gadget> {

    private final static List<GadgetType> ENABLED = new ArrayList<>();
    private final static List<GadgetType> VALUES = new ArrayList<>();

    public static List<GadgetType> enabled() {
        return ENABLED;
    }

    public static List<GadgetType> values() {
        return VALUES;
    }

    public static GadgetType valueOf(String s) {
        return valueOf(s, false);
    }

    public static GadgetType valueOf(String s, boolean onlyEnabled) {
        s = s.toLowerCase();
        String finalS = s;
        Stream<GadgetType> stream = VALUES.stream().filter(gadgetType -> gadgetType.getConfigName().equalsIgnoreCase(finalS));
        if(onlyEnabled) {
            stream.filter(gadgetType -> gadgetType.isEnabled());
        }
        Optional<GadgetType> optionalType = stream.findFirst();
        if(optionalType.isPresent()) {
            return optionalType.get();
        } else {
            stream = VALUES.stream().filter(gadgetType -> gadgetType.getConfigName().toLowerCase().startsWith(finalS));
            if(onlyEnabled) {
                stream.filter(gadgetType -> gadgetType.isEnabled());
            }
            Optional<GadgetType> bestMatchOptional = stream.findFirst();
            if(bestMatchOptional.isPresent())
                return bestMatchOptional.get();
            return null;
        }
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    private final double countdown;
    private final int runTime;
    // whether affecting players is enabled in the config
    private final boolean affectPlayersEnabled;
    // whether the gadget supports the affect-players setting
    private final boolean affectPlayersPossible;

    private GadgetType(XMaterial material, double defaultCountdown, int runTime, String permission, String configName, String defaultDesc, boolean affectPlayersPossible, Class<? extends Gadget> clazz) {
        super(Category.GADGETS, configName, permission, defaultDesc, material, clazz, ServerVersion.earliest());

        this.affectPlayersPossible = affectPlayersPossible;
        this.affectPlayersEnabled = SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Affect-Players");

        if (SettingsManager.getConfig().get("Gadgets." + configName + ".Cooldown") == null) {
            this.countdown = defaultCountdown;
            SettingsManager.getConfig().set("Gadgets." + configName + ".Cooldown", defaultCountdown);
        } else {
            this.countdown = Double.valueOf(String.valueOf(SettingsManager.getConfig().get("Gadgets." + configName + ".Cooldown")));
        }

        this.runTime = runTime;

        VALUES.add(this);
    }

    @Override
    public boolean isEnabled() {
        return !(this == GadgetType.valueOf("etherealpearl") && UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_12_R1) && super.isEnabled();
    }

    public boolean requiresAmmo() {
        return SettingsManager.getConfig().getBoolean("Gadgets." + getConfigName() + ".Ammo.Enabled");
    }

    public boolean affectPlayersEnabled() {
        return affectPlayersEnabled;
    }

    public boolean affectPlayersPossible() {
        return affectPlayersPossible;
    }

    public double getCountdown() {
        // cooldown should not be lower than runtime unless you enjoy bugs
        return Math.max(countdown, runTime);
    }

    public int getRunTime() {
        return runTime;
    }

    public static void register() {
        new GadgetType(XMaterial.IRON_HORSE_ARMOR, 8, 3, "ultracosmetics.gadgets.batblaster", "BatBlaster", "&7&oLaunch waves of annoying bats\n&7&oto people you don't like!", true, GadgetBatBlaster.class);
        new GadgetType(XMaterial.COOKED_CHICKEN, 6, 3, "ultracosmetics.gadgets.chickenator", "Chickenator", "&7&oShoot, boom, KFC.", false, GadgetChickenator.class);
        new GadgetType(XMaterial.BEACON, 45, 20, "ultracosmetics.gadgets.discoball", "DiscoBall", "&7&oJust, dance!", true, GadgetDiscoBall.class);
        new GadgetType(XMaterial.ENDER_PEARL, 2, 0, "ultracosmetics.gadgets.etherealpearl", "EtherealPearl", "&7&oTake a ride through the skies" + "\n&7&oon your very own Ethereal Pearl!", false, GadgetEtherealPearl.class);
        new GadgetType(XMaterial.TRIPWIRE_HOOK, 5, 0, "ultracosmetics.gadgets.fleshhook", "FleshHook", "&7&oMake new friends by throwing a hook" + "\n&7&ointo their face and pulling them\n&7&otowards you!", true, GadgetFleshHook.class);
        new GadgetType(XMaterial.MELON, 2, 0, "ultracosmetics.gadgets.melonthrower", "MelonThrower", "&7&oDeliciously fun!", true, GadgetMelonThrower.class);
        new GadgetType(XMaterial.PACKED_ICE, 12, 2, "ultracosmetics.gadgets.blizzardblaster", "BlizzardBlaster", "&7&oLet it go!", true, GadgetBlizzardBlaster.class);
        new GadgetType(XMaterial.COMPARATOR, 2, 0, "ultracosmetics.gadgets.portalgun", "PortalGun", "&7&oThe cake is a lie!", false, GadgetPortalGun.class);
        new GadgetType(XMaterial.SHEARS, 25, 11, "ultracosmetics.gadgets.explosivesheep", "ExplosiveSheep", "&7&oAre you sure it's supposed\n&7&oto flicker like that?", false, GadgetExplosiveSheep.class);
        new GadgetType(XMaterial.DIAMOND_HORSE_ARMOR, 0.5, 0, "ultracosmetics.gadgets.paintballgun", "PaintballGun", "&7&oPEW PEW PEW PEW!!!", false, GadgetPaintballGun.class);
        // '\u00F6' is an o with two dots over it
        new GadgetType(XMaterial.IRON_AXE, 8, 0, "ultracosmetics.gadgets.thorhammer", "ThorHammer", "&7&oGet the real Mj\u00F6lnir", true, GadgetThorHammer.class);
        new GadgetType(XMaterial.ENDER_EYE, 30, 12, "ultracosmetics.gadgets.antigravity", "AntiGravity", "&7&oYou don't like gravity?" + "\n&7&oThen, this gadget is made for you!", false, GadgetAntiGravity.class);
        new GadgetType(XMaterial.FIREWORK_STAR, 15, 0, "ultracosmetics.gadgets.smashdown", "SmashDown", "&7&oAND HIS NAME IS... JOHN CENA!!", true, GadgetSmashDown.class);
        new GadgetType(XMaterial.FIREWORK_ROCKET, 60, 10, "ultracosmetics.gadgets.rocket", "Rocket", "&7&oHouston, we have got a problem..", false, GadgetRocket.class);
        new GadgetType(XMaterial.WATER_BUCKET, 12, 2, "ultracosmetics.gadgets.tsunami", "Tsunami", "&9&oTSUNAMI!!\n&7&oJUMP!\n&7&oLet's go!", true, GadgetTsunami.class);
        new GadgetType(XMaterial.TNT, 10, 0, "ultracosmetics.gadgets.tnt", "TNT", "&7&oBlow some people up!\n&7&oKABOOM!", true, GadgetTNT.class);
        new GadgetType(XMaterial.BLAZE_ROD, 4, 0, "ultracosmetics.gadgets.fungun", "FunGun", "&7&oWoow! So much fun in a gun!", false, GadgetFunGun.class);
        new GadgetType(XMaterial.LEAD, 60, 7, "ultracosmetics.gadgets.parachute", "Parachute", "&7&oGERONIMOooo!", false, GadgetParachute.class);
        new GadgetType(XMaterial.DIAMOND_HOE, 3, 0, "ultracosmetics.gadgets.quakegun", "QuakeGun", "&7&oGet a real Rail Gun" + "\n&7&oand strike players and mobs!", true, GadgetQuakeGun.class);
        new GadgetType(XMaterial.SKELETON_SKULL, 45, 8, "ultracosmetics.gadgets.ghostparty", "GhostParty", "&7&oWho Ya Gonna Call?\n&f&lGHOST &4&lBUSTERS!", false, GadgetGhostParty.class);
        new GadgetType(XMaterial.FIREWORK_ROCKET, 0.2, 0, "ultracosmetics.gadgets.firework", "Firework", "&7&oNeed to celebrate?\n&7&oUse fireworks!", false, GadgetFirework.class);
        new GadgetType(XMaterial.FERN, 20, 10, "ultracosmetics.gadgets.christmastree", "ChristmasTree", "&7&oHere is a Christmas" + "\n&7&oTree for you!", false, GadgetChristmasTree.class);
        new GadgetType(XMaterial.ICE, 8, 3, "ultracosmetics.gadgets.freezecannon", "FreezeCannon", "&7&oTransform the floor into a rink!", false, GadgetFreezeCannon.class);
        new GadgetType(XMaterial.SNOWBALL, 0.5, 0, "ultracosmetics.gadgets.snowball", "Snowball", "&7&oJoin in on the festive fun by\n" + "&7&othrowing snow at people!", false, GadgetSnowball.class);
        new GadgetType(XMaterial.GOLDEN_CARROT, 2, 0, "ultracosmetics.gadgets.partypopper", "PartyPopper", "&7&oCelebrate by blasting confetti into\n&7&opeoples' eyes!", false, GadgetPartyPopper.class);

        new GadgetType(XMaterial.LIGHT_BLUE_WOOL, 25, 7, "ultracosmetics.gadgets.colorbomb", "ColorBomb", "&7&oA colorful bomb!", true, GadgetColorBomb.class);
        new GadgetType(XMaterial.BLUE_WOOL, 75, 12, "ultracosmetics.gadgets.trampoline", "Trampoline", "&7&oConstructs a trampoline!" + "\n&7&othat sends you and your\n&7&ofriends into air!", false, GadgetTrampoline.class);
        new GadgetType(XMaterial.BLACK_TERRACOTTA, 35, 8, "ultracosmetics.gadgets.blackhole", "BlackHole", "&7&oYou should not get caught by it..", true, GadgetBlackHole.class);
    }
}
