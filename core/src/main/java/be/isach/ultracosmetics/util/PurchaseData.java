package be.isach.ultracosmetics.util;

import org.bukkit.inventory.ItemStack;

/**
 * Created by sacha on 04/04/2017.
 */
public class PurchaseData {
	
	private int price;
	private Runnable onPurchase;
	private ItemStack showcaseItem;
	
	public int getPrice() {
		return price;
	}
	
	public ItemStack getShowcaseItem() {
		return showcaseItem;
	}
	
	public Runnable getOnPurchase() {
		return onPurchase;
	}
	
	public void setOnPurchase(Runnable onPurchase) {
		this.onPurchase = onPurchase;
	}
	
	public void setShowcaseItem(ItemStack showcaseItem) {
		this.showcaseItem = showcaseItem;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
}

