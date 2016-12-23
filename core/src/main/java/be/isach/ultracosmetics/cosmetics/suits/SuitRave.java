package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Created by Sacha on 20/12/15.
 */
public class SuitRave extends Suit {

    private int[] colors = new int[]{255, 0, 0};

    public SuitRave(UltraPlayer owner, ArmorSlot armorSlot, UltraCosmetics ultraCosmetics) {
        super(owner, armorSlot, SuitType.RAVE, ultraCosmetics);
    }

    protected void onUpdate() {
        if (colors[0] == 255 && colors[1] < 255 && colors[2] == 0)
            colors[1] += 15;
        if (colors[1] == 255 && colors[0] > 0 && colors[2] == 0)
            colors[0] -= 15;
        if (colors[1] == 255 && colors[2] < 255 && colors[0] == 0)
            colors[2] += 15;
        if (colors[2] == 255 && colors[1] > 0 && colors[0] == 0)
            colors[1] -= 15;
        if (colors[2] == 255 && colors[0] < 255 && colors[1] == 0)
            colors[0] += 15;
        if (colors[0] == 255 && colors[2] > 0 && colors[1] == 0)
            colors[2] -= 15;

        refresh();

    }

    private void refresh() {
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(Color.fromRGB(colors[0], colors[1], colors[2]));
        itemStack.setItemMeta(itemMeta);

        switch (getArmorSlot()) {
            case HELMET:
                getPlayer().getInventory().setHelmet(itemStack);
                break;
            case CHESTPLATE:
                getPlayer().getInventory().setChestplate(itemStack);
                break;
            case LEGGINGS:
                getPlayer().getInventory().setLeggings(itemStack);
                break;
            case BOOTS:
                getPlayer().getInventory().setBoots(itemStack);
                break;
        }
    }

    @Override
    protected void onEquip() {

    }
}
