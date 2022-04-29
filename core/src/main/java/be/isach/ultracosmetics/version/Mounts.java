package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.util.ServerVersion;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;

public class Mounts {
    private final boolean is18;
    private final String packageName;
    public Mounts() {
        is18 = UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_8_R3;
        packageName = "be.isach.ultracosmetics.cosmetics.mounts." + (is18 ? "pretend" : "abstract") + "horse";
    }

    // These should all only be called once so no need to cache result
    public Class<? extends Mount> getHorrorClass() {
        return getMountClass("InfernalHorror");
    }

    public Class<? extends Mount> getWalkingDeadClass() {
        return getMountClass("WalkingDead");
    }

    public Class<? extends Mount> getRudolphClass() {
        return getMountClass("Rudolph");
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Mount> getMountClass(String name) {
        try {
            return (Class<? extends Mount>) Class.forName(packageName + ".Mount" + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EntityType getHorrorType() {
        return is18 ? EntityType.HORSE : EntityType.SKELETON_HORSE;
    }

    public EntityType getWalkingDeadType() {
        return is18 ? EntityType.HORSE : EntityType.ZOMBIE_HORSE;
    }

    public EntityType getRudolphType() {
        return is18 ? EntityType.HORSE : EntityType.MULE;
    }

    public Class<? extends LivingEntity> getAbstractHorseClass() {
        return is18 ? Horse.class : AbstractHorse.class;
    }

    public boolean isAbstractHorse(EntityType type) {
        return getAbstractHorseClass().isAssignableFrom(type.getEntityClass());
    }
}
