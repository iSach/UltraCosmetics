package be.isach.ultracosmetics.v1_13_R2;

import be.isach.ultracosmetics.version.IItemGlower;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author RadBuilder
 */
public class ItemGlower implements IItemGlower {
	
	public ItemStack glow(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemMeta);
		return item;
	}
}
