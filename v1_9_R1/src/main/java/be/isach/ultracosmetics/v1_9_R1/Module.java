package be.isach.ultracosmetics.v1_9_R1;

import be.isach.ultracosmetics.v1_9_R1.customentities.CustomEntities;
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
