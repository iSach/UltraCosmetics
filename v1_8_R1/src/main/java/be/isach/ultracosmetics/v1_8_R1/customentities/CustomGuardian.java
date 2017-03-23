package be.isach.ultracosmetics.v1_8_R1.customentities;

import net.minecraft.server.v1_8_R1.EntityGuardian;
import net.minecraft.server.v1_8_R1.LocaleI18n;
import net.minecraft.server.v1_8_R1.World;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

import be.isach.ultracosmetics.v1_8_R1.morphs.MorphElderGuardian;

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
    public String getName() {
        return LocaleI18n.get("entity.Guardian.name");
    }

    @Override
    public void s_() {
        if (!custom)
            super.s_();
        else
            setHealth(getMaxHealth());
    }

    @Override
    protected String bn() {
        if (custom)
            return null;
        else
            return super.bn();
    }

    @Override
    protected String bo() {
        if (custom)
            return null;
        else
            return super.bo();
    }
}
