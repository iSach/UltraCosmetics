package be.isach.ultracosmetics.v1_9_R2.customentities;

import be.isach.ultracosmetics.v1_9_R2.morphs.MorphElderGuardian;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

/**
 * Created by Sacha on 19/12/15.
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
        getDataWatcher().set(DataWatcherRegistry.c.a(17), (float) (armorStand == null ? 0 : ((CraftArmorStand) armorStand).getHandle().getId()));
//        getDataWatcher().set(armorStand == null ? 0 : ((CraftArmorStand) armorStand).getHandle().getId(), 17);
    }

    @Override
    protected SoundEffect G() {
        if (custom) return null;
        else return super.G();
    }

    @Override
    protected SoundEffect bT() {
        if (custom) return null;
        else return super.bT();
    }

    @Override
    protected SoundEffect bS() {
        if (custom) return null;
        else return super.bS();
    }

    @Override
    public String getName() {
        return LocaleI18n.get("entity.Guardian.name");
    }

    @Override
    public void m() {
        if (!custom) super.m();
        else setHealth(getMaxHealth());
    }
}
