package be.isach.ultracosmetics.v1_8_R2;

import be.isach.ultracosmetics.v1_8_R2.customentities.CustomEntities;
import be.isach.ultracosmetics.version.IModule;

public class Module implements IModule{
    @Override
    public void enable() {
        CustomEntities.registerEntities();
    }

    @Override
    public void disable() {
        CustomEntities.unregisterEntities();
    }
}
