package be.isach.ultracosmetics.menu;

import org.bukkit.inventory.ItemStack;

/**
 * Created by sacha on 04/04/2017.
 */
public class PurchaseData {

    private int price;
    private Runnable onPurchase;
    private Runnable onCancel;
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

    public Runnable getOnCancel() {
        return onCancel;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setOnPurchase(Runnable onPurchase) {
        this.onPurchase = onPurchase;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public void setShowcaseItem(ItemStack showcaseItem) {
        this.showcaseItem = showcaseItem;
    }

}
