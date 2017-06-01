package be.isach.ultracosmetics.v1_12_R1.nms;

import java.lang.reflect.Field;

/**
 * @author RadBuilder
 */
public abstract class WrapperBase {

    protected Object handle;

    public WrapperBase(Object handle) {
        this.handle = handle;
    }

    public <T> T getField(String name, Class<?> fieldClass, Class<T> clazz) {
        T value = null;

        try {
            Field field = fieldClass.getDeclaredField(name);
            field.setAccessible(true);

            return clazz.cast(field.get(handle));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return value;
    }

    public <T> void setField(String name, Class<?> fieldClass, T value) {
        try {
            Field field = fieldClass.getDeclaredField(name);
            field.setAccessible(true);

            field.set(handle, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object getHandle() { return handle; }

}
