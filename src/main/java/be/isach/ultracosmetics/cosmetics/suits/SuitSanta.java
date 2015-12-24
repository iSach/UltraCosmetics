package be.isach.ultracosmetics.cosmetics.suits;

import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

/**
 * Created by Sacha on 20/12/15.
 */
public class SuitSanta extends Suit {

    public SuitSanta(UUID owner, ArmorSlot armorSlot) {
        super(owner, armorSlot, SuitType.SANTA);

        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(Color.fromRGB(255, 0, 0));
        itemStack.setItemMeta(itemMeta);
    }

}
