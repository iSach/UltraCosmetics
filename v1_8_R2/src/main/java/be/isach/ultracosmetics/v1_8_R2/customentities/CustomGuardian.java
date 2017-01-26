package be.isach.ultracosmetics.v1_8_R2.customentities;

import net.minecraft.server.v1_8_R2.EntityGuardian;
import net.minecraft.server.v1_8_R2.World;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

import be.isach.ultracosmetics.v1_8_R2.morphs.MorphElderGuardian;

/**
 * @author RadBuilder
 */
public class CustomGuardian extends EntityGuardian {

    private boolean custom;

    public CustomGuardian(World world) {
        super(world);
    }

    public void check() {
        custom = MorphElderGuardian.customEntities.contains(this);
    }

    public void target(ArmorStand armorStand) {
        getDataWatcher().watch(17, armorStand == null ? 0 : ((CraftArmorStand) armorStand).getHandle().getId());
    }

    @Override
    protected String z() {
        if (custom)
            return null;
        else
            return super.z();
    }

    @Override
    public void t_() {
        if (!custom)
            super.t_();
        else
            setHealth(getMaxHealth());
    }

    @Override
    protected String bo() {
        if (custom)
            return null;
        else
            return super.bo();
    }

    @Override
    protected String bp() {
        if (custom)
            return null;
        else
            return super.bp();
    }
}
