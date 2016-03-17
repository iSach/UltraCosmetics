package be.isach.ultracosmetics.cosmetics.morphs.customentities.v1_8_R3;

import be.isach.ultracosmetics.cosmetics.morphs.MorphType;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

/**
 * Created by Sacha on 19/12/15.
 */
public class CustomGuardian_1_8_R3 extends EntityGuardian {

    private boolean custom;

    public CustomGuardian_1_8_R3(World world) {
        super(world);
    }

    public void check() {
        custom = MorphType.customEntities.contains(this);
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
