package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Created by Sacha on 01/12/15.
 */
public enum GadgetType {

    BAT_BLASTER("ultracosmetics.gadgets.batblaster", "BatBlaster", GadgetBatBlaster.class),
    CHICKENATOR("ultracosmetics.gadgets.chickenator", "Chickenator", GadgetChickenator.class),
    COLOR_BOMB("ultracosmetics.gadgets.colorbomb", "ColorBomb", GadgetColorBomb.class),
    DISCO_BALL("ultracosmetics.gadgets.discoball", "DiscoBall", GadgetDiscoBall.class),
    ETHEREAL_PEARL("ultracosmetics.gadgets.etherealpearl", "EtherealPearl", GadgetEtherealPearl.class),
    FLESH_HOOK("ultracosmetics.gadgets.fleshhook", "FleshHook", GadgetFleshHook.class),
    MELON_THROWER("ultracosmetics.gadgets.melonthrower", "MelonThrower", GadgetMelonThrower.class),
    BLIZZARD_BLASTER("ultracosmetics.gadgets.blizzardblaster", "BlizzardBlaster", GadgetBlizzardBlaster.class),
    PORTAL_GUN("ultracosmetics.gadgets.portalgun", "PortalGun", GadgetPortalGun.class),
    EXPLOSIVE_SHEEP("ultracosmetics.gadgets.explosivesheep", "ExplosiveSheep", GadgetExplosiveSheep.class),
    PAINTBALL_GUN("ultracosmetics.gadgets.paintballgun", "PaintballGun", GadgetPaintballGun.class),
    THOR_HAMMER("ultracosmetics.gadgets.thorhammer", "ThorHammer", GadgetThorHammer.class),
    ANTI_GRAVITY("ultracosmetics.gadgets.antigravity", "AntiGravity", GadgetAntiGravity.class),
    SMASH_DOWN("ultracosmetics.gadgets.smashdown", "SmashDown", GadgetSmashDown.class),
    ROCKET("ultracosmetics.gadgets.rocket", "Rocket", GadgetRocket.class),
    BLACK_HOLE("ultracosmetics.gadgets.blackhole", "BlackHole", GadgetBlackHole.class),
    TSUNAMI("ultracosmetics.gadgets.tsunami", "Tsunami", GadgetTsunami.class),
    TNT("ultracosmetics.gadgets.tnt", "TNT", GadgetTNT.class),
    FUN_GUN("ultracosmetics.gadgets.fungun", "FunGun", GadgetFunGun.class),
    PARACHUTE("ultracosmetics.gadgets.parachute", "Parachute", GadgetParachute.class),
    QUAKE_GUN("ultracosmetics.gadgets.quakegun", "QuakeGun", GadgetQuakeGun.class),
    GHOST_PARTY("ultracosmetics.gadgets.ghostparty", "GhostParty", GadgetGhostParty.class),
    FIREWORK("ultracosmetics.gadgets.firework", "Firework", GadgetFirework.class),
    CHRISTMAS_TREE("ultracosmetics.gadgets.christmastree", "ChristmasTree", GadgetChristmasTree.class);

    String permission;
    public String configName;
    Class<? extends Gadget> clazz;

    GadgetType(String permission, String configName, Class<? extends Gadget> clazz) {
        this.permission = permission;
        this.configName = configName;
        this.clazz = clazz;
    }

    public Gadget equip(Player player) {
        try {
            clazz.getDeclaredConstructor(UUID.class).newInstance(player.getUniqueId());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new GadgetFirework(null);//TODO
    }

    public boolean requiresAmmo() {
        return SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Ammo.Enabled");
    }

    public String getConfigName() {
        return configName;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Gadgets." + configName + ".Enabled");
    }
}
