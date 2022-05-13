package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

/**
 * Created by sacha on 11/01/17.
 */
// Handles mounts that are real horses, but also variant horses on 1.8
// For variant horses on 1.9+ see MountAbstractHorse
public abstract class MountHorse extends Mount {

    public MountHorse(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, type, ultraCosmetics);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setupEntity() {
        Horse horse = (Horse) entity;
        // setColor has no effect on variant horses so skip if it's a variant horse
        if (getVariant() == null) {
            horse.setColor(getColor());
        } else {
            // Setting variant twice makes it work better with ViaVersion
            horse.setVariant(getVariant());
            horse.setVariant(getVariant());
        }
        horse.setTamed(true);
        horse.setDomestication(1);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setJumpStrength(0.7);
    }

    abstract protected Horse.Color getColor();

    @SuppressWarnings("deprecation")
    protected Horse.Variant getVariant() {
        return null;
    }
}
