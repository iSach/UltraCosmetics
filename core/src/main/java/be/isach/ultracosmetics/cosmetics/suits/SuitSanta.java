package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Represents an instance of a santa suit summoned by a player.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SuitSanta extends Suit {
    public SuitSanta(UltraPlayer owner, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(owner, suitType, ultraCosmetics);
    }

    @Override
    protected void setupItemStack() {
        super.setupItemStack();

        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(Color.fromRGB(255, 0, 0));
        itemStack.setItemMeta(itemMeta);
    }
}
