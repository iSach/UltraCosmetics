package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.cosmetics.pets.IPlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.util.ReflectionUtils;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class VersionManager {
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
    private Constructor<? extends IPlayerFollower> playerFolowerConstructor;
    private Constructor<? extends AAnvilGUI> anvilGUIConstructor;

    public VersionManager(ServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }


    @SuppressWarnings("unchecked")
    public void load() throws ReflectiveOperationException {
        module = loadModule("Module");
        entityUtil = loadModule("EntityUtil");
        actionBarUtil = loadModule("ActionBar");
        itemGlower = loadModule("ItemGlower");
        fireworkFactory = loadModule("FireworkFactory");
        mounts = loadModule("Mounts");
        pets = loadModule("Pets");
        morphs = loadModule("Morphs");
        anvilGUIConstructor = (Constructor<AAnvilGUI>) ReflectionUtils.getConstructor(Class.forName(PACKAGE + "." + serverVersion + ".AnvilGUI") , Player.class , AAnvilGUI.AnvilClickEventHandler.class);
        anvilGUIConstructor.setAccessible(true);
        playerFolowerConstructor = (Constructor<? extends IPlayerFollower>) ReflectionUtils.getConstructor(Class.forName(PACKAGE + "." + serverVersion + ".pet.PlayerFollower") , Pet.class , Player.class);
        playerFolowerConstructor.setAccessible(true);
    }
    @SuppressWarnings("unchecked")
    private <T> T loadModule(String name) throws ReflectiveOperationException{
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

    public AAnvilGUI newAnvilGUI(Player player, AAnvilGUI.AnvilClickEventHandler handler){
        try {
            return anvilGUIConstructor.newInstance(player , handler);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IPlayerFollower newPlayerFollower(Pet pet , Player player){
        try {
            return playerFolowerConstructor.newInstance(pet , player);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
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
