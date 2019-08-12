package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.UCMaterial;
import org.bukkit.Material;

/**
 * A cosmetic material type.
 *
 * @author iSach
 * @since 08-04-2016
 */
public abstract class CosmeticMatType<T extends Cosmetic> extends CosmeticType<T> {
	private UCMaterial material;
	
	public CosmeticMatType(Category category, String configName, String permission, String description, UCMaterial material, Class clazz, ServerVersion baseVersion) {
		super(category, configName, permission, description, clazz, baseVersion);
		this.material = material;
	}
	
	public UCMaterial getMaterial() {
		return material;
	}
}
