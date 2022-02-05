package be.isach.ultracosmetics.v1_8_R3.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_8_R3.customentities.CustomEntities;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;

/**
 * Created by Sacha on 15/03/16.
 */
public abstract class MountCustomEntity<E extends org.bukkit.entity.Entity> extends Mount<E> {

    protected EntityInsentient customEntity;

    public MountCustomEntity(UltraPlayer owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public E spawnEntity() {
        customEntity = getNewEntity();
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        getCustomEntity().setLocation(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        CustomEntities.customEntities.add(getCustomEntity());
        removeAI(customEntity);
        return getEntity();
    }

    private void removeAI(EntityInsentient entity) {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(entity.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(entity.targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entity.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entity.targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (ReflectiveOperationException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.customEntities.remove(customEntity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getEntity() {
        return (E) customEntity.getBukkitEntity();
    }

    public EntityInsentient getCustomEntity() {
        return customEntity;
    }

    public abstract EntityInsentient getNewEntity();
}
