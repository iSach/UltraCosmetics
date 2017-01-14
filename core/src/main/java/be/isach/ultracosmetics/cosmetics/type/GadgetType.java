package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.*;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gadget types.
 * 
 * @author 	iSach
 * @since 	12-01-2015
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

    public static GadgetType getByName(String s) {
        try {
            return VALUES.stream().filter(value -> value.getName().equalsIgnoreCase(s)).findFirst().get();
        } catch (Exception exc) {
            return null;
        }
    }

    public static void checkEnabled() {
        ENABLED.addAll(values().stream().filter(CosmeticType::isEnabled).collect(Collectors.toList()));
    }

    public static final GadgetType BATBLASTER = new GadgetType(Material.IRON_BARDING, (byte) 0, 8, "ultracosmetics.gadgets.batblaster", "BatBlaster", "&7&oLaunch waves of annoying bats\n&7&oto people you don't like!", GadgetBatBlaster.class);
    public static final GadgetType CHICKENATOR = new GadgetType(Material.COOKED_CHICKEN, (byte) 0, 6, "ultracosmetics.gadgets.chickenator", "Chickenator", "&7&oShoot, boom, KFC.", GadgetChickenator.class);
    public static final GadgetType COLORBOMB = new GadgetType(Material.WOOL, (byte) 3, 25, "ultracosmetics.gadgets.colorbomb", "ColorBomb", "&7&oA colorful bomb!", GadgetColorBomb.class);
    public static final GadgetType DISCOBALL = new GadgetType(Material.BEACON, (byte) 0, 45, "ultracosmetics.gadgets.discoball", "DiscoBall", "&7&oJust, dance!", GadgetDiscoBall.class);
    public static final GadgetType ETHEREALPEARL = new GadgetType(Material.ENDER_PEARL, (byte) 0, 2, "ultracosmetics.gadgets.etherealpearl", "EtherealPearl", "&7&oTake a ride through the skies" + "\n&7&oon your very own Ethereal Pearl!", GadgetEtherealPearl.class);
    public static final GadgetType FLESHHOOK = new GadgetType(Material.TRIPWIRE_HOOK, (byte) 0, 5, "ultracosmetics.gadgets.fleshhook", "FleshHook", "&7&oMake new friends by throwing a hook" + "\n&7&ointo their face and pulling them\n&7&otowards you!", GadgetFleshHook.class);
    public static final GadgetType MELONTHROWER = new GadgetType(Material.MELON_BLOCK, (byte) 0, 2, "ultracosmetics.gadgets.melonthrower", "MelonThrower", "&7&oDeliciously fun!", GadgetMelonThrower.class);
    public static final GadgetType BLIZZARDBLASTER = new GadgetType(Material.PACKED_ICE, (byte) 0, 12, "ultracosmetics.gadgets.blizzardblaster", "BlizzardBlaster", "&7&oLet it go!", GadgetBlizzardBlaster.class);
    public static final GadgetType PORTALGUN = new GadgetType(Material.REDSTONE_COMPARATOR, (byte) 0, 2, "ultracosmetics.gadgets.portalgun", "PortalGun", "&7&oMomentum, a function of " + "mass and velocity,\n&7&ois converved between portals. In Layman''s terms,\n&7&ospeedy thing goes in, speedy thing goes out.", GadgetPortalGun.class);
    public static final GadgetType EXPLOSIVESHEEP = new GadgetType(Material.SHEARS, (byte) 0, 25, "ultracosmetics.gadgets.explosivesheep", "ExplosiveSheep", "&7&oAre you sure it's supposed\n&7&oto flicker like that?", GadgetExplosiveSheep.class);
    public static final GadgetType PAINTBALLGUN = new GadgetType(Material.DIAMOND_BARDING, (byte) 0, 0.5, "ultracosmetics.gadgets.paintballgun", "PaintballGun", "&7&oPEW PEW PEW PEW!!!", GadgetPaintballGun.class);
    public static final GadgetType THORHAMMER = new GadgetType(Material.IRON_AXE, (byte) 0, 8, "ultracosmetics.gadgets.thorhammer", "ThorHammer", "&7&oGet the real Mjölnir", GadgetThorHammer.class);
    public static final GadgetType ANTIGRAVITY = new GadgetType(Material.EYE_OF_ENDER, (byte) 0, 30, "ultracosmetics.gadgets.antigravity", "AntiGravity", "&7&oYou don't like gravity?" + "\n&7&oThen, this gadget is made for you!", GadgetAntiGravity.class);
    public static final GadgetType SMASHDOWN = new GadgetType(Material.FIREWORK_CHARGE, (byte) 0, 15, "ultracosmetics.gadgets.smashdown", "SmashDown", "&7&oAND HIS NAME IS... JOHN CENA!!", GadgetSmashDown.class);
    public static final GadgetType ROCKET = new GadgetType(Material.FIREWORK, (byte) 0, 60, "ultracosmetics.gadgets.rocket", "Rocket", "&7&oHouston, we have got a problem..", GadgetRocket.class);
    public static final GadgetType BLACKHOLE = new GadgetType(Material.STAINED_CLAY, (byte) 15, 35, "ultracosmetics.gadgets.blackhole", "BlackHole", "&7&oYou should not get caught by it..", GadgetBlackHole.class);
    public static final GadgetType TSUNAMI = new GadgetType(Material.WATER_BUCKET, (byte) 0, 12, "ultracosmetics.gadgets.tsunami", "Tsunami", "&9&oTSUNAMI!!\n&7&oJUMP!\n&7&oLet's go!", GadgetTsunami.class);
    public static final GadgetType TNT = new GadgetType(Material.TNT, (byte) 0, 10, "ultracosmetics.gadgets.tnt", "TNT", "&7&oBlow some people up!\n&7&oKABOOM!", GadgetTNT.class);
    public static final GadgetType FUNGUN = new GadgetType(Material.BLAZE_ROD, (byte) 0, 4, "ultracosmetics.gadgets.fungun", "FunGun", "&7&oWoow! So much fun in a gun!", GadgetFunGun.class);
    public static final GadgetType PARACHUTE = new GadgetType(Material.LEASH, (byte) 0, 60, "ultracosmetics.gadgets.parachute", "Parachute", "&7&oGERONIMOooo!", GadgetParachute.class);
    public static final GadgetType QUAKEGUN = new GadgetType(Material.DIAMOND_HOE, (byte) 0, 3, "ultracosmetics.gadgets.quakegun", "QuakeGun", "&7&oGet a real Rail Gun" + "\n&7&oand strike players and mobs!", GadgetQuakeGun.class);
    public static final GadgetType GHOSTPARTY = new GadgetType(Material.SKULL_ITEM, (byte) 0, 45, "ultracosmetics.gadgets.ghostparty", "GhostParty", "&7&oWho Ya Gonna Call?\n&f&lGHOST &4&lBUSTERS!", GadgetGhostParty.class);
    public static final GadgetType FIREWORK = new GadgetType(Material.FIREWORK, (byte) 0, 0.2, "ultracosmetics.gadgets.firework", "Firework", "&7&oNeed to celebrate?\n&7&oUse fireworks!", GadgetFirework.class);
    public static final GadgetType CHRISTMASTREE = new GadgetType(Material.LONG_GRASS, (byte) 2, 20, "ultracosmetics.gadgets.christmastree", "ChristmasTree", "&7&oHere is a Christmas" + "\n&7&oTree for you!", GadgetChristmasTree.class);
    public static final GadgetType FREEZECANNON = new GadgetType(Material.ICE, (byte) 0, 8, "ultracosmetics.gadgets.freezecannon", "FreezeCannon", "&7&oTransform the floor into a rink!", GadgetFreezeCannon.class);
    public static final GadgetType SNOWBALL = new GadgetType(Material.SNOW_BALL, (byte) 0, 0.5, "ultracosmetics.gadgets.snowball", "Snowball", "&7&oJoin in on the festive fun by\n" + "&7&othrowing snow at people!", GadgetSnowball.class);
    public static final GadgetType PARTYPOPPER = new GadgetType(Material.GOLDEN_CARROT, (byte) 0, 2, "ultracosmetics.gadgets.partypopper", "PartyPopper", "&7&oCelebrate by blasting confetti into\n&7&opeoples' eyes!", GadgetPartyPopper.class);
    public static final GadgetType TRAMPOLINE = new GadgetType(Material.WOOL, (byte) 11, 75, "ultracosmetics.gadgets.trampoline", "Trampoline", "&7&oConstructs a trampoline!" + "\n&7&othat sends you and your\n&7&ofriends into air!", GadgetTrampoline.class);

    private double countdown;
    private boolean affectPlayers;

    GadgetType(Material material, byte data, double defaultCountdown, String permission, String configName, String defaultDesc, Class<? extends Gadget> clazz) {
        super(Category.GADGETS, configName, permission, defaultDesc, material, data, clazz);

        this.affectPlayers = SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Affect-Players");

        if (SettingsManager.getConfig().get("Gadgets." + configName + ".Cooldown") == null) {
            this.countdown = defaultCountdown;
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
}
