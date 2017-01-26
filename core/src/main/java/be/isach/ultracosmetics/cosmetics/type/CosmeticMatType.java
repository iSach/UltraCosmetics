package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import org.bukkit.Material;

/**
 * A cosmetic material type.
 * 
 * @author 	iSach
 * @since 	08-04-2016
 */
public class CosmeticMatType<T extends Cosmetic> extends CosmeticType<T> {

    private Material material;
    private byte data;

    public CosmeticMatType(Category category, String configName, String permission,
                           String description, Material material, byte data, Class clazz) {
        super(category, configName, permission, description, clazz);
        this.material = material;
        this.data = data;
    }

    public Material getMaterial() {
        return material;
    }

    public byte getData() {
        return data;
    }
}
