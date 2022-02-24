package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SuitFrozen extends Suit {
    public SuitFrozen(UltraPlayer owner, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(owner, suitType, ultraCosmetics);
    }

    @Override
    public void setupItemStack() {
        super.setupItemStack();
        if (getArmorSlot() == ArmorSlot.HELMET) return;
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(Color.AQUA);
        itemStack.setItemMeta(itemMeta);
    }
}
