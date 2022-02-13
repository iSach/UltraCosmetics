package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.util.ReflectionUtils;
import be.isach.ultracosmetics.util.ServerVersion;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class VersionManager {
    public static boolean IS_VERSION_1_13 = UltraCosmeticsData.get().getServerVersion().is113();
    public static final String PACKAGE = "be.isach.ultracosmetics";
    // TODO: value as Pair or something?
    private static final Map<String,Integer> WORLD_MIN_HEIGHTS = new HashMap<>();
    private static final Map<String,Integer> WORLD_MAX_HEIGHTS = new HashMap<>();
    private IModule module;
    private ServerVersion serverVersion;
    private IEntityUtil entityUtil;
    private IAncientUtil ancientUtil;
    private IFireworkFactory fireworkFactory;
    private Mounts mounts;
    private IPets pets;
    private IMorphs morphs;
    private Constructor<? extends IPlayerFollower> playerFollowerConstructor;

    public VersionManager(ServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }

    @SuppressWarnings("unchecked")
    public void load() throws ReflectiveOperationException {
        module = loadModule("Module");
        entityUtil = loadModule("EntityUtil");
        mounts = new Mounts();
        if (serverVersion == ServerVersion.v1_8_R3) {
            ancientUtil = loadModule("AncientUtil");
        } else {
            ancientUtil = new APIAncientUtil();
        }
        fireworkFactory = loadModule("FireworkFactory");
        pets = loadModule("Pets");
        morphs = loadModule("Morphs");
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

    public IAncientUtil getAncientUtil() {
        return ancientUtil;
    }

    public IFireworkFactory getFireworkFactory() {
        return fireworkFactory;
    }

    public Mounts getMounts() {
        return mounts;
    }

    public IPlayerFollower newPlayerFollower(Pet pet, Player player) {
        try {
            return playerFollowerConstructor.newInstance(pet, player);
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

    public IAnvilWrapper getAnvilWrapper() {
        try {
            return loadModule("AnvilWrapper");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getWorldMinHeight(World world) {
        return WORLD_MIN_HEIGHTS.computeIfAbsent(world.getName(), w -> {
            try {
                return world.getMinHeight();
            } catch (NoSuchMethodError ex) {
                return 0;
            }
        });
    }

    public int getWorldMaxHeight(World world) {
        return WORLD_MAX_HEIGHTS.computeIfAbsent(world.getName(), w -> {
            try {
                return world.getMaxHeight();
            } catch (NoSuchMethodError ex) {
                return 255;
            }
        });
    }
}
