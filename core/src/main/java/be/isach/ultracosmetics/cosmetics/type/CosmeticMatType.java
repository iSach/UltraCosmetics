package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.XMaterial;

/**
 * A cosmetic material type.
 *
 * @author iSach
 * @since 08-04-2016
 */
public abstract class CosmeticMatType<T extends Cosmetic> extends CosmeticType<T> {
    private XMaterial material;

    public CosmeticMatType(Category category, String configName, String permission, String description, XMaterial material, Class clazz, ServerVersion baseVersion) {
        super(category, configName, permission, description, clazz, baseVersion);
        this.material = material;
    }

    public XMaterial getMaterial() {
        return material;
    }
}
