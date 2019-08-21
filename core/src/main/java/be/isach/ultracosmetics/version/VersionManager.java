package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.cosmetics.pets.IPlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.util.ReflectionUtils;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class VersionManager {
    /**
     * If the version of Bukkit/Spigot is 1.13.
     */
    public static boolean IS_VERSION_1_13 = Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14");
    private final String PACKAGE = "be.isach.ultracosmetics";
    private IModule module;
    private ServerVersion serverVersion;
    private IEntityUtil entityUtil;
    private IActionBar actionBarUtil;
    private IItemGlower itemGlower;
    private IFireworkFactory fireworkFactory;
    private IMounts mounts;
    private IPets pets;
    private IMorphs morphs;
    private Constructor<? extends IPlayerFollower> playerFollowerConstructor;
    private Constructor<? extends IAnvilGUI> anvilGUIConstructor;
    private IPathfinderUtil pathfinderUtil;

    public VersionManager(ServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }

    @SuppressWarnings("unchecked")
    public void load() throws ReflectiveOperationException {
        module = loadModule("Module");
        entityUtil = loadModule("EntityUtil");
        actionBarUtil = loadModule("ActionBar");
        itemGlower = loadModule("ItemGlower");
        pathfinderUtil = loadModule("PathfinderUtil");
        fireworkFactory = loadModule("FireworkFactory");
        mounts = loadModule("Mounts");
        pets = loadModule("Pets");
        morphs = loadModule("Morphs");
        if (serverVersion.compareTo(ServerVersion.v1_14_R1) >= 0)
            anvilGUIConstructor = (Constructor<IAnvilGUI>) ReflectionUtils.getConstructor(Class.forName(PACKAGE + "." + serverVersion + ".AnvilGUI"), Player.class, String.class, Boolean.class, Consumer.class, BiFunction.class);
        else
            anvilGUIConstructor = (Constructor<IAnvilGUI>) ReflectionUtils.getConstructor(Class.forName(PACKAGE + "." + serverVersion + ".AnvilGUI"), Player.class, AAnvilGUI.AnvilClickEventHandler.class);
        anvilGUIConstructor.setAccessible(true);
        playerFollowerConstructor = (Constructor<? extends IPlayerFollower>) ReflectionUtils.getConstructor(Class.forName(PACKAGE + "." + serverVersion + ".pets.PlayerFollower"), Pet.class, Player.class);
        playerFollowerConstructor.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    private <T> T loadModule(String name) throws ReflectiveOperationException {
        return (T) ReflectionUtils
                .instantiateObject(Class.forName(PACKAGE + "." + serverVersion + "." + name));
    }

    public IEntityUtil getEntityUtil() {
        return entityUtil;
    }

    public IActionBar getActionBarUtil() {
        return actionBarUtil;
    }

    public IItemGlower getItemGlower() {
        return itemGlower;
    }

    public IFireworkFactory getFireworkFactory() {
        return fireworkFactory;
    }

    public IMounts getMounts() {
        return mounts;
    }

    public AAnvilGUI newAnvilGUI(Player player, AAnvilGUI.AnvilClickEventHandler handler) {
        try {
            return (AAnvilGUI) anvilGUIConstructor.newInstance(player, handler);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IAnvilGUI newAnvilGUI(Player player, String text, Consumer<Player> closeListener, BiFunction<Player, String, AAnvilGUI.Response> completeFunction) {
        try {
            return anvilGUIConstructor.newInstance(player, text, true, closeListener, completeFunction);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IPlayerFollower newPlayerFollower(Pet pet, Player player) {
        try {
            return playerFollowerConstructor.newInstance(pet, player);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IPathfinderUtil getPathfinderUtil() {
        return pathfinderUtil;
    }

    public IPets getPets() {
        return pets;
    }

    public IMorphs getMorphs() {
        return morphs;
    }

    public IModule getModule() {
        return module;
    }
}
