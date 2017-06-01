package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.menu.ClickRunnable;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Morph {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuMorphs extends CosmeticMenu<MorphType> {

	public MenuMorphs(UltraCosmetics ultraCosmetics) {
		super(ultraCosmetics, Category.MORPHS);
	}

	@Override
	protected void putItems(Inventory inventory, UltraPlayer player, int page) {
		putSelfViewItem(inventory, player);
	}

	private void putSelfViewItem(Inventory inventory, UltraPlayer player) {
		int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 5 : 6);
		MaterialData materialData;
		boolean toggle;
		if (player.canSeeSelfMorph()) {
			materialData = ItemFactory.createFromConfig("Categories.Self-View-Item.When-Enabled");
			toggle = false;
		} else {
			materialData = ItemFactory.createFromConfig("Categories.Self-View-Item.When-Disabled");
			toggle = true;
		}
		String msg = MessageManager.getMessage((toggle ? "Enable" : "Disable") + "-Third-Person-View");
		ClickRunnable run = data -> {
			player.setSeeSelfMorph(!player.canSeeSelfMorph());
			putSelfViewItem(inventory, player);
		};
		putItem(inventory, slot, ItemFactory.create(materialData.getItemType(), materialData.getData(), msg), run);
	}

	@Override
	protected ItemStack filterItem(ItemStack itemStack, MorphType cosmeticType, UltraPlayer player) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		String loreMsg = null;
		if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
			loreMsg = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig()
			                                                                                    .get("No-Permission.Lore-Message-" + ((player.hasPermission(cosmeticType.getPermission()) ? "Yes" : "No")))));
		}
		List<String> lore = new ArrayList<>();
		if (cosmeticType.showsDescription()) {
			lore.add("");
			lore.addAll(cosmeticType.getDescription());
		}
		if (lore != null) {
			lore.add("");
			lore.add(loreMsg);
		}
		lore.add("");
		lore.add(cosmeticType.getSkill());
		lore.add("");
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	public List<MorphType> enabled() {
		return MorphType.enabled();
	}

	@Override
	protected void toggleOn(UltraPlayer ultraPlayer, MorphType morphType, UltraCosmetics ultraCosmetics) {
		morphType.equip(ultraPlayer, ultraCosmetics);
	}

	@Override
	protected void toggleOff(UltraPlayer ultraPlayer) {
		ultraPlayer.removeMorph();
	}

	@Override
	protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
		return ultraPlayer.getCurrentMorph();
	}
}
