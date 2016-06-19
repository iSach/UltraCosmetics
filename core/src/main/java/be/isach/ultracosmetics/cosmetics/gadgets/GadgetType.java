package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 01/12/15.
 */
public enum GadgetType {

    BATBLASTER(Material.IRON_BARDING, (byte) 0, 8, "ultracosmetics.gadgets.batblaster", "BatBlaster", "&7&oLaunch waves of annoying bats\n&7&oto people you don't like!", GadgetBatBlaster.class),
    CHICKENATOR(Material.COOKED_CHICKEN, (byte) 0, 6, "ultracosmetics.gadgets.chickenator", "Chickenator", "&7&oShoot, boom, KFC.", GadgetChickenator.class),
    COLORBOMB(Material.WOOL, (byte) 3, 25, "ultracosmetics.gadgets.colorbomb", "ColorBomb", "&7&oA colorful bomb!", GadgetColorBomb.class),
    DISCOBALL(Material.BEACON, (byte) 0, 45, "ultracosmetics.gadgets.discoball", "DiscoBall", "&7&oJust, dance!", GadgetDiscoBall.class),
    ETHEREALPEARL(Material.ENDER_PEARL, (byte) 0, 2, "ultracosmetics.gadgets.etherealpearl", "EtherealPearl", "&7&oTake a ride through the skies" +
            "\n&7&oon your very own Ethereal Pearl!", GadgetEtherealPearl.class),
    FLESHHOOK(Material.TRIPWIRE_HOOK, (byte) 0, 5, "ultracosmetics.gadgets.fleshhook", "FleshHook", "&7&oMake new friends by throwing a hook" +
            "\n&7&ointo their face and pulling them\n&7&otowards you!", GadgetFleshHook.class),
    MELONTHROWER(Material.MELON_BLOCK, (byte) 0, 2, "ultracosmetics.gadgets.melonthrower", "MelonThrower", "&7&oDeliciously fun!", GadgetMelonThrower.class),
    BLIZZARDBLASTER(Material.PACKED_ICE, (byte) 0, 12, "ultracosmetics.gadgets.blizzardblaster", "BlizzardBlaster", "&7&oLet it go!", GadgetBlizzardBlaster.class),
    PORTALGUN(Material.REDSTONE_COMPARATOR, (byte) 0, 2, "ultracosmetics.gadgets.portalgun", "PortalGun", "&7&oMomentum, a function of " +
            "mass and velocity,\n&7&ois converved between portals. In Layman''s terms,\n&7&ospeedy thing goes in, speedy thing goes out.", GadgetPortalGun.class),
    EXPLOSIVESHEEP(Material.SHEARS, (byte) 0, 25, "ultracosmetics.gadgets.explosivesheep", "ExplosiveSheep", "&7&oAre you sure it's supposed\n&7&oto flicker like that?", GadgetExplosiveSheep.class),
    PAINTBALLGUN(Material.DIAMOND_BARDING, (byte) 0, 0.5, "ultracosmetics.gadgets.paintballgun", "PaintballGun", "&7&oPEW PEW PEW PEW!!!", GadgetPaintballGun.class),
    THORHAMMER(Material.IRON_AXE, (byte) 0, 8, "ultracosmetics.gadgets.thorhammer", "ThorHammer", "&7&oGet the real Mjölnir", GadgetThorHammer.class),
    ANTIGRAVITY(Material.EYE_OF_ENDER, (byte) 0, 30, "ultracosmetics.gadgets.antigravity", "AntiGravity", "&7&oYou don't like gravity?" +
            "\n&7&oThen, this gadget is made for you!", GadgetAntiGravity.class),
    SMASHDOWN(Material.FIREWORK_CHARGE, (byte) 0, 15, "ultracosmetics.gadgets.smashdown", "SmashDown", "&7&oAND HIS NAME IS... JOHN CENA!!", GadgetSmashDown.class),
    ROCKET(Material.FIREWORK, (byte) 0, 60, "ultracosmetics.gadgets.rocket", "Rocket", "&7&oHouston, we have got a problem..", GadgetRocket.class),
    BLACKHOLE(Material.STAINED_CLAY, (byte) 15, 35, "ultracosmetics.gadgets.blackhole", "BlackHole", "&7&oYou should not get caught by it..", GadgetBlackHole.class),
    TSUNAMI(Material.WATER_BUCKET, (byte) 0, 12, "ultracosmetics.gadgets.tsunami", "Tsunami", "&9&oTSUNAMI!!\n&7&oJUMP!\n&7&oLet's go!", GadgetTsunami.class),
    TNT(Material.TNT, (byte) 0, 10, "ultracosmetics.gadgets.tnt", "TNT", "&7&oBlow some people up!\n&7&oKABOOM!", GadgetTNT.class),
    FUNGUN(Material.BLAZE_ROD, (byte) 0, 4, "ultracosmetics.gadgets.fungun", "FunGun", "&7&oWoow! So much fun in a gun!", GadgetFunGun.class),
    PARACHUTE(Material.LEASH, (byte) 0, 60, "ultracosmetics.gadgets.parachute", "Parachute", "&7&oGERONIMOooo!", GadgetParachute.class),
    QUAKEGUN(Material.DIAMOND_HOE, (byte) 0, 3, "ultracosmetics.gadgets.quakegun", "QuakeGun", "&7&oGet a real Rail Gun" +
            "\n&7&oand strike players and mobs!", GadgetQuakeGun.class),
    GHOSTPARTY(Material.SKULL_ITEM, (byte) 0, 45, "ultracosmetics.gadgets.ghostparty", "GhostParty", "&7&oWho Ya Gonna Call?\n&f&lGHOST &4&lBUSTERS!",
            GadgetGhostParty.class),
    FIREWORK(Material.FIREWORK, (byte) 0, 0.2, "ultracosmetics.gadgets.firework", "Firework", "&7&oNeed to celebrate?\n&7&oUse fireworks!",
            GadgetFirework.class),
    CHRISTMASTREE(Material.LONG_GRASS, (byte) 2, 20, "ultracosmetics.gadgets.christmastree", "ChristmasTree", "&7&oHere is a Christmas" +
            "\n&7&oTree for you!", GadgetChristmasTree.class),
    FREEZECANNON(Material.ICE, (byte) 0, 8, "ultracosmetics.gadgets.freezecannon", "FreezeCannon", "&7&oTransform the floor into a rink!",
            GadgetFreezeCannon.class),
    SNOWBALL(Material.SNOW_BALL, (byte) 0, 0.5, "ultracosmetics.gadgets.snowball", "Snowball", "&7&oJoin in on the festive fun by\n" +
            "&7&othrowing snow at people!", GadgetSnowball.class),
    PARTYPOPPER(Material.GOLDEN_CARROT, (byte) 0, 2, "ultracosmetics.gadgets.partypopper", "PartyPopper",
            "&7&oCelebrate by blasting confetti into\n&7&opeoples' eyes!", GadgetPartyPopper.class),
    TRAMPOLINE(Material.WOOL, (byte) 11, 75, "ultracosmetics.gadgets.trampoline", "Trampoline", "&7&oConstructs a trampoline!" +
            "\n&7&othat sends you and your\n&7&ofriends into air!", GadgetTrampoline.class);

    String permission;
    public String configName;
    Class<? extends Gadget> clazz;
    private Material material;
    private byte data;
    private double countdown;
    private String description;
    private boolean affectPlayers;

    public static List<GadgetType> gadgetTypes = new ArrayList<>();

    GadgetType(Material material, byte data, double defaultCountdown, String permission, String configName, String defaultDesc, Class<? extends Gadget> clazz) {
        this.permission = permission;
        this.configName = configName;
        this.clazz = clazz;
        this.material = material;
        this.data = data;
        this.affectPlayers = SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Affect-Players");

        if (SettingsManager.getConfig().get("Gadgets." + configName + ".Cooldown") == null) {
            this.countdown = defaultCountdown;
            SettingsManager.getConfig().set("Gadgets." + configName + ".Cooldown", defaultCountdown);
        } else
            this.countdown = Double.valueOf(String.valueOf(SettingsManager.getConfig().get("Gadgets." + configName + ".Cooldown")));
        if (SettingsManager.getConfig().get("Gadgets." + configName + ".Description") == null) {
            this.description = defaultDesc;
            UltraCosmetics.config.addDefault("Gadgets." + configName + ".Description", getDescriptionWithColor(), "Description of this gadget.");
        } else
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Gadgets." + configName + ".Description")));
    }

    public Gadget equip(Player player) {
        Gadget gadget = null;
        try {
            gadget = clazz.getDeclaredConstructor(UUID.class).newInstance(player == null ? null : player.getUniqueId());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return gadget;
    }

    public boolean requiresAmmo() {
        return SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Ammo.Enabled");
    }

    public boolean affectPlayers() {
        return affectPlayers;
    }

    public static List<GadgetType> enabled() {
        return gadgetTypes;
    }

    public String getName() {
        return MessageManager.getMessage("Gadgets." + configName + ".name");
    }

    public String getConfigName() {
        return configName;
    }

    public String getPermission() {
        return permission;
    }

    public byte getData() {
        return data;
    }

    public Material getMaterial() {
        return material;
    }

    public List<String> getDescriptionWithColor() {
        return Arrays.asList(description.split("\n"));
    }

    public double getCountdown() {
        return countdown;
    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n")) {
            desc.add(string.replace('&', '§'));
        }
        return desc;
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Can-Be-Found-In-Treasure-Chests");
    }

    public boolean isEnabled() {
        if(this == ROCKET && UltraCosmetics.getServerVersion().compareTo(ServerVersion.v1_9_R1) >= 0) {
            return false;
        }
        return SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Enabled");
    }
}
